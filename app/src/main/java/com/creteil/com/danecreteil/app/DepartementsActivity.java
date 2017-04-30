package com.creteil.com.danecreteil.app;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.creteil.com.danecreteil.app.data.FetchTask;
import com.loopj.android.http.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by BOUAMI on 31/01/2017.
 */

public class DepartementsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = getClass().getSimpleName().toString();
    ListeDepartementsAnimateursAdapter mAdapter;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_LOAD = 2;
    static File mphotoFile = null;
    static String mCurrentPhotoPath;
    static Bitmap mImageBitmap;
    static String mbase64photo;
    static byte[] mimageBytes;
    RequestParams parametres = new RequestParams();
    String  nomFichier;
    Context mContext;
    ProgressDialog pDialog;

    public void setphotoFile(File mphotoFile) {
        this.mphotoFile = mphotoFile;
    }

    public File getphotoFile() {
        return this.mphotoFile;
    }

    private static final String[] ANIMATEURS_PROJECTION = {
            DaneContract.AnimateurEntry._ID,
            DaneContract.AnimateurEntry.COLUMN_NOM,
            DaneContract.AnimateurEntry.COLUMN_TEL,
            DaneContract.AnimateurEntry.COLUMN_EMAIL,
            DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID,
            DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID,
            DaneContract.AnimateurEntry.COLUMN_PHOTO
    };

    private static final String[] DEPARTEMENT_PROJECTION = {
            DaneContract.DepartementEntry.TABLE_NAME + "." + DaneContract.DepartementEntry._ID,
            DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_NOM,
            DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_INTITULE
    };

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 50;
        int targetH = 50;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
//        mImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        mimageBytes = getBytesFromBitmap(mImageBitmap);
        if (mimageBytes != null) {
//            mbase64photo = Base64.encodeToString(mimageBytes, Base64.DEFAULT);
//        mbase64photo = Base64.encodeToString(mimageBytes, Base64.NO_WRAP);
            ContentValues photoanimateur = new ContentValues();
            photoanimateur.put(DaneContract.AnimateurEntry.COLUMN_PHOTO,mimageBytes);
//            Log.w(LOG_TAG,"stream.toByteArray(): "+ mimageBytes.length);
            Uri animateurURI = DaneContract.AnimateurEntry.buildAnimateurs();
            String selection = "("+ DaneContract.AnimateurEntry._ID+ " = ? )";
            String[] selectionArgs = new String[] { mAdapter.getIdAnimateur() };
             getBaseContext().getContentResolver().update(animateurURI,photoanimateur,selection,selectionArgs);
        }
    }

    // convert from bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }


    private void handleCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            File file = new File(mCurrentPhotoPath);
            if (file.delete()) mCurrentPhotoPath = null;
//            mCurrentPhotoPath = null;
        }
    }
