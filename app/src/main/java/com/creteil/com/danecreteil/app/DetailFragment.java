package com.creteil.com.danecreteil.app;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Mohammed on 03/12/2016.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    PersonnelAdapter mPersonnelAdapter;
    private ListView mListPersonnelView;

    private ShareActionProvider mShareActionProvider;
    private static final String ETABCAST_SHARE_HASHTAG = " #EtabsDaneCreteil";
    private String mNomEtabcast;
    private String mEtabSharecast;
    private String mTelEtabcast;
    private String mMailEtabcast;
    private String mAdresseEtabcast;
    private String mIdEtab;
    private String mIdEtabcast;

    private static final String[] PERSONNEL_COLUMNS = {
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry._ID,
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry.COLUMN_NOM,
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry.COLUMN_STATUT,
            DaneContract.PersonnelEntry.TABLE_NAME + "." + DaneContract.PersonnelEntry.COLUMN_ETABLISSEMENT_ID,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_NOM,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_RNE,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TYPE,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TEL,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_FAX,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_EMAIL,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_ADRESSE,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_CP,
            DaneContract.VilleEntry.TABLE_NAME + "." + DaneContract.VilleEntry.COLUMN_VILLE_NOM,
            DaneContract.AnimateurEntry.TABLE_NAME + "." + DaneContract.AnimateurEntry.COLUMN_NOM,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_ETABLISSEMENT_ID
    };
    static final int COL_PERSONNEL_ID = 0;
    static final int COL_PERSONNEL_NOM = 1;
    static final int COL_PERSONNEL_STATUT = 2;
    static final int COL_ETAB_ID = 3;
    static final int COL_ETAB_NOM = 4;
    static final int COL_ETAB_RNE = 5;
    static final int COL_ETAB_TYPE = 6;
    static final int COL_ETAB_TEL = 7;
    static final int COL_ETAB_FAX = 8;
    static final int COL_ETAB_EMAIL = 9;
    static final int COL_ETAB_ADRESSE = 10;
    static final int COL_ETAB_CP = 11;
    static final int COL_VILLE = 12;
    static final int COL_ANIMATEUR = 13;
    static final int COL_ETABLISSEMENT_ID = 14;

    private TextView mNomView;
    private TextView mRneView;
    private TextView mTelView;
    private TextView mFaxView;
    private TextView mEmailView;
    private TextView mAdresseView;
    private TextView mAnimateurView;



    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPersonnelAdapter = new PersonnelAdapter(getActivity(), null, 0);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail_etab, container, false);
        mNomView = (TextView) rootView.findViewById(R.id.detail_nom_textview);
        mRneView = (TextView) rootView.findViewById(R.id.detail_rne_textview);
        mTelView = (TextView) rootView.findViewById(R.id.detail_tel_textview);
        mFaxView = (TextView) rootView.findViewById(R.id.detail_fax_textview);
        mEmailView = (TextView) rootView.findViewById(R.id.detail_email_textview);
        mAdresseView = (TextView) rootView.findViewById(R.id.detail_adresse_textview);
        mAnimateurView = (TextView) rootView.findViewById(R.id.detail_animateur_textview);
        mListPersonnelView = (ListView) rootView.findViewById(R.id.liste_personnel);
        mListPersonnelView.setAdapter(mPersonnelAdapter);
        mListPersonnelView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String IdPersonnel = cursor.getString(COL_PERSONNEL_ID) + "---"+cursor.getString(COL_PERSONNEL_NOM)+ "---"+cursor.getString(COL_PERSONNEL_STATUT);
                if (cursor != null) {
                    Log.d(LOG_TAG, "IDPersonnel : "+IdPersonnel);
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailetab, menu);
        // Retrieve the share menu item
        MenuItem menuShareItem = menu.findItem(R.id.action_share);
        MenuItem menuCallItem = menu.findItem(R.id.action_call);

