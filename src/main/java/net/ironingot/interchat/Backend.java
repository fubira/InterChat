package net.ironingot.interchat;

import net.ironingot.interchat.message.IMessageBroadcastor;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Backend {
    private static final String codec = "UTF-8";

    private String backendUrl = "";
    private String backendAuthKey = "";
    private long lastTime;

    public Backend(String url, String authKey) {
        this.backendUrl = url;
        this.backendAuthKey = authKey;
        this.lastTime = System.currentTimeMillis();
    }

    protected static String getRest(String urlString) {
        return Backend.callRest(urlString, "GET", null);
    }

    protected static String postRest(String urlString, String data) {
        return Backend.callRest(urlString, "POST", data);
    }

    private static String callRest(String urlString, String request, String data) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        OutputStreamWriter outputStreamWriter = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request);
            if (request == "POST") {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                outputStreamWriter = new OutputStreamWriter(new BufferedOutputStream(connection.getOutputStream()));
                outputStreamWriter.write(data);
                outputStreamWriter.close();
                outputStreamWriter = null;
            }
            connection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), codec));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            } catch (IOException e) {
            }
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return stringBuilder.toString();
    }

    private static JSONObject parseResponse(String response) {
        JSONObject responseObject = null;
        try {
            responseObject = new JSONObject(response);
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    public void postMessage(final Map<String, Object> data) {
        JSONObject json = new JSONObject(data);
        InterChatPlugin.logger.info("Post data: " + json.toString());
        String response = Backend.postRest(this.backendUrl + "/post", json.toString());
        InterChatPlugin.logger.info("Post Response: " + response);
    }

    public void receiveMessage(final IMessageBroadcastor broadcastor) {
        String response = Backend.getRest(this.backendUrl + "/message?from=" + lastTime);
        InterChatPlugin.logger.info("Message Response: " + response);

        JSONObject responseJson = parseResponse(response);
        if ((String)responseJson.get("result") != "ok") {
            InterChatPlugin.logger.warning("Backend.receiveMessage: bad response: " + response);
            return;
        }

        Long newTime = responseJson.getLong("time");
        this.lastTime = this.lastTime > newTime ? this.lastTime : newTime;
        
        try {
            JSONArray messages = responseJson.getJSONArray("messages");
            for (Object value: messages) {
                broadcastor.broadcast(new JSONObject(value).toMap());
            }
        }
        catch (JSONException e) {}
        
    }
}
