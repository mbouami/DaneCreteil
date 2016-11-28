package com.creteil.com.danecreteil.app.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohammed on 27/11/2016.
 */

public class JSONParser {

    public String parse(URL url, String method) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String line = null;
        urlConnection.setRequestMethod(method);
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }
        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            return null;
        }
        try {
            return buffer.toString();
        }
        finally {
            urlConnection.disconnect();
            reader.close();
        }
    }

    public ArrayList<Map<String, String>> getVilleDataFromJson(String villeJsonStr, String depart) throws JSONException {

        final String OWM_DEPART = depart;
        final String OWM_ID= "id";
        final String OWM_NOM= "nom";
        final String OWM_DISTRICT= "district";
        final String OWM_CP= "cp";
        JSONObject villeJson = new JSONObject(villeJsonStr);
        JSONArray villeArray = villeJson.getJSONArray(OWM_DEPART);
        ArrayList<Map<String, String>> resultStrs = new ArrayList<Map<String, String>>();
        for(int i = 0; i < villeArray.length(); i++) {
            JSONObject laville = villeArray.getJSONObject(i);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", laville.getString(OWM_ID));
            map.put("nom", laville.getString(OWM_NOM));
            resultStrs.add(map);
        }
        return resultStrs;
    }

}
