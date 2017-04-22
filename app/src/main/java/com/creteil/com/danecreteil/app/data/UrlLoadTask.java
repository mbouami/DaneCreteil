package com.creteil.com.danecreteil.app.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mohammed on 19/04/2017.
 */

public class UrlLoadTask extends AsyncTask<byte[], Void, Void> {
    private final String LOG_TAG = UrlLoadTask.class.getSimpleName();
    private final Context mContext;
    private URL mUrl = null;
    private boolean DEBUG = true;
    ProgressDialog pDialog;
    HttpURLConnection murlConnection;
    InputStream inputStream;
    OutputStream outputStream;

    public UrlLoadTask(Context mContext, String url) throws MalformedURLException {
        this.mContext = mContext;
        this.mUrl = new URL(url);
    }


    @Override
    protected Void doInBackground(byte[]... params) {
        String resultat = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            murlConnection = (HttpURLConnection) mUrl.openConnection();
            murlConnection.setRequestMethod("POST");
            murlConnection.setDoOutput(true);
            murlConnection.setChunkedStreamingMode(0);
//            murlConnection.setRequestProperty("Connection", "Keep-Alive");
//            murlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//            writeToFile( params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            OutputStream sortie = murlConnection.getOutputStream();
//            DataOutputStream dataOutputStream = new DataOutputStream(sortie);
//            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
//            String taskId = "3";
//            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"id\"" + lineEnd);
//            dataOutputStream.writeBytes("Content-Type: text/plain;charset=UTF-8" + lineEnd);
//            dataOutputStream.writeBytes("Content-Length: " + taskId.length() + lineEnd);
//            dataOutputStream.writeBytes(lineEnd);
//            dataOutputStream.writeBytes(taskId + lineEnd);
//            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
//            dataOutputStream.flush();
//            dataOutputStream.close();
            outputStream = new BufferedOutputStream(sortie);
            writeStream(outputStream);
            inputStream = new BufferedInputStream(murlConnection.getInputStream());
            resultat = readStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            murlConnection.disconnect();
        }
        Log.w(LOG_TAG,"resultat : " + resultat);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Transfert des données en cours. Merci de patienter...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pDialog.dismiss();
    }


    private String readStream(InputStream inputStream) {
        String resultat = null;
        BufferedReader reader;
        StringBuffer buffer;
        buffer = new StringBuffer();
        String line;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultat = buffer.toString();
        return resultat;
    }

    private void writeStream(OutputStream out){
        String output = "Hello world";

        try {
            out.write(output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(byte[] data) {
        try {
//            String base64photo = Base64.encodeToString(data, Base64.DEFAULT);
            String base64photo = Base64.encodeToString(data, Base64.NO_WRAP);
            FileOutputStream fileOutputStream = new FileOutputStream("photo.jpg");
            fileOutputStream.write(data);
            fileOutputStream.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