//
//    public int uploadFile(String id) {
//
//
//        String fileName = mCurrentPhotoPath;
//
//        HttpURLConnection conn = null;
//        DataOutputStream dos = null;
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1 * 1024 * 1024;
//        int serverResponseCode = 0;
//        File sourceFile = new File(mCurrentPhotoPath);
//
//        if (!sourceFile.isFile()) {
//
////            dialog.dismiss();
////
////            Log.e("uploadFile", "Source File not exist :"
////                    +uploadFilePath + "" + uploadFileName);
////
////            runOnUiThread(new Runnable() {
////                public void run() {
////                    messageText.setText("Source File not exist :"
////                            +uploadFilePath + "" + uploadFileName);
////                }
////            });
//            Log.d(LOG_TAG, "Source File not exist :"+mCurrentPhotoPath );
//            return 0;
//
//        }
//        else
//        {
//            try {
//
//                // open a URL connection to the Servlet
//                FileInputStream fileInputStream = new FileInputStream(sourceFile);
//                URL url = new URL(DaneContract.BASE_URL_UPDATE_PHOTO);
//
//                // Open a HTTP  connection to  the URL
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setDoInput(true); // Allow Inputs
//                conn.setDoOutput(true); // Allow Outputs
//                conn.setUseCaches(false); // Don't use a Cached Copy
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Connection", "Keep-Alive");
//                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
////                conn.setRequestProperty("uploaded_file", fileName);
//                OutputStream sortie = conn.getOutputStream();
//                dos = new DataOutputStream(sortie);
//
//                dos.writeBytes(twoHyphens + boundary + lineEnd);
//                dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\""
//                                + fileName + "\"");
////                dos.writeBytes(lineEnd);
//
//                // create a buffer of  maximum size
//                bytesAvailable = fileInputStream.available();
//
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                buffer = new byte[bufferSize];
//
//                // read file and write it into form...
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                while (bytesRead > 0) {
//
//                    dos.write(buffer, 0, bufferSize);
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                }
//
//                // send multipart form data necesssary after file data...
//                dos.writeBytes(lineEnd);
//                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//                // Responses from the server (code and message)
//                serverResponseCode = conn.getResponseCode();
//                String serverResponseMessage = conn.getResponseMessage();
//
//                Log.d(LOG_TAG, "HTTP Response is : "
//                        + serverResponseMessage + ": " + serverResponseCode);
//
//                if(serverResponseCode == 200){
//
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//
//                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
//                                    +" http://www.androidexample.com/media/uploads/";
////                            Toast.makeText(this, "File Upload Complete.",
////                                    Toast.LENGTH_SHORT).show();
//                            Log.d(LOG_TAG, msg );
//                        }
//                    });
//                }
//
//                //close the streams //
//                fileInputStream.close();
//                dos.flush();
//                dos.close();
//
//            } catch (MalformedURLException ex) {
//
////                dialog.dismiss();
//                ex.printStackTrace();
//
//                runOnUiThread(new Runnable() {
//                    public void run() {
////                        messageText.setText("MalformedURLException Exception : check script url.");
////                        Toast.makeText(UploadToServer.this, "MalformedURLException",
////                                Toast.LENGTH_SHORT).show();
//                        Log.d(LOG_TAG, "MalformedURLException Exception : check script url." );
//                    }
//                });
//
//                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
//            } catch (Exception e) {
//
////                dialog.dismiss();
//                e.printStackTrace();
//
//                runOnUiThread(new Runnable() {
//                    public void run() {
////                        messageText.setText("Got Exception : see logcat ");
////                        Toast.makeText(UploadToServer.this, "Got Exception : see logcat ",
////                                Toast.LENGTH_SHORT).show();
//                        Log.d(LOG_TAG, "Got Exception : see logcat ");
//
//                    }
//                });
////                Log.e("Upload file to server Exception", "Exception : "
////                        + e.getMessage(), e);
//            }
////            dialog.dismiss();
//            return serverResponseCode;
//
//        } // End else block
//    }
//

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    // AsyncTask - To convert Image to String
    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {
            Bitmap bitmap;
            String encodedString;

            protected void onPreExecute() {
                pDialog.setMessage("Converting Image to Binary Data");
                pDialog.show();
            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath,options);
                encodedString = getStringImage(bitmap);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                pDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param
                parametres.put("photo", encodedString);
                nomFichier = getphotoFile().getName();
                parametres.put("filename", nomFichier);
                parametres.put("id", mAdapter.getAnimateurId());
                // Trigger Image upload
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }

    public void makeHTTPCall() {
        pDialog.setMessage("Transfert des données en cours. Merci de patienter...");
//        String url = "http://192.168.1.12/imgupload/upload_image.php";
        String url = DaneContract.BASE_URL_UPDATE_PHOTO;
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post(url,
                parametres, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(), "response",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // Hide Progress Dialog
                        pDialog.hide();
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_IMAGE_LOAD:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        Bitmap bmp = null;
                        try {
                            bmp = getBitmapFromUri(selectedImage);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 0, stream);
                            ContentValues photoanimateur = new ContentValues();
                            photoanimateur.put(DaneContract.AnimateurEntry.COLUMN_PHOTO,stream.toByteArray());
                            Uri animateurURI = DaneContract.AnimateurEntry.buildAnimateurs();
                            String selection = "("
                                    + DaneContract.AnimateurEntry._ID
                                    + " = ? )";
                            String[] selectionArgs = new String[] { mAdapter.getIdAnimateur() };
                            getBaseContext().getContentResolver().update(animateurURI,photoanimateur,selection,selectionArgs);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    mCurrentPhotoPath = getphotoFile().getAbsolutePath();
//                    String fileNameSegments[] = mCurrentPhotoPath.split("/");
//                    nomFichier = fileNameSegments[fileNameSegments.length - 1];
//                    nomFichier = getphotoFile().getName();
//                    try {
//                        params.put("filename", nomFichier);
//                        params.put("photo", getphotoFile());
//                        params.put("id", mAdapter.getAnimateurId());
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } finally {
//                        sendPhoto();
//                    }
                    encodeImagetoString();
//                    sendPhoto();
//                    if (uploadFile(mAdapter.getAnimateurId())!=0) handleCameraPhoto();
                    handleCameraPhoto();
//                    sendPhoto();
                } else {
                    Log.w(LOG_TAG,"Erreur de capture Photo");
                }
                break;
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_departement_animateurs);
        ExpandableListView expandableContactListView = (ExpandableListView) findViewById(R.id.liste_departements_animateurs);
        mAdapter = new ListeDepartementsAnimateursAdapter(this,null,0,null,null,0,null,null);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        expandableContactListView.setIndicatorBounds(0,20);
        expandableContactListView.setAdapter(mAdapter);
        Loader<Cursor> loader = getLoaderManager().getLoader(-1);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(-1, null, this);
        } else {
            getLoaderManager().initLoader(-1, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.animactivity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_maj:
                FetchTask majanimTask = new FetchTask(this,DaneContract.BASE_URL_UPDATE_ANIM+"/");
                majanimTask.execute("maj_anim","","");
//                UpdateAnimateurs();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void UpdateAnimateurs() {
        Cursor animateursCursor = getContentResolver().query(
                DaneContract.AnimateurEntry.CONTENT_URI,
                new String[]{DaneContract.AnimateurEntry._ID,DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID},
                null,
                null,
                null);
        FetchTask majanimTask = null;
        String idanimateur = "";
        String animateur_id = "";
        if (animateursCursor != null ) {
            if  (animateursCursor.moveToFirst()) {
                do {
                    int idanimateurIndex = animateursCursor.getColumnIndex(DaneContract.AnimateurEntry._ID);
                    int animateurIdIndex = animateursCursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID);
                    idanimateur = String.valueOf(animateursCursor.getLong(idanimateurIndex));
                    animateur_id = animateursCursor.getString(animateurIdIndex);
                    majanimTask = new FetchTask(this,DaneContract.BASE_URL_UPDATE_ANIM+"/"+animateur_id);
                    majanimTask.execute("maj_anim",idanimateur);
                }while (animateursCursor.moveToNext());
            }
        }
        animateursCursor.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
//        Log.d(LOG_TAG, "onCreateLoader for loader_id " + id);
        CursorLoader cl;
        if (id != -1) {
            // child cursor
            Uri departementUri = DaneContract.AnimateurEntry.buildAnimateurs();
            String selection = "("
                    + DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID
                    + " = ? )";
            String sortOrder = DaneContract.AnimateurEntry.COLUMN_NOM
                    + " COLLATE LOCALIZED ASC";
            String[] selectionArgs = new String[] { String.valueOf(id) };

            cl = new CursorLoader(this, departementUri, ANIMATEURS_PROJECTION,
                    selection, selectionArgs, sortOrder);
        } else {
            // group cursor
            Uri departementUri = DaneContract.DepartementEntry.buildDepartement();
            String selection = DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_NOM + " != ''";
            String sortOrder = DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_NOM
                    + " COLLATE LOCALIZED ASC";
            cl = new CursorLoader(this, departementUri, DEPARTEMENT_PROJECTION,
                    selection, null, sortOrder);
        }
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
//        Log.d(LOG_TAG, "onLoadFinished() for loader_id " + id);
        if (id != -1) {
            // child cursor
            if (!cursor.isClosed()) {
//                Log.d(LOG_TAG, "data.getCount() " + cursor.getCount());

                HashMap<Integer, Integer> groupMap = mAdapter.getGroupMap();
                try {
                    int groupPos = groupMap.get(id);
//                    Log.d(LOG_TAG, "onLoadFinished() for groupPos " + groupPos);
                    mAdapter.setChildrenCursor(groupPos, cursor);
                } catch (NullPointerException e) {
                    Log.w(LOG_TAG,"Adapter expired, try again on the next query: "
                                    + e.getMessage());
                }
            }
        } else {
            mAdapter.setGroupCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
            int id = loader.getId();
//            Log.d(LOG_TAG, "onLoaderReset() for loader_id " + id);
            if (id != -1) {
                // child cursor
                try {
                    mAdapter.setChildrenCursor(id, null);
                } catch (NullPointerException e) {
                    Log.w(LOG_TAG, "Adapter expired, try again on the next query: "
                            + e.getMessage());
                }
            } else {
                mAdapter.setGroupCursor(null);
            }
    }
}
