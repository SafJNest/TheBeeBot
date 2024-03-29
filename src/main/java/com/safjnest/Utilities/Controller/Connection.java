package com.safjnest.Utilities.Controller;

import java.net.InetSocketAddress;
import net.dv8tion.jda.api.JDA;

import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.SQL.DatabaseHandler;

public class Connection extends WebSocketServer {
    
    private JDA jda;
    private static int TCP_PORT = 8096;
    private Postman postman;
    private GuildSettings gs;
    
    public Connection(JDA jda, GuildSettings gs){
        super(new InetSocketAddress(TCP_PORT));
        this.jda = jda;
        this.gs = gs;
        this.postman = new Postman(jda, gs);


    }

    public void willBeRemovedSoon(){
        /**
       * Smurfing
       */
      jda.getClass();
      gs.doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed();
  }
    
    @Override
    public void onStart() {
        System.out.println("[Beebot] INFO Connection thread started -> " + TCP_PORT);
    }

    @Override
    public void onOpen(org.java_websocket.WebSocket conn, ClientHandshake handshake) {
        //System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(org.java_websocket.WebSocket conn, int code, String reason, boolean remote) {
        //System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(org.java_websocket.WebSocket conn, String message) {
        System.out.println("RECEIVED: " + message);
        String request = parseRequest(message, "request");
        String guildId = parseRequest(message, "guildId");
        String userId = parseRequest(message, "userId");
        String server = "";
        switch (request){
            case "server_list":
                server = "server_list-" + postman.getServerList(userId, parseRequest(message, "ids"));
                break;
            case "getHomeStats":
                server = "getHomeStats-" + postman.getHomeStats();
                break;
            case "getPrefix":
                server = "getPrefix-" + postman.getPrefix(guildId, userId);
                break;
            case "newPrefix":
                try {
                    gs.getServer(guildId).setPrefix(parseRequest(message, "prefix"));
                    DatabaseHandler.insertGuild(guildId, parseRequest(message, "prefix"));
                    server = "newPrefix-ok"; 
                } catch (Exception e) {
                    server = "newPrefix-!ok"; 
                }
                break;
            default:
                server = "UNKNOWN COMMAND";
                break;
        }
        System.out.println("SENT: " + server);
        conn.send(server);
    }

    @Override
    public void onError(org.java_websocket.WebSocket conn, Exception ex) {
        System.out.println("Error from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        ex.printStackTrace();
        conn.close();
    }


    public String parseRequest(String json, String obj) {
        JSONParser jsonParser = new JSONParser();
        Object object= null;
        try {
            object = jsonParser.parse(json);
            JSONObject jsonObject = (JSONObject) object;
            return (String) jsonObject.get(obj);
        } catch (ParseException e) {
            return "?";
        }
 
    }
    
}
