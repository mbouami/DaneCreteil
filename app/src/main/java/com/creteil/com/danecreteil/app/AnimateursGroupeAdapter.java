package com.creteil.com.danecreteil.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.creteil.com.danecreteil.app.data.DaneContract;

import java.util.HashMap;

/**
 * Created by Mohammed on 25/01/2017.
 */

public class AnimateursGroupeAdapter extends SimpleCursorTreeAdapter {

    private final String LOG_TAG = getClass().getSimpleName().toString();
    private ContentResolver mContent;
    protected final HashMap<String, Integer> mGroupMap;

    public AnimateursGroupeAdapter(Context context) {
//        super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        super(context, null,0,null,null,0,null,null);
//        super(
//                context,
//                cursor,
//                R.layout.list_item_departements,
//                new String[]{DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_DEPART},
//                new int[]{android.R.id.text1},
//                R.layout.list_item_animateurs,
//                new String[]{DaneContract.AnimateurEntry.COLUMN_NOM},
//                new int[]{android.R.id.text1});
        mContent = context.getContentResolver();
        mGroupMap = new HashMap<String, Integer>();
    }

    private Intent createPhoneIntent(String tel) {
        Intent shareIntent = new Intent(Intent.ACTION_DIAL);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setData(Uri.parse("tel:"+tel));
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


    public HashMap<String, Integer> getGroupMap() {
        return mGroupMap;
    }

    public static class ViewHolder {
        public final TextView nomView;
        public final TextView telView;
        public final TextView mailView;
        public final TextView departementView;
        public final ImageButton boutoncallView;
        public final ImageButton boutonmailView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.nom);
            telView = (TextView) view.findViewById(R.id.tel);
            mailView = (TextView) view.findViewById(R.id.mail);
            departementView = (TextView) view.findViewById(R.id.departement);
            boutoncallView = (ImageButton) view.findViewById(R.id.bouton_call);
            boutonmailView = (ImageButton) view.findViewById(R.id.bouton_mail);
        }
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        Log.d(LOG_TAG, "getChildrenCursor() ");
        int groupPos = groupCursor.getPosition();
        String departement = groupCursor.getString(groupCursor.getColumnIndex(
                                                    DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID));
        Log.d(LOG_TAG, "getChildrenCursor() for groupPos " + groupPos);
        Log.d(LOG_TAG, "getChildrenCursor() for departement " + departement);

        mGroupMap.put(departement, groupPos);
        String[] ANIMATEURS_COLUMNS = {
                DaneContract.AnimateurEntry.TABLE_NAME + "." + DaneContract.AnimateurEntry._ID,
                DaneContract.AnimateurEntry.COLUMN_NOM,
                DaneContract.AnimateurEntry.COLUMN_TEL,
                DaneContract.AnimateurEntry.COLUMN_EMAIL,
                DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID
        };
        Uri animateursUri = DaneContract.AnimateurEntry.buildAnimateurs();
        String sortOrder = DaneContract.AnimateurEntry.COLUMN_NOM + " ASC";
        String selection = "("+ DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID+ " = ? )";
        String[] selectionArgs = new String[] { departement };
        Cursor listeanimateurs =  mContent.query(
                animateursUri,
                ANIMATEURS_COLUMNS,
                selection,
                selectionArgs,
                sortOrder);
        return listeanimateurs;
    }

//    @Override
//    public String convertToString(Cursor cursor) {
//        return super.convertToString(cursor);
//    }
//
//    @Override
//    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
//        Log.d(LOG_TAG, "runQueryOnBackgroundThread ");
//        return super.runQueryOnBackgroundThread(constraint);
//    }

    @Override
    public View newChildView(Context context, Cursor cursorChild, boolean isLastChild, ViewGroup parent) {
//        return super.newChildView(context, cursor, isLastChild, parent);
        Log.d(LOG_TAG, "newChildView ");
        int layoutId = -1;
        layoutId = R.layout.list_item_animateurs;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursorChild, boolean isLastChild) {
//        super.bindChildView(view, context, cursor, isLastChild);
        Log.d(LOG_TAG, "bindChildView ");
        final String nom = cursorChild.getString(cursorChild.getColumnIndex("nom"));
        final String Tel = cursorChild.getString(cursorChild.getColumnIndex("tel"));
        final String mail = cursorChild.getString(cursorChild.getColumnIndex("email"));
        final Context lecontext = context;
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.nomView.setText(nom);
        viewHolder.telView.setText(Tel);
        viewHolder.mailView.setText(mail);
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

    @Override
    public View newGroupView(Context context, Cursor cursorGroup, boolean isExpanded, ViewGroup parent) {
//        return super.newGroupView(context, cursor, isExpanded, parent);
        Log.d(LOG_TAG, "newGroupView "+isExpanded);
//        int layoutId = -1;
        int layoutId = R.layout.list_item_departements;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursorGroup, boolean isExpanded) {
//        super.bindGroupView(view, context, cursor, isExpanded);
        final String depart = cursorGroup.getString(cursorGroup.getColumnIndex("departement"));
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.departementView.setText(depart);
        Log.d(LOG_TAG, "bindGroupView "+depart+"----"+isExpanded+"----"+viewHolder.departementView.getText());
    }
}
