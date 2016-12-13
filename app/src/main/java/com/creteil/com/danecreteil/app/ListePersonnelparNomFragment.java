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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.creteil.com.danecreteil.app.data.DaneContract;

/**
 * Created by Mohammed on 11/12/2016.
 */

public class ListePersonnelparNomFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = ListePersonnelparNomFragment.class.getSimpleName();
    PersonnelAdapter mPersonnelAdapter;
    static final String PERSONNEL_URI = "URI";
    private Uri mUri;
    AutoCompleteTextView listepersonnel;
    private static final int PERSONNEL_PAR_NOM_LOADER = 0;
    private boolean mEtabLayout;
    private static final String[] PERSONNEL_COLUMNS = {
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry._ID,
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry.COLUMN_NOM,
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry.COLUMN_STATUT,
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry.COLUMN_ETABLISSEMENT_ID,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_NOM,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_RNE,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TYPE,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TEL,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_FAX,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_EMAIL,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_ADRESSE,
//            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_CP,
//            DaneContract.VilleEntry.TABLE_NAME + "." + DaneContract.VilleEntry.COLUMN_VILLE_NOM
    };
    static final int COL_PERSONNEL_ID = 0;
    static final int COL_PERSONNEL_NOM = 1;
    static final int COL_PERSONNEL_STATUT = 2;
    static final int COL_ETAB_ID = 3;
//    static final int COL_ETAB_NOM = 4;
//    static final int COL_ETAB_RNE = 5;
//    static final int COL_ETAB_TYPE = 6;
//    static final int COL_ETAB_TEL = 7;
//    static final int COL_ETAB_FAX = 8;
//    static final int COL_ETAB_EMAIL = 9;
//    static final int COL_ETAB_ADRESSE = 10;
//    static final int COL_ETAB_CP = 11;
//    static final int COL_VILLE = 12;

    public ListePersonnelparNomFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PERSONNEL_PAR_NOM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ListePersonnelparNomFragment.PERSONNEL_URI);
        }
        mPersonnelAdapter = new PersonnelAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.liste_personnel_par_nom, container, false);
        listepersonnel = (AutoCompleteTextView) rootView.findViewById(R.id.rechercher_personnel_par_nom);
        listepersonnel.setAdapter(mPersonnelAdapter);
        listepersonnel.setThreshold(3);
        listepersonnel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            String sortOrder = DaneContract.PersonnelEntry.COLUMN_NOM + " ASC";
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    PERSONNEL_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPersonnelAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPersonnelAdapter.swapCursor(null);
    }

    public interface Callback {
        public void onItemSelected(Uri personnelUri);
    }
}
