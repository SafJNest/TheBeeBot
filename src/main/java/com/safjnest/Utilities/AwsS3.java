package com.safjnest.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import org.apache.commons.io.FileUtils;


/**
 * Class that provides the connection between Beebot and Amazon database.
 * <p>There are just stored all the custom sound.</p>
 * @see com.safjnest.Commands.Audio.PlaySound PlaySound
 * @since 1.3
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 */
public class AwsS3 {
    /**Credentials to connect into the database. Such as {@code Secret key} and {@code Access Key} */
    private AWSCredentials credentials;
    /**Name of the bucket */
    private String bucket;
    /**The effective client that provides the connection */
    private AmazonS3 s3Client;
    /**Object that provides the connection with the {@code PostgreSQL} database. 
     * @see com.safjnest.Utilities.SQL PostgreSQL  
     */
    private SQL sql;

    /**
     * Default constructor
     * @param credentials
     * @param bucket
     * @param sql
     */
    public AwsS3(AWSCredentials credentials, String bucket, SQL sql) {
        this.credentials = credentials;
        this.bucket = bucket;
        this.sql = sql;
    }
    
    /**
     * Initialize the {@link AwsS3#s3Client s3Client} and try to connect to the database.
     * <p>If something goes wrong will be thrown an exception and the bot won't start up.
     */
    public void initialize() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSignerOverride("AWSS3V4SignerType");

