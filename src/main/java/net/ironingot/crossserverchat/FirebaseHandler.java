package net.ironingot.crossserverchat;

import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.util.Map;
import java.util.HashMap;

public class FirebaseHandler {
    private CrossServerChat plugin;
    private static final String codec = "UTF-8";

    public FirebaseHandler(CrossServerChat plugin) {
        this.plugin = plugin;

        /*
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", plugin.getConfigHandler().getServerIdentify());

        TokenGenerator tokenGenerator = new TokenGenerator(plugin.getConfigHandler().getFirestoreAuthKey());
        String token = tokenGenerator.createToken(payload);

        CrossServerChat.logger.info("token:" + token);
        */
        sendMessage();
    }

    public void sendMessage() {
        String URL = this.plugin.getConfigHandler().getFirestoreURL() + "?key=" + this.plugin.getConfigHandler().getFirestoreAuthKey();
        CrossServerChat.logger.info(URL);
        CrossServerChat.logger.info(this.post(URL, "{ 'fields': { sender: 'fubira', message: 'test' } }"));
    }

    public void readMessage() {
    }

    protected String post(String urlString, String data) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=" + codec);
            connection.connect();

            OutputStreamWriter output = new OutputStreamWriter(new BufferedOutputStream(connection.getOutputStream()));
            output.write(data);
            output.close();

            bufferedReader =
                new BufferedReader(new InputStreamReader(connection.getInputStream(), codec));

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
}
