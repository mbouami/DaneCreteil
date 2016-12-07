package com.creteil.com.danecreteil.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creteil.com.danecreteil.app.data.DaneContract;

/**
 * Created by Mohammed on 03/12/2016.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;
    static final String DETAIL_URI = "URI";
    private Uri mUri;

    private ShareActionProvider mShareActionProvider;
    private static final String ETABCAST_SHARE_HASHTAG = " #EtabsDaneCreteil";
    private String mEtabSharecast;
    private String mTelEtabcast;
    private String mMailEtabcast;
    private String mAdresseEtabcast;

    private static final String[] ETAB_COLUMNS = {
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry._ID,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_NOM,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_RNE,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TYPE,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_TEL,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_FAX,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_EMAIL,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_ADRESSE,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_CP,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_VILLE_ID,
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_ETABLISSEMENT_ID,
            DaneContract.VilleEntry.TABLE_NAME + "." + DaneContract.VilleEntry.COLUMN_VILLE_NOM
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    static final int COL_ETAB_ID = 0;
    static final int COL_ETAB_NOM = 1;
    static final int COL_ETAB_RNE = 2;
    static final int COL_ETAB_TYPE = 3;
    static final int COL_ETAB_TEL = 4;
    static final int COL_ETAB_FAX = 5;
    static final int COL_ETAB_EMAIL = 6;
    static final int COL_ETAB_ADRESSE = 7;
    static final int COL_ETAB_CP = 8;
    static final int COL_ETAB_VILLE_ID = 9;
    static final int COL_ETAB_ETABLISSEMENT_ID = 10;
    static final int COL_VILLE = 11;

    private TextView mNomView;
    private TextView mRneView;
    private TextView mTelView;
    private TextView mFaxView;
    private TextView mEmailView;
    private TextView mAdresseView;



    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {;
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
        int id = item.getItemId();
        if (id == R.id.action_call) {
            startActivity(createPhoneIntent());
        }
        if (id == R.id.action_share) {
            startActivity(createShareIntent());
        }
        if (id == R.id.action_mail) {
            startActivity(createMailIntent());
//            startActivity(Intent.createChooser(createMailIntent(), mMailEtabcast));
        }
        if (id == R.id.action_map) {
            startActivity(createMapIntent());
        }
        return true;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mEtabSharecast + ETABCAST_SHARE_HASHTAG);
        return shareIntent;
    }

    private Intent createPhoneIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_DIAL);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setData(Uri.parse("tel:"+mTelEtabcast));
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return shareIntent;
    }

    private Intent createMailIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_EMAIL, mMailEtabcast);
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
            Uri updatedUri = DaneContract.EtablissementEntry.buildEtablissementParId(idEtablissement,"etab");
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
//            Log.v(LOG_TAG, "In onCreateLoader "+mUri.toString());
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    ETAB_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
//            Log.v(LOG_TAG, "In onLoadFinished "+mUri.toString()+"----"+data.getString(COL_VILLE)+"---"+data.getString(COL_ETAB_VILLE_ID));
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
        mEtabSharecast = String.format("%s", "test");
        mMailEtabcast = String.format("%s", mail);
        mTelEtabcast = String.format("%s", tel);
        mAdresseEtabcast = String.format("%s", adresse);
//        if (mShareActionProvider != null) {
//            mShareActionProvider.setShareIntent(createShareIntent());
////            mShareActionProvider.setShareIntent(createPhoneIntent());
//        }
    }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
