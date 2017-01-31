package com.creteil.com.danecreteil.app;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ExpandableListView;

import com.creteil.com.danecreteil.app.data.DaneContract;

import java.util.HashMap;

/**
 * Created by BOUAMI on 31/01/2017.
 */

public class DepartementsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = getClass().getSimpleName().toString();
    ListeDepartementsAnimateursAdapter mAdapter;

    private static final String[] ANIMATEURS_PROJECTION = {
            DaneContract.AnimateurEntry._ID,
            DaneContract.AnimateurEntry.COLUMN_NOM,
            DaneContract.AnimateurEntry.COLUMN_TEL,
            DaneContract.AnimateurEntry.COLUMN_EMAIL,
            DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID,
            DaneContract.AnimateurEntry.COLUMN_PHOTO
    };

    private static final String[] DEPARTEMENT_PROJECTION = {
            DaneContract.DepartementEntry.TABLE_NAME + "." + DaneContract.DepartementEntry._ID,
            DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_NOM,
            DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_INTITULE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_departement_animateurs);
        ExpandableListView expandableContactListView = (ExpandableListView) findViewById(R.id.liste_departements_animateurs);
        mAdapter = new ListeDepartementsAnimateursAdapter(this,null,0,null,null,0,null,null);
        expandableContactListView.setIndicatorBounds(0,20);
        expandableContactListView.setAdapter(mAdapter);
        Loader<Cursor> loader = getLoaderManager().getLoader(-1);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(-1, null, this);
        } else {
            getLoaderManager().initLoader(-1, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader for loader_id " + id);
        CursorLoader cl;
        if (id != -1) {
            // child cursor
            Uri departementUri = DaneContract.AnimateurEntry.buildAnimateurs();
            String selection = "("
                    + DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID
                    + " = ? )";
            String sortOrder = DaneContract.AnimateurEntry.COLUMN_NOM
                    + " COLLATE LOCALIZED ASC";
            String[] selectionArgs = new String[] { String.valueOf(id) };

            cl = new CursorLoader(this, departementUri, ANIMATEURS_PROJECTION,
                    selection, selectionArgs, sortOrder);
        } else {
            // group cursor
            Uri departementUri = DaneContract.DepartementEntry.buildDepartement();
            String selection = DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_NOM + " != ''";
            String sortOrder = DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_NOM
                    + " COLLATE LOCALIZED ASC";
            cl = new CursorLoader(this, departementUri, DEPARTEMENT_PROJECTION,
                    selection, null, sortOrder);
        }
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        Log.d(LOG_TAG, "onLoadFinished() for loader_id " + id);
        if (id != -1) {
            // child cursor
            if (!cursor.isClosed()) {
                Log.d(LOG_TAG, "data.getCount() " + cursor.getCount());

                HashMap<Integer, Integer> groupMap = mAdapter.getGroupMap();
                try {
                    int groupPos = groupMap.get(id);
                    Log.d(LOG_TAG, "onLoadFinished() for groupPos " + groupPos);
                    mAdapter.setChildrenCursor(groupPos, cursor);
                } catch (NullPointerException e) {
                    Log.w(LOG_TAG,
                            "Adapter expired, try again on the next query: "
                                    + e.getMessage());
                }
            }
        } else {
            mAdapter.setGroupCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
            int id = loader.getId();
            Log.d(LOG_TAG, "onLoaderReset() for loader_id " + id);
            if (id != -1) {
                // child cursor
                try {
                    mAdapter.setChildrenCursor(id, null);
                } catch (NullPointerException e) {
                    Log.w(LOG_TAG, "Adapter expired, try again on the next query: "
                            + e.getMessage());
                }
            } else {
                mAdapter.setGroupCursor(null);
            }
    }
}
