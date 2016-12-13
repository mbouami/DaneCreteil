package com.creteil.com.danecreteil.app;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.TextView;

import com.creteil.com.danecreteil.app.data.DaneContract;

/**
 * Created by Mohammed on 08/12/2016.
 */

public class PersonnelAdapter extends CursorAdapter implements Filterable {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_PERSONNEL_EN_COURS = 0;
    private static final int VIEW_TYPE_PERSONNEL_LES_AUTRES = 1;
    private ContentResolver mContent;

    public PersonnelAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContent = context.getContentResolver();
    }

    public static class ViewHolder {
        public final TextView nomView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.list_item_personnel_textview);
        }
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
        return cursor.getString(ListePersonnelparNomFragment.COL_PERSONNEL_STATUT)
                +" : "
                +cursor.getString(ListePersonnelparNomFragment.COL_PERSONNEL_NOM);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.list_item_personnel;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.nomView.setText(convertCursorRowToUXFormat(cursor));
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_PERSONNEL_EN_COURS : VIEW_TYPE_PERSONNEL_LES_AUTRES;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public String convertToString(Cursor cursor) {
        //returns string inserted into textview after item from drop-down list is selected.
        return cursor.getString(ListePersonnelparNomFragment.COL_PERSONNEL_STATUT)+" "+cursor.getString(ListePersonnelparNomFragment.COL_PERSONNEL_NOM);
//                +" ("+cursor.getString(ListeEtabParNomFragment.COL_VILLE)+")";
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        FilterQueryProvider filter = getFilterQueryProvider();
        if (filter != null) {
            return filter.runQuery(constraint);
        }
        Uri uri = DaneContract.PersonnelEntry.CONTENT_URI.buildUpon().appendPath(constraint.toString()).appendPath("rechercher").build();
        String nompersonnel = DaneContract.PersonnelEntry.getPersonnelFromUri(uri);
        String[] selectionArgs;
        String selection;
        selectionArgs = new String[]{"%" + nompersonnel +"%"};
        return mContent.query(uri,
                PERSONNEL_COLUMNS,
                sPersonnelParNomSelection,
                selectionArgs,
                null,
                null
        );
    }
    public static final String[] PERSONNEL_COLUMNS = {
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry._ID,
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry.COLUMN_NOM,
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry.COLUMN_STATUT,
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry.COLUMN_ETABLISSEMENT_ID,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_NOM,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_RNE,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TYPE,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TEL,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_FAX,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_EMAIL,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_ADRESSE,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_CP,
//            DaneContract.VilleEntry.TABLE_NAME + "." + DaneContract.VilleEntry.COLUMN_VILLE_NOM
    };

    private static final String sPersonnelParNomSelection =
            DaneContract.PersonnelEntry.TABLE_NAME+
                    "." + DaneContract.PersonnelEntry.COLUMN_NOM + " like ? '";
}
