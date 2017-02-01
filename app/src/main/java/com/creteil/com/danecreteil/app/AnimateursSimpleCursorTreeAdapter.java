package com.creteil.com.danecreteil.app;

import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.widget.SimpleCursorTreeAdapter;

import com.creteil.com.danecreteil.app.data.DaneContract;

import java.util.HashMap;

/**
 * Created by Mohammed on 29/01/2017.
 */

public class AnimateursSimpleCursorTreeAdapter extends SimpleCursorTreeAdapter {

    private final String LOG_TAG = getClass().getSimpleName().toString();
    private AnimateursList mActivity;
    protected final HashMap<Integer, Integer> mGroupMap;

    // Please Note: Here cursor is not provided to avoid querying on main
    // thread.
    public AnimateursSimpleCursorTreeAdapter(Context context, int groupLayout,
                                             int childLayout, String[] groupFrom, int[] groupTo,
                                             String[] childrenFrom, int[] childrenTo) {

        super(context, null, groupLayout, groupFrom, groupTo, childLayout,
                childrenFrom, childrenTo);
        mActivity = (AnimateursList) context;
        mGroupMap = new HashMap<Integer, Integer>();
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        // Logic to get the child cursor on the basis of selected group.
        int groupPos = groupCursor.getPosition();
        int groupId = groupCursor.getInt(groupCursor
                .getColumnIndex(DaneContract.AnimateurEntry._ID));

        Log.d(LOG_TAG, "getChildrenCursor() for groupPos " + groupPos);
        Log.d(LOG_TAG, "getChildrenCursor() for groupId " + groupId);

        mGroupMap.put(groupId, groupPos);
        Loader<Cursor> loader = mActivity.getLoaderManager().getLoader(groupId);
        if (loader != null && !loader.isReset()) {
            mActivity.getLoaderManager().restartLoader(groupId, null, mActivity);
        } else {
            mActivity.getLoaderManager().initLoader(groupId, null, mActivity);
        }

        return null;
    }

    public HashMap<Integer, Integer> getGroupMap() {
        return mGroupMap;
    }
}
