package com.creteil.com.danecreteil.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mohammed on 03/12/2016.
 */

public class EtabsAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_ETABS_EN_COURS = 0;
    private static final int VIEW_TYPE_ETABS_LES_AUTRES = 1;
    private boolean mEtabLayout;

    public EtabsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
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
        return cursor.getString(EtabsFragment.COL_ETAB_TYPE)+" "+cursor.getString(EtabsFragment.COL_ETAB_NOM);
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
}
