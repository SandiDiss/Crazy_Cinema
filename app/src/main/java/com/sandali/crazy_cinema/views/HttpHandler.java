package com.sandali.crazy_cinema.views;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler {
    public String HttpHandlerRequest(String keyword) {
        String api_url = "http://www.omdbapi.com/?apikey=";
        String API_KEY = "2f9c3869";
        try {
            URL url = new URL(api_url + API_KEY+"&s=" + keyword);


            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
