package com.creteil.com.danecreteil.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by mbouami on 23/01/2017.
 */

public class ListeanimateursAdapter extends CursorAdapter {

    public ListeanimateursAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView nomView;
        public final TextView telView;
        public final TextView mailView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.nom);
            telView = (TextView) view.findViewById(R.id.tel);
            mailView = (TextView) view.findViewById(R.id.mail);
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

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.nomView.setText(cursor.getString(cursor.getColumnIndex("nom")));
        viewHolder.telView.setText(cursor.getString(cursor.getColumnIndex("tel")));
        viewHolder.mailView.setText(cursor.getString(cursor.getColumnIndex("email")));
    }
}