        s3Client = AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("s3.us-east-1.amazonaws.com", "us-east-1"))
            .withPathStyleAccessEnabled(true)
            .withClientConfiguration(clientConfiguration)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();

        try {
            s3Client.doesBucketExistV2(bucket);
        } catch (SdkClientException e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("[AWS] INFO Connection Successful!");
    }

    public AmazonS3 getS3Client() {
        return s3Client;
    }

    /**
     * Makes a list of all the files in the bucket.
     * <p>The files are sorted by the first letter of the file name in the lexicographic order.
     * @param prefix
     * @return
     */
    public HashMap<String, ArrayList<String>> listObjectsLexicographic(String prefix) {
        HashMap<String, ArrayList<String>> alpha = new HashMap<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
            .withBucketName(bucket)
            .withPrefix(prefix);
        ObjectListing objectListing;
        do {
            objectListing = s3Client.listObjects(listObjectsRequest);
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                if(!alpha.containsKey(String.valueOf(objectSummary.getKey().split("/")[2].charAt(0)).toUpperCase()))
                    alpha.put(String.valueOf(objectSummary.getKey().split("/")[2].charAt(0)).toUpperCase(), new ArrayList<String>());
                alpha.get(String.valueOf(objectSummary.getKey().split("/")[2].charAt(0)).toUpperCase()).add(objectSummary.getKey().split("/")[2]);
            }
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());
        return alpha;
    }

    /**
     * Makes a list of all the files in the bucket.
     * <p>The files are sorted by the first letter of the file name in the lexicographic order.
     * @param prefix
     * @return
     */
    public ArrayList<String> listObjects(String prefix) {
        ArrayList<String> sounds = new ArrayList<String>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
            .withBucketName(bucket)
            .withPrefix(prefix);
        ObjectListing objectListing;
        do {
            objectListing = s3Client.listObjects(listObjectsRequest);
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) 
                sounds.add(objectSummary.getKey().split("/")[2]);
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());
        return sounds;
    }


    /**
     * Makes a list of all the files in the bucket.
     * <p>The files are sorted by the first letter of the file name in the lexicographic order.
     * @param id
     * @return
     */
    public HashMap<String, ArrayList<String>> listObjects(String prefix, String id) {
        HashMap<String, ArrayList<String>> alpha = new HashMap<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
            .withBucketName(bucket);
        ObjectListing objectListing;
        do {
            objectListing = s3Client.listObjects(listObjectsRequest);
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                if(objectSummary.getKey().split("/")[1].equals(id)) {
                    if(!alpha.containsKey(String.valueOf(objectSummary.getKey().split("/")[2].charAt(0)).toUpperCase()))
                        alpha.put(String.valueOf(objectSummary.getKey().split("/")[2].charAt(0)).toUpperCase(), new ArrayList<String>());
                    alpha.get(String.valueOf(objectSummary.getKey().split("/")[2].charAt(0)).toUpperCase()).add(objectSummary.getKey().split("/")[2]);
                }
            }
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());
        return alpha;
    }

    /**
     * Makes a list of all the files in the bucket.
     * <p>The files are sorted by the first letter of the file name in the lexicographic order.
     * @param id
     * @return
     */
    public HashMap<String, ArrayList<String>> listObjectsByServer(String id, CommandEvent event) {
        HashMap<String, ArrayList<String>> alpha = new HashMap<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
            .withBucketName(bucket);
        ObjectListing objectListing;
        do {
            objectListing = s3Client.listObjects(listObjectsRequest);
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                if(objectSummary.getKey().split("/")[1].equals(id)) {
                    String serverName = "";
                    try {
                        serverName = event.getJDA().getGuildById(objectSummary.getKey().split("/")[0]).getName();
                        
                    } catch (Exception e) {
                        serverName = "ImNotInTheServer";
                    }
                    if(!alpha.containsKey(serverName))
                    alpha.put(serverName, new ArrayList<String>());
                alpha.get(serverName).add(objectSummary.getKey().split("/")[2]);
                }
            }
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());
        return alpha;
    }

    /**
     * Makes a list of all the files in the bucket.
     * <p>The files are sorted by the first letter of the file name in the lexicographic order.
     * @return
     */
    public HashMap<String, ArrayList<String>> listObjectsByServer() {
        HashMap<String, ArrayList<String>> alpha = new HashMap<>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
            .withBucketName(bucket);
        ObjectListing objectListing;
        do {
            objectListing = s3Client.listObjects(listObjectsRequest);
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                if(!alpha.containsKey(objectSummary.getKey().split("/")[0]))
                    alpha.put(objectSummary.getKey().split("/")[0], new ArrayList<String>());
                alpha.get(objectSummary.getKey().split("/")[0]).add(objectSummary.getKey());
            }
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());
        return alpha;
    }

    public String getPrefix(CommandEvent event){
        return event.getGuild().getId() + "/";
    }


    public boolean fileExists(String fileName){
        return s3Client.doesObjectExist(bucket, fileName);
    }

    public S3Object downloadFile(String path, String fileName, CommandEvent event) {
        try {
            S3Object fullObject = s3Client.getObject(
                new GetObjectRequest(bucket, fileName));
            S3ObjectInputStream s3is = fullObject.getObjectContent();
            FileUtils.copyInputStreamToFile(s3is, new File(path + fileName + "." + fullObject.getObjectMetadata().getUserMetaDataOf("format")));
            s3is.close();
            return fullObject;
        } catch (AmazonClientException | IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public S3Object downloadFile(String path, String fileName, SlashCommandEvent event) {
        try {
            S3Object fullObject = s3Client.getObject(
                new GetObjectRequest(bucket, fileName));
            S3ObjectInputStream s3is = fullObject.getObjectContent();
            FileUtils.copyInputStreamToFile(s3is, new File(path + fileName + "." + fullObject.getObjectMetadata().getUserMetaDataOf("format")));
            s3is.close();
            return fullObject;
        } catch (AmazonClientException | IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public S3Object downloadFile(String fileName, CommandEvent event, String newFileName) {
    
        String prefix = getPrefix(event);
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
            .withBucketName(bucket)
            .withPrefix(prefix);
            ObjectListing objectListing;
            do {
                objectListing = s3Client.listObjects(listObjectsRequest);
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    if(objectSummary.getKey().split("/")[2].equalsIgnoreCase(fileName)){
                        prefix = objectSummary.getKey();
                        break;
                    }
            }
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());
            S3Object fullObject = s3Client.getObject(
                new GetObjectRequest(bucket, prefix));
            S3ObjectInputStream s3is = fullObject.getObjectContent();
            FileUtils.copyInputStreamToFile(s3is, new File("rsc" + File.separator + "SoundBoard"+ File.separator + newFileName + "." +fullObject.getObjectMetadata().getUserMetaDataOf("format")));
            s3is.close();
            return fullObject;
        } catch (AmazonClientException | IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void insertIntoFakerGumaKeriaGiuroCheMiStaiSullePalle(){
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucket);
            ObjectListing objectListing;
            do {
                objectListing = s3Client.listObjects(listObjectsRequest);
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    S3Object fullObject = s3Client.getObject(
                        new GetObjectRequest("thebeebox", objectSummary.getKey()));
                    String id;
                    String query = "SELECT id FROM sound WHERE name = '" + objectSummary.getKey().split("/")[2] + "' AND guild_id = '"+fullObject.getObjectMetadata().getUserMetaDataOf("guild")+"';";
                    id = sql.getString(query, "id");
                    s3Client.copyObject("thebeebox", objectSummary.getKey(), "thebeebot", id);
                    try {
                        fullObject.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    
                }
            listObjectsRequest.setMarker(objectListing.getNextMarker());
            } while (objectListing.isTruncated());
            System.out.println("ye");
    }

}
