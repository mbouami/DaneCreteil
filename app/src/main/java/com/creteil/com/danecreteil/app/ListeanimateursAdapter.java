package com.creteil.com.danecreteil.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mbouami on 23/01/2017.
 */

public class ListeanimateursAdapter extends CursorAdapter {
    private final String LOG_TAG = getClass().getSimpleName().toString();

    public ListeanimateursAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView nomView;
        public final TextView telView;
        public final TextView mailView;
        public final ImageButton boutoncallView;
        public final ImageButton boutonmailView;
        public final ImageView photoView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.nom);
            telView = (TextView) view.findViewById(R.id.tel);
            mailView = (TextView) view.findViewById(R.id.mail);
            boutoncallView = (ImageButton) view.findViewById(R.id.bouton_call);
            boutonmailView = (ImageButton) view.findViewById(R.id.bouton_mail);
            photoView = (ImageView) view.findViewById(R.id.photo);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = -1;
        layoutId = R.layout.list_item_animateurs;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ListeanimateursAdapter.ViewHolder viewHolder = new ListeanimateursAdapter.ViewHolder(view);
        view.setTag(viewHolder);
        return view;
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
    public void bindView(View view, Context context, Cursor cursor) {
        final String Tel = cursor.getString(cursor.getColumnIndex("tel"));
        final String mail = cursor.getString(cursor.getColumnIndex("email"));
        final Context lecontext = context;
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.nomView.setText(cursor.getString(cursor.getColumnIndex("nom")));
        viewHolder.telView.setText(cursor.getString(cursor.getColumnIndex("tel")));
        viewHolder.mailView.setText(cursor.getString(cursor.getColumnIndex("email")));
        byte[] byteArrayPhoto = cursor.getBlob(cursor.getColumnIndexOrThrow("photo"));
        Log.d(LOG_TAG,"Taile Image : "+byteArrayPhoto.length);
        Bitmap bm = BitmapFactory.decodeByteArray(byteArrayPhoto, 0 ,byteArrayPhoto.length);
        viewHolder.photoView.setImageBitmap(bm);
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
    }
}
