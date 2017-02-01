package com.creteil.com.danecreteil.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.creteil.com.danecreteil.app.data.DaneContract.AnimateurEntry;

import java.util.HashMap;


/**
 * Created by mbouami on 23/01/2017.
 */

public class AnimateursFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = getClass().getSimpleName().toString();
    ListView listView = null;
    ListeanimateursAdapter mAnimateurAdapter;
    private static final int ANIMATEURS_LOADER = 0;

    private static final String[] ANIMATEURS_COLUMNS = {
            AnimateurEntry.TABLE_NAME + "." + AnimateurEntry._ID,
            AnimateurEntry.COLUMN_NOM,
            AnimateurEntry.COLUMN_TEL,
            AnimateurEntry.COLUMN_EMAIL
    };

    public AnimateursFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ANIMATEURS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mAnimateurAdapter = new ListeanimateursAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_animateurs, container, false);
        listView = (ListView) rootView.findViewById(R.id.liste_animateurs);
        listView.setAdapter(mAnimateurAdapter);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = AnimateurEntry.COLUMN_NOM + " ASC";
        Uri animateursUri = AnimateurEntry.buildAnimateurs();
        return new CursorLoader(getActivity(),
                animateursUri,
                ANIMATEURS_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAnimateurAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAnimateurAdapter.swapCursor(null);
    }
}
