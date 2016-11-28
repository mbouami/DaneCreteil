package com.creteil.com.danecreteil.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mohammed on 28/11/2016.
 */

public class VillesAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_VILLE_EN_COURS = 0;
    private static final int VIEW_TYPE_VILLE_LES_AUTRES = 1;

    public VillesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    public static class ViewHolder {
        public final TextView idView;
        public final TextView nomView;

        public ViewHolder(View view) {
            idView = (TextView) view.findViewById(R.id.id);
            nomView = (TextView) view.findViewById(R.id.nom);
        }
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
//        switch (viewType) {
//            case VIEW_TYPE_TODAY: {
//                layoutId = R.layout.list_item_forecast_today;
//                break;
//            }
//            case VIEW_TYPE_FUTURE_DAY: {
//                layoutId = R.layout.list_item_forecast;
//                break;
//            }
//        }
        layoutId = R.layout.list_item_villes;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        long idville = cursor.getLong(VillesFragment.COL_VILLE_ID);
        viewHolder.idView.setText(String.valueOf(idville));
        String nomville = cursor.getString(VillesFragment.COL_VILLE_NOM);
        viewHolder.nomView.setText(nomville);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_VILLE_EN_COURS : VIEW_TYPE_VILLE_LES_AUTRES;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
