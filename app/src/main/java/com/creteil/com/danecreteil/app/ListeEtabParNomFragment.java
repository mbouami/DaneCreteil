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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.creteil.com.danecreteil.app.data.DaneContract;

/**
 * Created by Mohammed on 10/12/2016.
 */

public class ListeEtabParNomFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = ListeEtabParNomFragment.class.getSimpleName();
    EtabsAvecVilleAdapter mEtabsAdapter;
    static final String ETAB_URI = "URI";
    private Uri mUri;
    AutoCompleteTextView listeetabs;
    private static final int ETAB_PAR_NOM_LOADER = 0;
    private boolean mEtabLayout;

    private static final String[] ETAB_COLUMNS = {
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry._ID,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_NOM,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TYPE,
//            DaneContract.VilleEntry.TABLE_NAME + "." + DaneContract.VilleEntry.COLUMN_VILLE_NOM
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    static final int COL_ETAB_ID = 0;
    static final int COL_ETAB_NOM = 1;
    static final int COL_ETAB_TYPE = 2;
//    static final int COL_VILLE = 3;

    public ListeEtabParNomFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(LOG_TAG, "onCreate : ");
//        mEtabsAdapter = new EtabsAdapter(getActivity(),null,0);
//        listeetabs = (AutoCompleteTextView) getActivity().findViewById(R.id.rechercher_etabs_par_nom);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ETAB_PAR_NOM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ListeEtabParNomFragment.ETAB_URI);
        }
        mEtabsAdapter = new EtabsAvecVilleAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.liste_etab_par_nom, container, false);
        listeetabs = (AutoCompleteTextView) rootView.findViewById(R.id.rechercher_etabs_par_nom);
        listeetabs.setAdapter(mEtabsAdapter);
        listeetabs.setThreshold(3);
        listeetabs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(DaneContract.PersonnelEntry.buildPersonnelParIdEtab(cursor.getString(COL_ETAB_ID),"etab"));
                }
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Restore the previously serialized activated item position.
//        Log.d(LOG_TAG, "onViewCreated : ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.v(LOG_TAG, "In onCreateLoader "+mUri.toString());
        if ( null != mUri ) {
            String sortOrder = DaneContract.EtablissementEntry.COLUMN_TYPE + " ASC";
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
//        Log.v(LOG_TAG, "In onLoadFinished "+data.getCount());
        mEtabsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEtabsAdapter.swapCursor(null);
    }

    public interface Callback {
        public void onItemSelected(Uri villeUri);
    }

    public void setUseEtabLayout(boolean EtabLayout) {
        mEtabLayout = EtabLayout;
        if (mEtabsAdapter != null) {
            mEtabsAdapter.setUseEtabLayout(mEtabLayout);
        }
    }
}
