package com.creteil.com.danecreteil.app;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by BOUAMI on 31/01/2017.
 */

public class ListeDepartementsAnimateursAdapter extends SimpleCursorTreeAdapter {
    private final String LOG_TAG = getClass().getSimpleName().toString();
    private DepartementsActivity mActivity;
    private  Context mContext;
    ProgressDialog pDialog = null;
    protected HashMap<Integer, Integer> mGroupMap;
    protected String midanimateur;
    protected String manimateur_id;
    private int nbreanimateurs;
    static PackageManager mpackageManager;
    protected String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_LOAD = 2;


    public ListeDepartementsAnimateursAdapter(Context context, Cursor cursor, int collapsedGroupLayout, int expandedGroupLayout, String[] groupFrom, int[] groupTo, int childLayout, int lastChildLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, collapsedGroupLayout, expandedGroupLayout, groupFrom, groupTo, childLayout, lastChildLayout, childFrom, childTo);

    }

    public ListeDepartementsAnimateursAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        mActivity = (DepartementsActivity) context;
        mGroupMap = new HashMap<Integer, Integer>();
        mContext = context;
        nbreanimateurs = 0;
        mpackageManager = context.getPackageManager();
        pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
    }

    public static class ViewHolder {
        public final TextView nomView;
        public final TextView telView;
        public final TextView mailView;
        public final ImageButton boutoncallView;
        public final ImageButton boutonmailView;
        public final ImageButton boutonupdateView;
        public final TextView departementView;
        public final ImageView photoView;
        public final ImageView flecheView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.nom);
            telView = (TextView) view.findViewById(R.id.tel);
            mailView = (TextView) view.findViewById(R.id.mail);
            boutoncallView = (ImageButton) view.findViewById(R.id.bouton_call);
            boutonmailView = (ImageButton) view.findViewById(R.id.bouton_mail);
            boutonupdateView = (ImageButton) view.findViewById(R.id.bouton_update);
            photoView = (ImageView) view.findViewById(R.id.photo);
            departementView = (TextView) view.findViewById(R.id.departement);
            flecheView = (ImageView) view.findViewById(R.id.imagefleche);
//            departementView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_forward_black_24dp,0,0,0);
        }
    }


    public HashMap<Integer, Integer> getGroupMap() {
        return mGroupMap;
    }

    public String getIdAnimateur() {
        return midanimateur;
    }

    protected void setIdAnimateur(String idanim) {
        midanimateur = idanim;
    }

    protected void setAnimateurId(String idanim) {
        manimateur_id = idanim;
    }

    public String getAnimateurId() {
        return manimateur_id;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        int groupPos = groupCursor.getPosition();
        int groupId = groupCursor.getInt(groupCursor.getColumnIndex(DaneContract.DepartementEntry._ID));
//        Log.d(LOG_TAG, "getChildrenCursor() for groupPos " + groupPos);
//        Log.d(LOG_TAG, "getChildrenCursor() for groupId " + groupId);
        mGroupMap.put(groupId, groupPos);
        Loader<Cursor> loader = mActivity.getLoaderManager().getLoader(groupId);
        if (loader != null && !loader.isReset()) {
            mActivity.getLoaderManager()
                    .restartLoader(groupId, null, mActivity);
        } else {
            mActivity.getLoaderManager().initLoader(groupId, null, mActivity);
        }

        return null;
    }

//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        return super.getGroupView(groupPosition, isExpanded, convertView, parent);
//    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "DANECRETEIL_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(String idanim,String anim_id) {
        setIdAnimateur(idanim);
        setAnimateurId(anim_id);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mpackageManager) != null) {
//            mActivity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(LOG_TAG,"Erreur de création de fichier : "+ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity,"com.creteil.com.danecreteil.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mActivity.setphotoFile(photoFile);
                mActivity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO );
            }
        }
    }

    private void photofromgallery(String idanim) {
        setIdAnimateur(idanim);
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        mActivity.startActivityForResult(gallery, REQUEST_IMAGE_LOAD);
    }

    private Intent createPhoneIntent(String tel) {
        Intent shareIntent = new Intent(Intent.ACTION_DIAL);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setData(Uri.parse("tel:"+tel));
    //        if (ActivityCompat.checkSelfPermission(getActivity(),
    //                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
    //            return null;
    //        }
        return shareIntent;
    }

    private Intent createMailIntent(String mail) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { mail });
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Demande d'information");
        return shareIntent;
    }

    @Override
    protected void bindChildView(View view, Context context, final Cursor cursor, boolean isLastChild) {
//        super.bindChildView(view, context, cursor, isLastChild);
        final String idanimateur = cursor.getString(cursor.getColumnIndex(DaneContract.AnimateurEntry._ID));
        final String animateur_id = cursor.getString(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID));
        final String nom = cursor.getString(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_NOM));
        final String Tel = cursor.getString(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_TEL));
        final String mail = cursor.getString(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_EMAIL));
