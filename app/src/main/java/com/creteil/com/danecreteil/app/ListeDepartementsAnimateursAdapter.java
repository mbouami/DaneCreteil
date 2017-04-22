package com.creteil.com.danecreteil.app;

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

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.creteil.com.danecreteil.app.data.FetchTask;
import com.creteil.com.danecreteil.app.photos.PhotoIntentActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by BOUAMI on 31/01/2017.
 */

public class ListeDepartementsAnimateursAdapter extends SimpleCursorTreeAdapter {
    private final String LOG_TAG = getClass().getSimpleName().toString();
    private DepartementsActivity mActivity;
    private  Context mContext;
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
    }

    public static class ViewHolder {
        public final TextView nomView;
        public final TextView telView;
        public final TextView mailView;
        public final ImageButton boutoncallView;
        public final ImageButton boutonmailView;
        public final TextView departementView;
        public final ImageView photoView;
        public final ImageView flecheView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.nom);
            telView = (TextView) view.findViewById(R.id.tel);
            mailView = (TextView) view.findViewById(R.id.mail);
            boutoncallView = (ImageButton) view.findViewById(R.id.bouton_call);
            boutonmailView = (ImageButton) view.findViewById(R.id.bouton_mail);
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
//        FetchTask majanimTask = new FetchTask(mContext,DaneContract.BASE_URL_UPDATE_ANIM+"/"+anim_id);
//        majanimTask.execute("maj_anim",idanim,anim_id);
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
            final byte[] imageanim = Base64.decode(cursor.getBlob(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_PHOTO)),Base64.DEFAULT);
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
        viewHolder.photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePictureIntent(idanimateur,animateur_id);
            }
        });

//        viewHolder.photoView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                dispatchTakePictureIntent(idanimateur,animateur_id);
//                return false;
//            }
//        });
    }

    private void UpdatePictureIntent(String idanimateur, String animateur_id) {
        FetchTask majanimTask = new FetchTask(mContext,DaneContract.BASE_URL_UPDATE_ANIM+"/"+animateur_id);
        majanimTask.execute("maj_anim",idanimateur);
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
