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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.creteil.com.danecreteil.app.data.DaneContract;

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
    private int nbreanimateurs;
    static PackageManager mpackageManager;
    protected String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
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
        String imageFileName = "JPEG_" + timeStamp + "_";
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }

    private void capturerPhotoIntent(String idanim) {
        setIdAnimateur(idanim);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mpackageManager) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mContext,
                        "com.creteil.com.danecreteil.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mActivity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

//    private void setPic() {
//        // Get the dimensions of the View
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        mImageView.setImageBitmap(bitmap);
//    }

    private void dispatchTakePictureIntent(String idanim) {
        setIdAnimateur(idanim);
//        Log.d(LOG_TAG, "dispatchTakePictureIntent " + idanim);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mpackageManager) != null) {
            mActivity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
        final String nom = cursor.getString(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_NOM));
        final String Tel = cursor.getString(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_TEL));
        final String mail = cursor.getString(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_EMAIL));
        final byte[] imageanim = cursor.getBlob(cursor.getColumnIndex(DaneContract.AnimateurEntry.COLUMN_PHOTO));
        final Context lecontext = context;
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.nomView.setText(nom);
        viewHolder.telView.setText(Tel);
        viewHolder.mailView.setText(mail);

        if (imageanim != null){
            Bitmap photoanim = BitmapFactory.decodeByteArray(imageanim, 0, imageanim.length);
            viewHolder.photoView.setImageBitmap(photoanim);
//            viewHolder.photoView.setImageBitmap(
//                    decodeSampledBitmapFromResource(getResources(), R.id.photo, 100, 100));
        }
//        Bitmap bImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_downward_black_24dp);
//        viewHolder.flecheView.setImageBitmap(bImage);
//        viewHolder.flecheView.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
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
//                Log.d(LOG_TAG, "photoView onClick " + nom+"----"+idanimateur+"----"+getIdAnimateur());
               dispatchTakePictureIntent(idanimateur);
//                photofromgallery(idanimateur);
//                capturerPhotoIntent(idanimateur);
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
