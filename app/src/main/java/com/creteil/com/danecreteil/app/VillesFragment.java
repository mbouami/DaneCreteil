package com.creteil.com.danecreteil.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.data.DaneContract;

/**
 * Created by Mohammed on 27/11/2016.
 */

public class VillesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = VillesFragment.class.getSimpleName();

    VillesAdapter mVillesAdapter;
    ListView listView = null;
    private static final int VILLES_LOADER = 0;
    private String choix_depart;

    static String Villeencours = null;

    private static final String[] VILLES_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            DaneContract.VilleEntry.TABLE_NAME + "." + DaneContract.VilleEntry._ID,
            DaneContract.VilleEntry.COLUMN_NOM,
            DaneContract.VilleEntry.COLUMN_DEPARTEMENT,
            DaneContract.VilleEntry.COLUMN_VILLE_ID
    };

    static final int COL_VILLE_ID = 0;
    static final int COL_VILLE_NOM = 1;
    static final int COL_VILLE_DEPARTEMENT = 2;
    static final int COL_VILLE_BASE_ID = 3;

    public VillesFragment() {

    }

    public String getChoix_depart() {
        return choix_depart;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVillesAdapter = new VillesAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_villes, container, false);
        RadioGroup liste_depart = (RadioGroup) rootView.findViewById(R.id.liste_departement);
        liste_depart.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.dep77:
                        choix_depart = "77";
                        break;
                    case R.id.dep93:
                        choix_depart = "93";
                        break;
                    case R.id.dep94:
                        choix_depart = "94";
                        break;
                }
                onDepartementChanged();
            }
        });
        listView = (ListView) rootView.findViewById(R.id.liste_villes);
        listView.setAdapter(mVillesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Villeencours = cursor.getString(COL_VILLE_NOM);
                if (cursor != null) {
//                    Toast.makeText(getActivity(), cursor.getString(COL_VILLE_ID)+"--"+cursor.getString(COL_VILLE_NOM)+"--"+cursor.getString(COL_VILLE_BASE_ID), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), EtablissementActivity.class)
                            .setData(DaneContract.EtablissementEntry.buildEtablissementParVille(cursor.getString(COL_VILLE_ID)));
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(VILLES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onDepartementChanged( ) {
//        updateVille();
        getLoaderManager().restartLoader(VILLES_LOADER, null, this);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        updateVille();
//    }
//
//    void onDepartementChanged( ) {
//        updateVille();
//    }

    private void updateVille() {
        FetchVillesTask villesTask = new FetchVillesTask(getActivity());
        villesTask.execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = DaneContract.VilleEntry.COLUMN_NOM + " ASC";
        Uri villeParDepartementUri = DaneContract.VilleEntry.buildVilleParDepartement(choix_depart);
        return new CursorLoader(getActivity(),
                villeParDepartementUri,
                VILLES_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mVillesAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mVillesAdapter.swapCursor(null);
    }
}
