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
import com.creteil.com.danecreteil.app.data.JSONParser;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
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
    static String mencodedString;
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

    public static final String[] ANIMATEURS_PROJECTION = {
            DaneContract.AnimateurEntry._ID,
            DaneContract.AnimateurEntry.COLUMN_NOM,
            DaneContract.AnimateurEntry.COLUMN_TEL,
            DaneContract.AnimateurEntry.COLUMN_EMAIL,
            DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID,
            DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID,
            DaneContract.AnimateurEntry.COLUMN_PHOTO
    };

    public static final String[] DEPARTEMENT_PROJECTION = {
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
            ContentValues photoanimateur = new ContentValues();
//            photoanimateur.put(DaneContract.AnimateurEntry.COLUMN_PHOTO,mimageBytes);
            photoanimateur.put(DaneContract.AnimateurEntry.COLUMN_PHOTO,Base64.decode(mencodedString,Base64.DEFAULT));

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
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    //Reducing Image Size of a selected Image
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 500;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

    //Converting Selected Image to Base64Encode String
    private String getImageBase64(Uri selectedImage) {
        Bitmap myImg = null;
        try {
            myImg = decodeUri(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        return  android.util.Base64.encodeToString(byte_arr, 0);
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
                mencodedString = encodedString;
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                pDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param
                parametres.put("photo", encodedString);
//                File myFile = new File(mCurrentPhotoPath);
//                try {
//                    parametres.put("photo", myFile);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                nomFichier = getphotoFile().getName();
//                parametres.put("filename", nomFichier);
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
//        String url="http://192.168.1.12/danecreteil/web/pnanimateurs/test";
//        String url="http://192.168.1.19:8080/danecreteil/web/pnanimateurs/test";
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post(url, parametres, new BaseJsonHttpResponseHandler<JSONObject>() {

//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, String response) {
//                        pDialog.hide();
//                        handleCameraPhoto();
//                        Toast.makeText(getApplicationContext(), "Transfert réussi : "+rawJsonResponse,Toast.LENGTH_LONG).show();
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, String errorResponse) {
//
//            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                pDialog.hide();
                JSONObject mrawJsonResponse = null;
                try {
                    mrawJsonResponse = new JSONObject(rawJsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (!mrawJsonResponse.getBoolean("erreur")){
                        handleCameraPhoto();
                        Toast.makeText(getApplicationContext(),mrawJsonResponse.getString("message"),
                                        Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {
                        pDialog.hide();
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Ressorces de la requête non trouvées",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Lz serveur ne répond pas",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Erreurs \n Sources d'erreurs: \n1. Pas de connection à internet\n2. Application non déployée sur le serveur\n3. Le serveur Web est à l'arrêt\n HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
            }

            @Override
            protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
//        client.post(url,
//                parametres, new  AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        pDialog.hide();
//                        handleCameraPhoto();
//                        Toast.makeText(getApplicationContext(), "Transfert réussi",Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                        // Hide Progress Dialog
//                        pDialog.hide();
//                        // When Http response code is '404'
//                        if (statusCode == 404) {
//                            Toast.makeText(getApplicationContext(),
//                                    "Ressorces de la requête non trouvées",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                        // When Http response code is '500'
//                        else if (statusCode == 500) {
//                            Toast.makeText(getApplicationContext(),
//                                    "Lz serveur ne répond pas",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                        // When Http response code other than 404, 500
//                        else {
//                            Toast.makeText(
//                                    getApplicationContext(),
//                                    "Erreurs \n Sources d'erreurs: \n1. Pas de connection à internet\n2. Application non déployée sur le serveur\n3. Le serveur Web est à l'arrêt\n HTTP Status code : "
//                                            + statusCode, Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    }
//                });
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
//                    handleCameraPhoto();
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