//        final byte[] imageanim = Base64.decode(cursor.getBlob(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_PHOTO)),Base64.DEFAULT);
//        final byte[] imageanim = cursor.getBlob(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_PHOTO));
        final Context lecontext = context;
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.nomView.setText(nom);
        viewHolder.telView.setText(Tel);
        viewHolder.mailView.setText(mail);
//        Log.d(LOG_TAG,"Taile Image : "+nom+"--"+imageanim.length);
        if (cursor.getBlob(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_PHOTO)) != null){
//            final byte[] imageanim = Base64.decode(cursor.getBlob(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_PHOTO)),Base64.DEFAULT);
            final byte[] imageanim = cursor.getBlob(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_PHOTO));
            Bitmap photoanim = BitmapFactory.decodeByteArray(imageanim, 0, imageanim.length);
            viewHolder.photoView.setImageBitmap(photoanim);
        }
        viewHolder.boutoncallView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lecontext.startActivity(createPhoneIntent(Tel));
            }
        });

        viewHolder.boutonmailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lecontext.startActivity(createMailIntent(mail));
            }
        });

        viewHolder.boutonupdateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePictureIntent(idanimateur,animateur_id);
            }
        });

//        viewHolder.photoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UpdatePictureIntent(idanimateur,animateur_id);
//            }
//        });

        viewHolder.photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dispatchTakePictureIntent(idanimateur,animateur_id);
                return false;
            }
        });
    }

    private void UpdatePictureIntent(String idanimateur, String animateur_id) {
        final String idanim = idanimateur;
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(DaneContract.BASE_URL_UPDATE_ANIM+"/"+animateur_id, new BaseJsonHttpResponseHandler<JSONObject>() {
            @Override
            public void onStart() {
                pDialog.setMessage("Mise à jour de l'animateur en cours. Merci de patienter...");
                pDialog.show();
            }

            @Override
            public void onFinish() {
                pDialog.hide();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                JSONObject detailanimJson = null;
                ContentValues animateurValues = new ContentValues();
                final String OWM_NOM= "nom";
                final String OWM_TEL = "tel";
                final String OWM_EMAIL = "email";
                final String OWN_PHOTO = "photo";
                try {
                    detailanimJson = new JSONObject(rawJsonResponse);
                    animateurValues.put(DaneContract.AnimateurEntry.COLUMN_NOM, detailanimJson.getString(OWM_NOM));
                    animateurValues.put(DaneContract.AnimateurEntry.COLUMN_TEL, detailanimJson.getString(OWM_TEL));
                    animateurValues.put(DaneContract.AnimateurEntry.COLUMN_EMAIL, detailanimJson.getString(OWM_EMAIL));
                    animateurValues.put(DaneContract.AnimateurEntry.COLUMN_PHOTO, Base64.decode(detailanimJson.getString(OWN_PHOTO),Base64.DEFAULT));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String whereClause = "_id=?";
                String[] whereArgs = new String[] { idanim };
                try {
                    mContext.getContentResolver().update(DaneContract.AnimateurEntry.CONTENT_URI,
                            animateurValues,whereClause,whereArgs);
                } catch (NullPointerException e) {
                    Log.w(LOG_TAG,e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {
                pDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(mContext,
                            "Ressorces de la requête non trouvées",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(mContext,
                            "Lz serveur ne répond pas",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(mContext,
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
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
//        super.bindGroupView(view, context, cursor, isExpanded);
        final String depart = cursor.getString(cursor.getColumnIndex(DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_NOM));
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.departementView.setText("Les animateurs du " + depart);

    }

    @Override
    public View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
//        return super.newChildView(context, cursor, isLastChild, parent);
        int layoutId = -1;
        layoutId = R.layout.list_item_animateurs;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
//        return super.newGroupView(context, cursor, isExpanded, parent);
        int layoutId = -1;
        layoutId = R.layout.list_item_departement;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

}
