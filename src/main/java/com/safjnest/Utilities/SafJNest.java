package com.safjnest.Utilities;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * Classe ufficiale della <a href="https://github.com/SafJNest">SafJNest Corporation</a>
 * <p>
 * Nella classe sono presenti alcuni dei metodi piu' importanti e significativi.
 */
public class SafJNest extends Thread {

    public static void loadingBee(int speed) {
        printDbmrStyle("\033[46;30m "
                + "                                                      __            \n"
                + "                                                      // \\           \n"
                + "                                                      \\\\_/ //        \n"
                + "                                     '-.._.-''-.._.. -(||)(')        \n"
                + "                                                       '''           ", speed);
        printDbmrStyle(""
                + "\033[90m███████╗\033[93m █████╗ \033[90m███████╗\033[93m   ██╗    \033[90m███╗   ██╗\033[93m███████╗\033[90m███████╗\033[93m████████╗\033[40m\n"
                + "\033[90m██╔════╝\033[93m██╔══██╗\033[90m██╔════╝\033[93m   ██║    \033[90m████╗  ██║\033[93m██╔════╝\033[90m██╔════╝\033[93m╚══██╔══╝\n"
                + "\033[90m███████╗\033[93m███████║\033[90m█████╗  \033[93m   ██║    \033[90m██╔██╗ ██║\033[93m█████╗  \033[90m███████╗\033[93m   ██║   \n"
                + "\033[90m╚════██║\033[93m██╔══██║\033[90m██╔══╝\033[93m██   ██║    \033[90m██║╚██╗██║\033[93m██╔══╝  \033[90m╚════██║\033[93m   ██║   \n"
                + "\033[90m███████║\033[93m██║  ██║\033[90m██║   \033[93m╚█████╔╝    \033[90m██║ ╚████║\033[93m███████╗\033[90m███████║\033[93m   ██║   \n"
                + "\033[90m╚══════╝\033[93m╚═╝  ╚═╝\033[90m╚═╝   \033[93m ╚════╝     \033[90m╚═╝  ╚═══╝\033[93m╚══════╝\033[90m╚══════╝\033[93m   ╚═╝\n\033[0m");
    }

    public static void bee(){
        System.out.println("\033[46;30m "
                       + "                                                      __            \n"
                       + "                                                      // \\           \n"
                       + "                                                      \\\\_/ //        \n"
                       + "                                     '-.._.-''-.._.. -(||)(')        \n"
                       + "                                                       '''           ");
        System.out.println(""
                       + "\033[90m███████╗\033[93m █████╗ \033[90m███████╗\033[93m   ██╗    \033[90m███╗   ██╗\033[93m███████╗\033[90m███████╗\033[93m████████╗\033[40m\n"
                       + "\033[90m██╔════╝\033[93m██╔══██╗\033[90m██╔════╝\033[93m   ██║    \033[90m████╗  ██║\033[93m██╔════╝\033[90m██╔════╝\033[93m╚══██╔══╝\n"
                       + "\033[90m███████╗\033[93m███████║\033[90m█████╗  \033[93m   ██║    \033[90m██╔██╗ ██║\033[93m█████╗  \033[90m███████╗\033[93m   ██║   \n"
                       + "\033[90m╚════██║\033[93m██╔══██║\033[90m██╔══╝\033[93m██   ██║    \033[90m██║╚██╗██║\033[93m██╔══╝  \033[90m╚════██║\033[93m   ██║   \n"
                       + "\033[90m███████║\033[93m██║  ██║\033[90m██║   \033[93m╚█████╔╝    \033[90m██║ ╚████║\033[93m███████╗\033[90m███████║\033[93m   ██║   \n"
                       + "\033[90m╚══════╝\033[93m╚═╝  ╚═╝\033[90m╚═╝   \033[93m ╚════╝     \033[90m╚═╝  ╚═══╝\033[93m╚══════╝\033[90m╚══════╝\033[93m   ╚═╝\n\033[0m");
    }

