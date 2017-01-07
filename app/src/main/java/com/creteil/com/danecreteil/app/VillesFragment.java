package com.creteil.com.danecreteil.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.creteil.com.danecreteil.app.service.DaneService;
import com.creteil.com.danecreteil.app.service.DaneServiceAdapter;

/**
 * Created by Mohammed on 27/11/2016.
 */

public class VillesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = VillesFragment.class.getSimpleName();

    VillesAdapter mVillesAdapter;
    ListView listView = null;
    private static final int VILLES_LOADER = 0;
    private String choix_depart;
    RadioGroup liste_depart;

    static String Villeencours = null;
    private String BASE_URL ="http://www.bouami.fr/gestionetabs/web/listedetailvilles";

    private static final String[] VILLES_COLUMNS = {
            DaneContract.VilleEntry.TABLE_NAME + "." + DaneContract.VilleEntry._ID,
            DaneContract.VilleEntry.COLUMN_VILLE_NOM,
            DaneContract.VilleEntry.COLUMN_VILLE_DEPARTEMENT,
            DaneContract.VilleEntry.COLUMN_VILLE_ID
    };

    static final int COL_VILLE_ID = 0;
    static final int COL_VILLE_NOM = 1;
    static final int COL_VILLE_DEPARTEMENT = 2;
    static final int COL_VILLE_BASE_ID = 3;


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri villeUri);
    }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.villefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVillesAdapter = new VillesAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_villes, container, false);
        SelectDepart(rootView);
        liste_depart = (RadioGroup) rootView.findViewById(R.id.liste_departement);
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
                    ((Callback) getActivity())
                            .onItemSelected(DaneContract.EtablissementEntry.buildEtablissementParVille(cursor.getString(COL_VILLE_ID)));
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

    @Override
    public void onResume() {
        super.onResume();
//        Log.d(LOG_TAG, "onResume : ");
        SelectDepart(getView());
    }


    private void SelectDepart(View view) {
        switch (Utility.getPreferredDepart(getActivity())) {
            case "77" :
                RadioButton selection77 = (RadioButton) view.findViewById(R.id.dep77);
                selection77.setChecked(true);
                choix_depart = "77";
                break;
            case "93" :
                RadioButton selection93 = (RadioButton) view.findViewById(R.id.dep93);
                selection93.setChecked(true);
                choix_depart = "93";
                break;
            case "94" :
                RadioButton selection94 = (RadioButton) view.findViewById(R.id.dep94);
                selection94.setChecked(true);
                choix_depart = "94";
                break;
        }
        onDepartementChanged();
    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        updateVille();
//    }

    void onDepartementChanged( ) {
//        updateVille();
        getLoaderManager().restartLoader(VILLES_LOADER, null, this);
    }

    private void updateVille() {
        FetchVillesTask villesTask = new FetchVillesTask(getActivity());
        villesTask.execute();
    }

    private void updateDatabase() {
        FetchVillesTask villesTask = new FetchVillesTask(getActivity());
        villesTask.execute("update");
//        DaneServiceAdapter.syncImmediately(getActivity());
//        Intent mServiceIntent = new Intent(getActivity(), DaneService.class);
////        mServiceIntent.putExtra("EXTRA_COMPTEUR", "test");
//        getActivity().startService(mServiceIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = DaneContract.VilleEntry.COLUMN_VILLE_NOM + " ASC";
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
