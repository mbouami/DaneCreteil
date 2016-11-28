package com.creteil.com.danecreteil.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.creteil.com.danecreteil.app.data.DaneContract.VilleEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Mohammed on 28/11/2016.
 */

public class FetchVillesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchVillesTask.class.getSimpleName();

    private final Context mContext;
    private boolean DEBUG = true;

    public FetchVillesTask(Context context) {
        mContext = context;
    }

//    long addLocation(String locationSetting, String cityName, double lat, double lon) {
//        long locationId;
//
//        // First, check if the location with this city name exists in the db
//        Cursor locationCursor = mContext.getContentResolver().query(
//                WeatherContract.LocationEntry.CONTENT_URI,
//                new String[]{WeatherContract.LocationEntry._ID},
//                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
//                new String[]{locationSetting},
//                null);
//
//        if (locationCursor.moveToFirst()) {
//            int locationIdIndex = locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
//            locationId = locationCursor.getLong(locationIdIndex);
//        } else {
//            // Now that the content provider is set up, inserting rows of data is pretty simple.
//            // First create a ContentValues object to hold the data you want to insert.
//            ContentValues locationValues = new ContentValues();
//
//            // Then add the data, along with the corresponding name of the data type,
//            // so the content provider knows what kind of value is being inserted.
//            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
//            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
//            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
//            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);
//
//            // Finally, insert location data into the database.
//            Uri insertedUri = mContext.getContentResolver().insert(
//                    WeatherContract.LocationEntry.CONTENT_URI,
//                    locationValues
//            );
//
//            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
//            locationId = ContentUris.parseId(insertedUri);
//        }
//
//        locationCursor.close();
//        // Wait, that worked?  Yes!
//        return locationId;
//    }

    private void getVillesDataFromJson(String villesJsonStr,String departementSetting)
            throws JSONException {

        final String OWM_DEPART = departementSetting;
        final String OWM_ID= "id";
        final String OWM_NOM= "nom";
        final String OWM_DISTRICT= "district";
        final String OWM_CP= "cp";
        final String OWM_VILLE_ID = "id";
        try {
            JSONObject villeJson = new JSONObject(villesJsonStr);
            JSONArray villeArray = villeJson.getJSONArray(OWM_DEPART);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(villeArray.length());
            for(int i = 0; i < villeArray.length(); i++) {
                JSONObject laville = villeArray.getJSONObject(i);
                ContentValues villesValues = new ContentValues();
                villesValues.put(VilleEntry.COLUMN_NOM, laville.getString(OWM_NOM));
                villesValues.put(VilleEntry.COLUMN_CODE_DEPARTEMENT, OWM_DEPART);
                villesValues.put(VilleEntry.COLUMN_NOM_DEPARTEMENT, "");
                villesValues.put(VilleEntry.COLUMN_VILLE_BASE_ID, laville.getString(OWM_ID));
                cVVector.add(villesValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(VilleEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchVillesTask Complete. " + inserted + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    @Override
    protected Void doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String departementQuery = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String vcillesJsonStr = null;

        String format = "json";
        try {
            final String VILLES_BASE_URL ="http://www.bouami.fr/gestionetabs/web/listevilles/";

            Uri builtUri = Uri.parse(VILLES_BASE_URL).buildUpon()
                    .appendPath(departementQuery)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            vcillesJsonStr = buffer.toString();
            getVillesDataFromJson(vcillesJsonStr, departementQuery);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}
