package com.creteil.com.danecreteil.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ExpandableListView;

import com.creteil.com.danecreteil.app.data.DaneContract;

import java.util.HashMap;

/**
 * Created by Mohammed on 29/01/2017.
 */

public class AnimateursList extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = getClass().getSimpleName().toString();
    private static final String[] ANIMATEURS_COLUMNS = {
            DaneContract.AnimateurEntry.TABLE_NAME + "." + DaneContract.AnimateurEntry._ID,
            DaneContract.AnimateurEntry.COLUMN_NOM,
            DaneContract.AnimateurEntry.COLUMN_TEL,
            DaneContract.AnimateurEntry.COLUMN_EMAIL,
            DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID
    };

    public static final String[] ETAB_PROJECTION = new String[]{
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry._ID,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_NOM,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TYPE,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_RNE,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_ANIMATEUR_ID,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_VILLE_ID,
    };

    private static final String[] DEPARTEMENT_COLUMNS = {
            DaneContract.AnimateurEntry.COLUMN_NOM,
            "COUNT(*) AS lesanimateurs"
    };
    AnimateursSimpleCursorTreeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_animateurs);

        ExpandableListView expandableContactListView = (ExpandableListView) findViewById(R.id.liste_animateurs);

        mAdapter = new AnimateursSimpleCursorTreeAdapter(this,
                android.R.layout.simple_expandable_list_item_1,
                android.R.layout.simple_expandable_list_item_1,
                new String[]{DaneContract.AnimateurEntry.COLUMN_NOM},
                new int[]{android.R.id.text1},
                new String[]{DaneContract.EtablissementEntry.COLUMN_NOM},
                new int[]{android.R.id.text1});

        expandableContactListView.setAdapter(mAdapter);

        android.content.Loader<Object> loader = getLoaderManager().getLoader(-1);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(-1, null, this);
        } else {
            getLoaderManager().initLoader(-1, null, this);
        }
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader for loader_id " + id);
        CursorLoader cl;
        if (id != -1) {
            // child cursor
            Uri etaburi = DaneContract.EtablissementEntry.buildEtablissements();
            String[] selectionArgs = new String[]{String.valueOf(id)};
            String selection = "("
                    + DaneContract.EtablissementEntry.COLUMN_ANIMATEUR_ID
                    + " = ? )";
            String sortOrder = DaneContract.EtablissementEntry.COLUMN_NOM
                    + " COLLATE LOCALIZED ASC";

            cl = new CursorLoader(this, etaburi, ETAB_PROJECTION,
                    selection, selectionArgs, sortOrder);
        } else {
            // group cursor
            Uri AnimateursUri = DaneContract.AnimateurEntry.buildAnimateurs();
            String selection = "((" + DaneContract.AnimateurEntry.COLUMN_NOM
                    + " NOTNULL) AND ("
                    + DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID + ">0))";
            String sortOrder = DaneContract.AnimateurEntry.COLUMN_NOM
                    + " COLLATE LOCALIZED ASC";
            cl = new CursorLoader(this, AnimateursUri, ANIMATEURS_COLUMNS,
                    selection, null, sortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }


//    @Override
//    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.d(LOG_TAG, "onCreateLoader for loader_id " + id);
//        CursorLoader cl;
//        if (id != -1) {
//            // child cursor
//            Uri etaburi = DaneContract.EtablissementEntry.buildEtablissements();
//            String[] selectionArgs = new String[]{String.valueOf(id)};
//            String selection = "("
//                    + DaneContract.EtablissementEntry.COLUMN_ANIMATEUR_ID
//                    + " = ? )";
//            String sortOrder = DaneContract.EtablissementEntry.COLUMN_NOM
//                    + " COLLATE LOCALIZED ASC";
//
//            cl = new CursorLoader(this, etaburi, ETAB_PROJECTION,
//                    selection, selectionArgs, sortOrder);
//        } else {
//            // group cursor
//            Uri AnimateursUri = DaneContract.AnimateurEntry.buildAnimateurs();
//            String selection = "((" + DaneContract.AnimateurEntry.COLUMN_NOM
//                    + " NOTNULL) AND ("
//                    + DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID + ">0))";
//            String sortOrder = DaneContract.AnimateurEntry.COLUMN_NOM
//                    + " COLLATE LOCALIZED ASC";
//            cl = new CursorLoader(this, AnimateursUri, ANIMATEURS_COLUMNS,
//                    selection, null, sortOrder);
//        }
//        return cl;
//    }
//
//
//    @Override
//    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
//        int id = loader.getId();
//        Log.d(LOG_TAG, "onLoadFinished() for loader_id " + id);
//        if (id != -1) {
//            // child cursor
//            if (!data.isClosed()) {
//                Log.d(LOG_TAG, "data.getCount() " + data.getCount());
//
//                HashMap<Integer, Integer> groupMap = mAdapter.getGroupMap();
//                try {
//                    int groupPos = groupMap.get(id);
//                    Log.d(LOG_TAG, "onLoadFinished() for groupPos " + groupPos);
//                    mAdapter.setChildrenCursor(groupPos, data);
//                } catch (NullPointerException e) {
//                    Log.w(LOG_TAG,
//                            "Adapter expired, try again on the next query: "
//                                    + e.getMessage());
//                }
//            }
//        } else {
//            mAdapter.setGroupCursor(data);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(android.content.Loader<Cursor> loader) {
//        int id = loader.getId();
//        Log.d(LOG_TAG, "onLoaderReset() for loader_id " + id);
//        if (id != -1) {
//            // child cursor
//            try {
//                mAdapter.setChildrenCursor(id, null);
//            } catch (NullPointerException e) {
//                Log.w(LOG_TAG, "Adapter expired, try again on the next query: "
//                        + e.getMessage());
//            }
//        } else {
//            mAdapter.setGroupCursor(null);
//        }
//    }
}