//        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuShareItem);
//        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuCallItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
//        if (mEtabSharecast != null) {
//            mShareActionProvider.setShareIntent(createShareIntent());
//        }
//        if (mTelEtabcast != null) {
//            mShareActionProvider.setShareIntent(createPhoneIntent());
//        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_call:
                startActivity(createPhoneIntent());
                return true;
            case R.id.action_share:
                startActivity(createShareIntent());
                return true;
            case R.id.action_mail:
                startActivity(createMailIntent());
                return true;
            case R.id.action_map:
                startActivity(createMapIntent());
                return true;
            case R.id.action_maj:
                majetab();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void majetab() {
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(DaneContract.BASE_URL_DETAIL_ETAB+"/"+mIdEtabcast, new BaseJsonHttpResponseHandler<JSONObject>() {
            @Override
            public void onStart() {
                pDialog.setMessage("Mise à jour de l'établissement en cours. Merci de patienter...");
                pDialog.show();
            }

            @Override
            public void onFinish() {
                pDialog.hide();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                long etablissementId = 0;
                // First, check if the location with this city name exists in the db
                Cursor etablissementCursor = getContext().getContentResolver().query(
                        DaneContract.EtablissementEntry.CONTENT_URI,
                        new String[]{DaneContract.EtablissementEntry._ID},
                        DaneContract.EtablissementEntry._ID + " = ?",
                        new String[]{mIdEtab},
                        null);
                if (etablissementCursor.moveToFirst()) {
                    int etablissementIdIndex = etablissementCursor.getColumnIndex(DaneContract.EtablissementEntry._ID);
                    etablissementId = etablissementCursor.getLong(etablissementIdIndex);
                }
                DaneContract.delPersonnel(getContext(),mIdEtab);
                try {
                    JSONObject detailetabJson = new JSONObject(rawJsonResponse);
                    JSONArray listePersonnel = detailetabJson.getJSONArray("personnel");
                    for(int j = 0; j < listePersonnel.length(); j++) {
                        JSONObject lepersonnel = listePersonnel.getJSONObject(j);
                        long insertedPersonnel = DaneContract.addPersonnelJson(getContext(),lepersonnel,etablissementId);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {
                pDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getContext(),
                            "Ressorces de la requête non trouvées",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getContext(),
                            "Lz serveur ne répond pas",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getContext(),
                            "Erreurs \n Sources d'erreurs: \n1. Pas de connection à internet\n2. Application non déployée sur le serveur\n3. Le serveur Web est à l'arrêt\n HTTP Status code : "
                                    + statusCode, Toast.LENGTH_LONG)
                            .show();
                }
            }

            @Override
            protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Carte visite : "+mNomEtabcast);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mEtabSharecast);
        return shareIntent;
    }

    private Intent createPhoneIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_DIAL);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setData(Uri.parse("tel:"+mTelEtabcast));
//        if (ActivityCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            return null;
//        }
        return shareIntent;
    }

    private Intent createMailIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { mMailEtabcast });
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Demande de rendez-vous");
        return shareIntent;
    }

    private Intent createMapIntent() {
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", mAdresseEtabcast)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        return intent;
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
//        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onEtablissementChanged( String idEtablissement ) {
        if (null != mUri) {
//            Uri updatedUri = DaneContract.EtablissementEntry.buildEtablissementParId(idEtablissement,"etab");
            Uri updatedUri = DaneContract.PersonnelEntry.buildPersonnelParIdEtab(idEtablissement,"etab");
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
//            Log.v(LOG_TAG, "In onCreateLoader "+mUri.toString());
            String sortOrder = DaneContract.PersonnelEntry.COLUMN_PERSONNEL_ID + " ASC";
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
        if (data != null && data.moveToFirst()) {
        mIdEtabcast = data.getString(COL_ETABLISSEMENT_ID);
        mIdEtab = data.getString(COL_ETAB_ID);
//           Log.v(LOG_TAG, "In onLoadFinished "+mUri.toString()+"----"+data.getString(COL_PERSONNEL_STATUT)+"---"+data.getString(COL_PERSONNEL_NOM));
        String nom = data.getString(COL_ETAB_TYPE) +" "+data.getString(COL_ETAB_NOM);
        mNomView.setText(nom);
        String rne = data.getString(COL_ETAB_RNE);
        mRneView.setText("RNE : "+rne);
        String tel = data.getString(COL_ETAB_TEL);
        mTelView.setText("Tél : "+tel);
        String fax = data.getString(COL_ETAB_FAX);
        mFaxView.setText("Fax : "+fax);
        String mail = data.getString(COL_ETAB_EMAIL);
        mEmailView.setText("Mail : "+mail);
        String adresse = data.getString(COL_ETAB_ADRESSE)+ " "+data.getString(COL_ETAB_CP)+" "+data.getString(COL_VILLE);
        mAdresseView.setText("Adresse : "+adresse);
        String animateur = (data.getString(COL_ANIMATEUR)==null)?" ":data.getString(COL_ANIMATEUR);
        mAnimateurView.setText("Animateur : "+animateur);
        mNomEtabcast = nom;
        mEtabSharecast = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s", nom,"RNE : "+rne,"Tél : "+tel,"Fax : "+fax,"Mail : "+mail,"Adresse : "+adresse,"Animateur : "+animateur);
        mMailEtabcast = String.format("%s", mail);
        mTelEtabcast = String.format("%s", tel);
        mAdresseEtabcast = String.format("%s", adresse);
        mPersonnelAdapter.swapCursor(data);
//        if (mShareActionProvider != null) {
//            mShareActionProvider.setShareIntent(createShareIntent());
////            mShareActionProvider.setShareIntent(createPhoneIntent());
//        }
    }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPersonnelAdapter.swapCursor(null);
    }
}
