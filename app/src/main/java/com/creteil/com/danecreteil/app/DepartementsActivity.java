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
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
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
    static String mencodedString;
    static byte[] mimageBytes;
    RequestParams parametres = new RequestParams();
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
        mImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        mimageBytes = getBytesFromBitmap(mImageBitmap);
        if (mimageBytes != null) {
            ContentValues photoanimateur = new ContentValues();
            photoanimateur.put(DaneContract.AnimateurEntry.COLUMN_PHOTO,Base64.decode(mencodedString,Base64.DEFAULT));
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


    // AsyncTask - To convert Image to String
    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {
            Bitmap bitmap;
            String encodedString;

            protected void onPreExecute() {
//                pDialog.setMessage("Converting Image to Binary Data");
//                pDialog.show();
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
                parametres.put("photo", encodedString);
                parametres.put("id", mAdapter.getAnimateurId());
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }

    public void makeHTTPCall() {
//        String url = "http://192.168.1.12/imgupload/upload_image.php";
        String url = DaneContract.BASE_URL_UPDATE_PHOTO;
//        String url="http://192.168.1.12/danecreteil/web/pnanimateurs/test";
//        String url="http://192.168.1.19:8080/danecreteil/web/pnanimateurs/test";
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post(url, parametres, new BaseJsonHttpResponseHandler<JSONObject>() {

            @Override
            public void onStart() {
                pDialog.setMessage("Transfert des données en cours. Merci de patienter...");
                pDialog.show();
            }

            @Override
            public void onFinish() {
                pDialog.hide();
            }

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
                    encodeImagetoString();
                } else {
                    Log.w(LOG_TAG,"Erreur de capture Photo");
                }
                break;
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
                mContext = this;
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(DaneContract.BASE_URL_UPDATE_ANIM+"/", new BaseJsonHttpResponseHandler<JSONObject>() {
                    @Override
                    public void onStart() {
//                        super.onStart();
                        pDialog.setMessage("Mise à jour de la liste des animateurs en cours. Merci de patienter...");
                        pDialog.show();
                    }

                    @Override
                    public void onFinish() {
//                        super.onFinish();
                        pDialog.hide();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                        JSONObject detailanimJson = null;
                        JSONArray animsArray = null;
                        final String OWM_NOM= "nom";
                        final String OWM_TEL = "tel";
                        final String OWM_EMAIL = "email";
                        final String OWN_PHOTO = "photo";
                        try {
                            detailanimJson = new JSONObject(rawJsonResponse);
                            animsArray = detailanimJson.getJSONArray("animateurs");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for(int k = 0; k < animsArray.length(); k++) {
                            JSONObject animateur = null;
                            try {
                                animateur = animsArray.getJSONObject(k);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Cursor animateursCursor = null;
                            try {
                                animateursCursor = mContext.getContentResolver().query(
                                        DaneContract.AnimateurEntry.CONTENT_URI,
                                        new String[]{DaneContract.AnimateurEntry._ID},
                                        DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID + " = ?",
                                        new String[]{animateur.getString("id")},
                                        null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (animateursCursor.moveToFirst()) {
                                ContentValues animateurValues = new ContentValues();
                                try {
                                    animateurValues.put(DaneContract.AnimateurEntry.COLUMN_NOM, animateur.getString(OWM_NOM));
                                    animateurValues.put(DaneContract.AnimateurEntry.COLUMN_TEL, animateur.getString(OWM_TEL));
                                    animateurValues.put(DaneContract.AnimateurEntry.COLUMN_EMAIL, animateur.getString(OWM_EMAIL));
                                    animateurValues.put(DaneContract.AnimateurEntry.COLUMN_PHOTO, Base64.decode(animateur.getString(OWN_PHOTO),Base64.DEFAULT));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String whereClause = "_id=?";
                                String[] whereArgs = new String[] { animateursCursor.getString(animateursCursor.getColumnIndex(DaneContract.AnimateurEntry._ID)) };
                                try {
                                    mContext.getContentResolver().update(DaneContract.AnimateurEntry.CONTENT_URI,
                                            animateurValues,whereClause,whereArgs);
                                } catch (NullPointerException e) {
                                    Log.w(LOG_TAG,e.getMessage());
                                }
                            }
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
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
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
        if (id != -1) {
            if (!cursor.isClosed()) {
                HashMap<Integer, Integer> groupMap = mAdapter.getGroupMap();
                try {
                    int groupPos = groupMap.get(id);
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
            if (id != -1) {
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
