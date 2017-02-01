package com.creteil.com.danecreteil.app;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.creteil.com.danecreteil.app.data.DaneContract;

import java.util.HashMap;

import static com.creteil.com.danecreteil.app.R.id.imageView;

/**
 * Created by BOUAMI on 31/01/2017.
 */

public class ListeDepartementsAnimateursAdapter extends SimpleCursorTreeAdapter {
    private final String LOG_TAG = getClass().getSimpleName().toString();
    private DepartementsActivity mActivity;
    private  Context mContext;
    protected HashMap<Integer, Integer> mGroupMap;
    private int nbreanimateurs = 0;

    public ListeDepartementsAnimateursAdapter(Context context, Cursor cursor, int collapsedGroupLayout, int expandedGroupLayout, String[] groupFrom, int[] groupTo, int childLayout, int lastChildLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, collapsedGroupLayout, expandedGroupLayout, groupFrom, groupTo, childLayout, lastChildLayout, childFrom, childTo);
    }

    public ListeDepartementsAnimateursAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        mActivity = (DepartementsActivity) context;
        mGroupMap = new HashMap<Integer, Integer>();
        mContext = context;
    }

    public static class ViewHolder {
        public final TextView nomView;
        public final TextView telView;
        public final TextView mailView;
        public final ImageButton boutoncallView;
        public final ImageButton boutonmailView;
        public final TextView departementView;
        public final ImageView flecheView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.nom);
            telView = (TextView) view.findViewById(R.id.tel);
            mailView = (TextView) view.findViewById(R.id.mail);
            boutoncallView = (ImageButton) view.findViewById(R.id.bouton_call);
            boutonmailView = (ImageButton) view.findViewById(R.id.bouton_mail);
            departementView = (TextView) view.findViewById(R.id.departement);
            flecheView = (ImageView) view.findViewById(R.id.imagefleche);
//            departementView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_forward_black_24dp,0,0,0);
        }
    }


    public HashMap<Integer, Integer> getGroupMap() {
        return mGroupMap;
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
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
//        super.bindChildView(view, context, cursor, isLastChild);
        final String Tel = cursor.getString(cursor.getColumnIndex("tel"));
        final String mail = cursor.getString(cursor.getColumnIndex("email"));
        final Context lecontext = context;
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.nomView.setText(cursor.getString(cursor.getColumnIndex("nom")));
        viewHolder.telView.setText(cursor.getString(cursor.getColumnIndex("tel")));
        viewHolder.mailView.setText(cursor.getString(cursor.getColumnIndex("email")));
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
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
//        super.bindGroupView(view, context, cursor, isExpanded);
        final String depart = cursor.getString(cursor.getColumnIndex(DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_NOM));
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.departementView.setText("Les animateurs du "+depart);

    }

//    @Override
//    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
////        return super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
//        View v = convertView;
//
//        if (v == null) {
//            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            int layoutId = -1;
//            layoutId = R.layout.list_item_animateurs;
//            v = inflater.inflate(layoutId, parent, false);
//            ViewHolder viewHolder = new ViewHolder(v);
//            v.setTag(viewHolder);
//        }
////        ViewHolder viewHolder = (ViewHolder) v.getTag();
////        viewHolder.nomView.setText(cursor.getString(cursor.getColumnIndex("nom")));
////        viewHolder.telView.setText(cursor.getString(cursor.getColumnIndex("tel")));
////        viewHolder.mailView.setText(cursor.getString(cursor.getColumnIndex("email")));
//
////        TextView itemName = (TextView) v.findViewById(R.id.itemName);
////        TextView itemDescr = (TextView) v.findViewById(R.id.itemDescr);
////
////        ItemDetail det = catList.get(groupPosition).getItemList().get(childPosition);
////
////        itemName.setText(det.getName());
////        itemDescr.setText(det.getDescr());
//
//        return v;
//
//    }
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
