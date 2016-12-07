package com.creteil.com.danecreteil.app;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.creteil.com.danecreteil.app.data.DaneContract.EtablissementEntry;

/**
 * Created by Mohammed on 02/12/2016.
 */
public class EtabsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EtabsFragment.class.getSimpleName();
    EtabsAdapter mEtabsAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private boolean mEtabLayout;
    private static final int ETAB_LOADER = 0;

    static final String ETAB_URI = "URI";
    private Uri mUri;

    private static final String[] ETAB_COLUMNS = {
            EtablissementEntry.TABLE_NAME + "." + EtablissementEntry._ID,
            EtablissementEntry.TABLE_NAME + "." + EtablissementEntry.COLUMN_NOM,
            EtablissementEntry.TABLE_NAME + "." + EtablissementEntry.COLUMN_TYPE,
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    static final int COL_ETAB_ID = 0;
    static final int COL_ETAB_NOM = 1;
    static final int COL_ETAB_TYPE = 2;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri etabUri);
    }

    public EtabsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(EtabsFragment.ETAB_URI);
        }
        mEtabsAdapter = new EtabsAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.fragment_etab, container, false);
        mListView = (ListView) rootView.findViewById(R.id.liste_etabs);
        mListView.setAdapter(mEtabsAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(EtablissementEntry.buildEtablissementParId(cursor.getString(COL_ETAB_ID),"etab"));
                }
                mPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mEtabsAdapter.setUseEtabLayout(mEtabLayout);
        return rootView;
    }

    public void setUseEtabLayout(boolean EtabLayout) {
        mEtabLayout = EtabLayout;
        if (mEtabsAdapter != null) {
            mEtabsAdapter.setUseEtabLayout(mEtabLayout);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ETAB_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            String sortOrder = EtablissementEntry.COLUMN_TYPE + " ASC";
            Log.v(LOG_TAG, "In onCreateLoader "+mUri.toString());
            String mEtablissement = String.format("%s %s", "Liste des établissements à ",VillesFragment.Villeencours);
            TextView titreetabTextView = (TextView)getView().findViewById(R.id.titre_etablissement);
            titreetabTextView.setText(mEtablissement);
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    ETAB_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        Log.v(LOG_TAG, "In onLoadFinished");
//        if (!data.moveToFirst()) { return; }
//        mEtabsAdapter.swapCursor(data);

        mEtabsAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEtabsAdapter.swapCursor(null);
    }
}
