package net.ironingot.interchat;

import net.ironingot.interchat.message.IMessageBroadcastor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Backend {
    private static final String charset = "UTF-8";

    private String backendUrl = "";
    private String backendAuthKey = "";
    private long lastTime;

    private List<JSONObject> messageCache = new ArrayList<JSONObject>();

    private Thread receiveThread = null;

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
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request);
            if (request == "POST") {
                connection.setRequestProperty("Content-Type", "application/json; charset=" + charset);
                connection.setRequestProperty("Accept", "application/json");
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                connection.getOutputStream().write(data.getBytes(charset));
            }
            connection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));

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

    public void postMessageAsync(final Map<String, Object> data) {
        final Backend self = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject(data);
                Backend.postRest(self.backendUrl + "/post", json.toString());
            }
        }).start();
    }

    public void receiveMessageAsync() {
        final Backend self = this;
        if(this.receiveThread != null && this.receiveThread.isAlive()) {
            return;
        }

        this.receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = Backend.getRest(self.backendUrl + "/message?from=" + lastTime);

                JSONObject responseJson = parseResponse(response);
                if (!"ok".equalsIgnoreCase(responseJson.getString("result"))) {
                    InterChatPlugin.logger.warning("Backend.receiveMessage: bad response: " + response);
                    return;
                }
        
                Long newTime = responseJson.getLong("time") + 1;
                self.lastTime = self.lastTime > newTime ? self.lastTime : newTime;
                
                try {
                    JSONArray messages = responseJson.getJSONArray("messages");
                    for (int i = 0; i < messages.length(); i ++) {
                        JSONObject json = messages.getJSONObject(i);
                        messageCache.add(json);
                    }
                }
                catch (JSONException e) {}
            }
        });
        this.receiveThread.start();
    }

    public void broadcastMessage(final IMessageBroadcastor broadcastor) {
        int count = 0;

        while(!this.messageCache.isEmpty()) {
            JSONObject json = this.messageCache.remove(0);
            broadcastor.broadcast(json.toMap());
            if (++count > 10) break;
        }
    }
}
