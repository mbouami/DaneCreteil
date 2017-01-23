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
 * Created by Mohammed on 03/12/2016.
 */

public class EtabsAdapter extends CursorAdapter implements Filterable {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_ETABS_EN_COURS = 0;
    private static final int VIEW_TYPE_ETABS_LES_AUTRES = 1;
    private boolean mEtabLayout;
    private ContentResolver mContent;
    private static final String LOG_TAG = EtabsAdapter.class.getSimpleName();
    private boolean mAvecVille = false;

    public EtabsAdapter(Context context, Cursor c, int flags, boolean avecville) {
        super(context, c, flags);
        mContent = context.getContentResolver();
        mAvecVille = avecville;
    }

    public void setUseEtabLayout(boolean EtabLayout) {
        mEtabLayout = EtabLayout;
    }

    public static class ViewHolder {
        public final TextView nomView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.list_item_etabs_textview);
        }
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
//        return cursor.getString(cursor.getColumnIndex("type"))
//                +" "+cursor.getString(cursor.getColumnIndex("nom"));
        if (mAvecVille) {
            String nomville = null;
            Cursor curs = getVilleById(cursor.getString(cursor.getColumnIndex("ville_id")));
            if (curs.moveToFirst()){
                nomville = curs.getString(curs.getColumnIndex("nom"));
            }
            return cursor.getString(cursor.getColumnIndex("type"))+" "+
                    cursor.getString(cursor.getColumnIndex("nom"))
                    +" ("+nomville+")"
                    ;
        } else {
            return cursor.getString(cursor.getColumnIndex("type"))+" "+
                    cursor.getString(cursor.getColumnIndex("nom"));
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.list_item_etabs;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
//        String nomville = cursor.getString(VillesFragment.COL_VILLE_NOM);
//        viewHolder.nomView.setText(nomville);
        viewHolder.nomView.setText(convertCursorRowToUXFormat(cursor));
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_ETABS_EN_COURS : VIEW_TYPE_ETABS_LES_AUTRES;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public String convertToString(Cursor cursor) {
        //returns string inserted into textview after item from drop-down list is selected.
        return cursor.getString(cursor.getColumnIndex("type"))
                +" "+cursor.getString(cursor.getColumnIndex("nom"));
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        FilterQueryProvider filter = getFilterQueryProvider();
        if (filter != null) {
            return filter.runQuery(constraint);
        }
        Uri uri = DaneContract.EtablissementEntry.CONTENT_URI.buildUpon().appendPath(constraint.toString()).appendPath("rechercher").build();
//        Log.v(LOG_TAG, "In onCreateLoader "+uri.toString()+"---"+DaneContract.EtablissementEntry.getNomEtablissementFromUri(uri));
        String nometab = DaneContract.EtablissementEntry.getNomEtablissementFromUri(uri);
        String[] selectionArgs;
        String selection;
        selectionArgs = new String[]{"%" + nometab +"%"};
//        selection = DaneContract.EtablissementEntry.TABLE_NAME+"." + DaneContract.EtablissementEntry.COLUMN_NOM + " like ? ";
        return mContent.query(uri,
                ETAB_PROJECTION,
                sEtablissementParNomSelection,
                selectionArgs,
                null,
                null
        );
    }

    public Cursor getVilleById(String Idville) {
        Uri uri = DaneContract.VilleEntry.buildVille();
        String[] selectionArgs = new String[]{Idville};
        return mContent.query(uri,
                Ville_PROJECTION,
                sVilleParIdSelection,
                selectionArgs,
                null,
                null
        );
    }

    public static final String[] ETAB_PROJECTION = new String[] {
            DaneContract.EtablissementEntry.TABLE_NAME +"."+DaneContract.EtablissementEntry._ID,
            DaneContract.EtablissementEntry.TABLE_NAME +"."+DaneContract.EtablissementEntry.COLUMN_NOM,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TYPE,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_RNE,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_VILLE_ID
    };

    private static final String sEtablissementParNomSelection =
            DaneContract.EtablissementEntry.TABLE_NAME+
                    "." + DaneContract.EtablissementEntry.COLUMN_NOM + " like ? ";

    public static final String[] Ville_PROJECTION = new String[] {
            DaneContract.VilleEntry.TABLE_NAME +"."+DaneContract.VilleEntry._ID,
            DaneContract.VilleEntry.TABLE_NAME +"."+DaneContract.VilleEntry.COLUMN_VILLE_NOM
    };

    private static final String sVilleParIdSelection =
            DaneContract.VilleEntry.TABLE_NAME+
                    "." + DaneContract.VilleEntry._ID + " = ? ";
}
