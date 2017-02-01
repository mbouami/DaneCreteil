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
import com.creteil.com.danecreteil.app.data.DaneProvider;

/**
 * Created by mbouami on 28/12/2016.
 */

public class AnimateursAdapter extends CursorAdapter implements Filterable {
    private ContentResolver mContent;
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_ANIMS_EN_COURS = 0;
    private static final int VIEW_TYPE_ANIMS_LES_AUTRES = 1;
    private static final String LOG_TAG = AnimateursAdapter.class.getSimpleName();

    public AnimateursAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContent = context.getContentResolver();
    }

    public static class ViewHolder {
        public final TextView nomView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.list_item_anims_textview);
        }
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex("nom"));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.list_item_anims;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        AnimateursAdapter.ViewHolder viewHolder = new AnimateursAdapter.ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        AnimateursAdapter.ViewHolder viewHolder = (AnimateursAdapter.ViewHolder) view.getTag();
        viewHolder.nomView.setText(convertCursorRowToUXFormat(cursor));
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_ANIMS_EN_COURS : VIEW_TYPE_ANIMS_LES_AUTRES;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public String convertToString(Cursor cursor) {
        //returns string inserted into textview after item from drop-down list is selected.
        return cursor.getString(cursor.getColumnIndex("nom"));
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        FilterQueryProvider filter = getFilterQueryProvider();
        if (filter != null) {
            return filter.runQuery(constraint);
        }
        Uri uri = DaneContract.AnimateurEntry.CONTENT_URI.buildUpon().appendPath(constraint.toString()).appendPath("rechercher").build();
        String nomanim = DaneContract.AnimateurEntry.getNomAnimateurFromUri(uri);
        String[] selectionArgs;
        String selection;
        selectionArgs = new String[]{"%" + nomanim +"%"};
        selection = DaneContract.AnimateurEntry.TABLE_NAME+"." + DaneContract.AnimateurEntry.COLUMN_NOM + " like ? ";
        return mContent.query(uri,
                ANIMATEURS_PROJECTION,
                selection,
                selectionArgs,
                null,
                null
        );
    }
    public static final String[] ANIMATEURS_PROJECTION = new String[] {
            DaneContract.AnimateurEntry.TABLE_NAME +"."+DaneContract.AnimateurEntry._ID,
            DaneContract.AnimateurEntry.TABLE_NAME +"."+DaneContract.AnimateurEntry.COLUMN_NOM
    };
}
