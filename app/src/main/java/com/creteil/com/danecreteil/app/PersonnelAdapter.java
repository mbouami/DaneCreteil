package com.creteil.com.danecreteil.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mohammed on 08/12/2016.
 */

public class PersonnelAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_PERSONNEL_EN_COURS = 0;
    private static final int VIEW_TYPE_PERSONNEL_LES_AUTRES = 1;

    public PersonnelAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView nomView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.list_item_personnel_textview);
        }
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
        return cursor.getString(DetailFragment.COL_PERSONNEL_STATUT)+" : "+cursor.getString(DetailFragment.COL_PERSONNEL_NOM);
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
}
