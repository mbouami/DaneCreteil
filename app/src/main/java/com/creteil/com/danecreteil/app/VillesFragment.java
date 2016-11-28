package com.creteil.com.danecreteil.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import com.creteil.com.danecreteil.app.data.JSONParser;
import com.creteil.com.danecreteil.app.data.DaneContract;

/**
 * Created by Mohammed on 27/11/2016.
 */

public class VillesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    VillesAdapter mVillesAdapter;
    ListView listView = null;
    private static final int VILLES_LOADER = 0;
    private String choix_depart =null;

    private static final String[] VILLES_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            DaneContract.VilleEntry.TABLE_NAME + "." + DaneContract.VilleEntry._ID,
            DaneContract.VilleEntry.COLUMN_NOM,
            DaneContract.VilleEntry.COLUMN_NOM_DEPARTEMENT,
            DaneContract.VilleEntry.COLUMN_CODE_DEPARTEMENT,
            DaneContract.VilleEntry.COLUMN_VILLE_BASE_ID
    };

    static final int COL_VILLE_ID = 0;
    static final int COL_VILLE_NOM = 1;
    static final int COL_VILLE_NOM_DEPARTEMENT = 2;
    static final int COL_VILLE_CODE_DEPARTEMENT = 3;
    static final int COL_VILLE_BASE_ID = 4;

    public VillesFragment() {

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
                updateVille();
            }
        });
        listView = (ListView) rootView.findViewById(R.id.liste_villes);
        listView.setAdapter(mVillesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Map<String, String> item = (Map<String, String>) mVillesAdapter.getItem(position);
                Toast.makeText(getActivity(), item.get("id"), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), EtabActivity.class)
//                        .putExtra("idville", item.get("id")).putExtra("nomville",item.get("nom"));
//                startActivity(intent);
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
        updateVille();
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
        villesTask.execute(choix_depart);
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

//    public class FetchVillesTask extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {
//
//        private final String LOG_TAG = FetchVillesTask.class.getSimpleName();
//        private JSONParser listevillespardepart = new JSONParser();
//        ProgressDialog pDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage("Chargement des données en cours. Merci de patienter...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//        }
//        @Override
//        protected ArrayList<Map<String, String>> doInBackground(String... params) {
//            if (params.length == 0) {
//                return null;
//            }
//            String villesJsonStr = null;
//            try {
//                final String QUERY_DEPART = params[0].toString();
//                String baseUrl = "http://www.bouami.fr/gestionetabs/web/listevilles/";
//                URL url = new URL(baseUrl.concat(QUERY_DEPART));
//                villesJsonStr = listevillespardepart.parse(url,"GET");
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//                return null;
//            }
//            try {
//                return listevillespardepart.getVilleDataFromJson(villesJsonStr,params[0].toString());
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//            return null;
//        }
//        @Override
//        protected void onPostExecute(ArrayList<Map<String, String>> result) {
//            pDialog.dismiss();
//            if (result != null) {
//                mVillesAdapter = new SimpleAdapter(getActivity(),result, R.layout.list_item_villes, new String[] { "id", "nom" },new int[] { R.id.id, R.id.nom });
//                listView.setAdapter(mVillesAdapter);
//            }
//        }
//    }
}
