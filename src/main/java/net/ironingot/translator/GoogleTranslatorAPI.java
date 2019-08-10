package net.ironingot.translator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GoogleTranslatorAPI {
    private static final String baseURL = "http://www.google.com/transliterate";
    private static final String codec = "UTF-8";

    public static String translate(String text, String from, String to) {
        String encodedText = null;
        try {
            encodedText = URLEncoder.encode(text, codec);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String urlString = baseURL + "?langpair=" + from + "|" + to + "&text=" + encodedText;
        return pickupFirstCandidate(callWebAPI(urlString));
   }

   private static String pickupFirstCandidate(String response) {
        StringBuilder stringBuilder = new StringBuilder();
        JSONParser parser = new JSONParser();

        try {
            JSONArray responseArray = (JSONArray)parser.parse(response);

            for (int id = 0; id < responseArray.size(); id++) {
                String partString = "";
                try {
                    JSONArray partArray = (JSONArray)responseArray.get(id);
                    partString = (String)partArray.get(0);
                    partString = (String)((JSONArray)partArray.get(1)).get(0);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                stringBuilder.append(partString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (stringBuilder.length() == 0) {
            return "";
        }

        return stringBuilder.toString();
    }

    private static String callWebAPI(String urlString) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

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
                if (bufferedReader == null) {
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