    public static void printDbmrStyle(String line) {
        printDbmrStyle(line, 15);
    }

    public static void printDbmrStyle(String line, int speed) {
        try {
            for (char c : line.toCharArray()) {
                System.out.print(c);
                Thread.sleep(speed);
            }
            System.out.println();
        } catch (Exception e) {
        }
    }

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }
    
    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public static boolean intIsParsable(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public static boolean longIsParsable(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }
    
    public static int divideandconquer(int n) {
        n = abs(n);
        if (n < 100000) {
            if (n < 100) {
                if (n < 10)
                    return 1;
                else
                    return 2;
            } else {
                if (n < 1000)
                    return 3;
                else {
                    if (n < 10000)
                        return 4;
                    else
                        return 5;
                }
            }
        } else {
            if (n < 10000000) {
                if (n < 1000000)
                    return 6;
                else
                    return 7;
            } else {
                if (n < 100000000)
                    return 8;
                else {
                    if (n < 1000000000)
                        return 9;
                    else
                        return 10;
                }
            }
        }
    }

    public static int abs(int x) {
        return (x > 0) ? x : -x;
    }

    public static double abs(double x) {
        return x>0?x:-x;
    }


    /**
     * @Deprecated
     * 
     */
    public static BigInteger randomBighi(int numBits) {
        if (numBits < 2)
            throw new IllegalArgumentException("SafJNest doesnt like 0 or negative numbers");
        numBits--;
        int numBytes = (int) (((long) numBits + 7) / 8);
        byte[] randomBits = new byte[numBytes];
        if (numBytes > 0) {
            try {
                SecureRandom.getInstanceStrong().nextBytes(randomBits);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            int excessBits = 8 * numBytes - numBits;
            randomBits[0] &= (1 << (8 - excessBits)) - 1;
        }
        return new BigInteger(1, randomBits).add(BigInteger.TWO.pow(numBits));
    }

    /**
     * @Deprecated
     * 
     */
    public static boolean isPrime(BigInteger p) {
        return (BigInteger.TWO.modPow(p, p).compareTo(BigInteger.TWO) == 0);
    }

    /**
     * @Deprecated
     * 
     */
    public static BigInteger getFirstPrime(BigInteger n) {
        if(n.equals(BigInteger.TWO))
            return n;
        if(n.mod(BigInteger.TWO).equals(BigInteger.ZERO))
            n = n.add(BigInteger.ONE);
        while(!isPrime(n))
            n = n.add(BigInteger.TWO);
        return n;
    }

    public static float fastInvSquareRoot(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= (1.5f - xhalf * x * x);
        return x;
    }

    public static String checkEmoji(String msg) {
        msg = msg.replaceAll("<3", new StringBuilder().appendCodePoint(0x1F497).toString());
        msg = msg.replaceAll(":143:", new StringBuilder().appendCodePoint(0x1F618).toString());
        msg = msg.replaceAll(":pantano:", new StringBuilder().appendCodePoint(0x1F62C).toString());
        msg = msg.replaceAll(":mario:", new StringBuilder().appendCodePoint(0x1F921).toString());
        msg = msg.replaceAll(":safj:", new StringBuilder().appendCodePoint(0x1F41D).toString());
        msg = msg.replaceAll(":skull:", new StringBuilder().appendCodePoint(0x1F480).toString());
        msg = msg.replaceAll(":sad:", new StringBuilder().appendCodePoint(0x1F614).toString());
        msg = msg.replaceAll(":merio:", new StringBuilder().appendCodePoint(0x1F533).toString());
        msg = msg.replaceAll(":baco:", new StringBuilder().appendCodePoint(0x1F41B).toString());
        msg = msg.replaceAll(":swag:", new StringBuilder().appendCodePoint(0x1F60E).toString());
        msg = msg.replaceAll(":stonks:", new StringBuilder().appendCodePoint(0x1F4C8).toString());
        msg = msg.replaceAll(":diablo:", new StringBuilder().appendCodePoint(0x1F608).toString());
        msg = msg.replaceAll(":deltoide:", new StringBuilder().appendCodePoint(0x00394).toString());
        msg = msg.replaceAll(":squidgame:", (new StringBuilder().appendCodePoint(0x1F991).toString()
                                           + new StringBuilder().appendCodePoint(0x1F3B2).toString()));
        return msg;
    }

    public static BigInteger getRandomBigInteger(int numBits) {
        return new BigInteger(numBits-1, new Random()).add(BigInteger.TWO.pow(numBits-1));
    }

    public static BigInteger getRandomPrime(int numBits) {
        if(numBits < 2)
            return BigInteger.valueOf(-1);

        BigInteger result = SafJNest.getRandomBigInteger(numBits).nextProbablePrime();
        if(result.bitLength() > numBits) {
            result = BigInteger.TWO.pow(numBits - 1).nextProbablePrime();
            if(result.bitLength() > numBits) {
                result = BigInteger.valueOf(-1);
            }
        }
        return result;
    }
    
    public static String getJSalt(int byteNum) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBits(byteNum));
    }

    private static byte[] randomBits(int n) {
        byte bytes[] = new byte[n];
        (new SecureRandom()).nextBytes(bytes);
        return bytes;
    }

    public static String getFormattedDuration(long millis) {
        Duration duration = Duration.ofMillis(millis);
        String formattedTime = String.format("%02d", duration.toHoursPart()) 
                                            + ":" + String.format("%02d", duration.toMinutesPart()) 
                                            + ":" + String.format("%02d", duration.toSecondsPart()) 
                                            + "s";
        if(formattedTime.startsWith("00:"))
            formattedTime = formattedTime.substring(3);
        if(formattedTime.startsWith("00:"))
            formattedTime = formattedTime.substring(3);
        return formattedTime;
    }

    public static String formatDuration(long milliseconds) {
        Duration duration = Duration.ofMillis(milliseconds);

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else
            return String.format("%02d:%02d", minutes, seconds);

    }
    
    public static double factorial(double n) {
        double fact = 1;
        for (int i = 2; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }

    public static double recursiveFactorial(double n) {
        if(n == 1) return 1;
        return n * factorial(n - 1);
    }


    public static String findSimilarWord(String input, ArrayList<String> arr) {
        double maxSimilarity = 0;
        String s = "";
        
        for (String ss : arr) {
            double similarity = calculateSimilarity(input, ss);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                s = ss;
            }
        }
        
        return s;
    }
    
    private static double calculateSimilarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }
    
    private static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
    
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    public static String getVideoIdFromYoutubeUrl(String youtubeUrl) {
        //Matches possibile Youtube urls.
        String pattern = "(?i)(.*?)(^|\\/|v=)([a-z0-9_-]{11})(.*)?";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeUrl);
        if (matcher.find()) {
            return matcher.group(3);
        }
        return null;
    }

    /**
     * @deprecated
     */
    public static String searchYoutubeVideo(String query, String youtubeApiKey) throws Exception {
        URL theUrl = new URL("https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=1&q=" + query.replace(" ", "+") + "&key=" + youtubeApiKey);
        URLConnection request = theUrl.openConnection();
        request.connect();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new InputStreamReader((InputStream) request.getContent()));
        JSONArray items = (JSONArray) json.get("items");
        JSONObject item = (JSONObject) items.get(0);
        JSONObject id = (JSONObject) item.get("id");
        return (String) id.get("videoId");
    }

    public static int extractSeconds(String youtubeLink) {
        String regex = "[?&]t=(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(youtubeLink);

        if (matcher.find()) {
            String secondsStr = matcher.group(1);
            try {
                return Integer.parseInt(secondsStr);
            } catch (NumberFormatException e) {}
        }
        return -1;
    }
    
}
