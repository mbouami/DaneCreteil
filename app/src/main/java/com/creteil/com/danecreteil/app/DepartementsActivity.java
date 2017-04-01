package com.creteil.com.danecreteil.app;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.creteil.com.danecreteil.app.data.FetchTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by BOUAMI on 31/01/2017.
 */

public class DepartementsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = getClass().getSimpleName().toString();
    ListeDepartementsAnimateursAdapter mAdapter;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_LOAD = 2;

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

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(mpackageManager) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

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
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Cursor  mcursor = mAdapter.getCursor();
                    ByteArrayOutputStream blob = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 0, blob);
                    byte[] bitmapdata = blob.toByteArray();
                    imageBitmap.recycle();
                    imageBitmap = null;
                    String photo_string = bitmapdata.toString();
                    ContentValues photoanimateur = new ContentValues();
                    photoanimateur.put(DaneContract.AnimateurEntry.COLUMN_PHOTO,blob.toByteArray());
                    Log.w(LOG_TAG,"stream.toByteArray(): "+ bitmapdata.length+ "---"+photo_string);
                    Uri animateurURI = DaneContract.AnimateurEntry.buildAnimateurs();
                    String selection = "("
                            + DaneContract.AnimateurEntry._ID
                            + " = ? )";
                    String[] selectionArgs = new String[] { mAdapter.getIdAnimateur() };
                    getBaseContext().getContentResolver().update(animateurURI,photoanimateur,selection,selectionArgs);
                    try {
//                        String UPDATE_PHOTO_URL ="http://www.bouami.fr/gestionetabs/web/pnanimateurs?id="+mAdapter.getIdAnimateur()+"&donnees="+photo_string;
//                        String UPDATE_PHOTO_URL ="http://validate.jsontest.com/";
                        String UPDATE_PHOTO_URL = DaneContract.BASE_URL + "/pnanimateurs?id="+mAdapter.getIdAnimateur()+"&donnees="+photo_string;
                        URL url = new URL(UPDATE_PHOTO_URL);
                        Log.w(LOG_TAG,"UPDATE_PHOTO_URL: "+ UPDATE_PHOTO_URL);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        try {
                            urlConnection.setRequestMethod("GET");
                            urlConnection.connect();
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            readStream(in);
                        } finally {
                            urlConnection.disconnect();
                        }
                        blob.close();
                        blob = null;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
        }
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
////            mImageView.setImageBitmap(imageBitmap);
//            Cursor  mcursor = mAdapter.getCursor();
//            String sortie = mcursor.getString(mcursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_NOM));
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
//            ContentValues photoanimateur = new ContentValues();
//            photoanimateur.put(DaneContract.AnimateurEntry.COLUMN_PHOTO,stream.toByteArray());
//            Uri animateurURI = DaneContract.AnimateurEntry.buildAnimateurs();
//            String selection = "("
//                    + DaneContract.AnimateurEntry._ID
//                    + " = ? )";
//            String[] selectionArgs = new String[] { mAdapter.getIdAnimateur() };
//            getBaseContext().getContentResolver().update(animateurURI,photoanimateur,selection,selectionArgs);
//            Log.d(LOG_TAG, "onActivityResult "+ stream.toByteArray().toString()+"---"+ mAdapter.getGroupMap().toString());
//            Log.d(LOG_TAG, "onActivityResult "+ mAdapter.getIdAnimateur()+"----");
//        }
    }

    private void readStream(InputStream in) {
        String resultat = null;
        BufferedReader reader;
        StringBuffer buffer;
        buffer = new StringBuffer();
        String line;
        reader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultat = buffer.toString();
        Log.w(LOG_TAG,"resultat : " + resultat);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_departement_animateurs);
        ExpandableListView expandableContactListView = (ExpandableListView) findViewById(R.id.liste_departements_animateurs);
        mAdapter = new ListeDepartementsAnimateursAdapter(this,null,0,null,null,0,null,null);
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
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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
