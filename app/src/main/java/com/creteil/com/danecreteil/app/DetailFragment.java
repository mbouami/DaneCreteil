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
            DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry.COLUMN_ETABLISSEMENT_ID
//            DaneContract.VilleEntry.TABLE_NAME + "." + DaneContract.VilleEntry.COLUMN_NOM
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
    static final int COL_ETAB_VILLE = 11;

    private TextView mNomView;
    private TextView mRneView;
    private TextView mTelView;
    private TextView mFaxView;
    private TextView mEmailView;
    private TextView mAdresseView;
    private TextView mTypeView;



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
        mTypeView = (TextView) rootView.findViewById(R.id.detail_type_textview);
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

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuShareItem);
//        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuCallItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mEtabSharecast != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
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
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ceci est un message de test.");
        return shareIntent;
    }

    private Intent createMapIntent() {
        Uri location = Uri.parse("geo:0,0?q=+"+"adresse");
        Intent shareIntent = new Intent(Intent.ACTION_VIEW,location);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            Log.v(LOG_TAG, "In onCreateLoader "+mUri.toString());
//            Cursor etablissementCursor = mContext.getContentResolver().query(
//                    DaneContract.EtablissementEntry.CONTENT_URI,
//                    new String[]{DaneContract.EtablissementEntry._ID},
//                    DaneContract.EtablissementEntry.COLUMN_ETABLISSEMENT_ID + " = ?",
//                    new String[]{etab_id},
//                    null);
//            return new CursorLoader(
//                    getActivity(),
//                    DaneContract.EtablissementEntry.CONTENT_URI,
//                    ETAB_COLUMNS,
//                    DaneContract.EtablissementEntry.TABLE_NAME + "." + DaneContract.EtablissementEntry._ID + " = ?",
//                    new String[]{DaneContract.EtablissementEntry.getEtablissementFromUri(mUri)},
////                    new String[]{String.valueOf(25)},
//                    null
//            );
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
        Log.v(LOG_TAG, "In onLoadFinished "+mUri.toString());
//        String nom = data.getString(COL_ETAB_NOM);
        mNomView.setText(data.getString(data.getColumnIndex("nom"))+ data.getColumnName(COL_ETAB_RNE)+"---"+data.getCount());
//        String rne = data.getString(COL_ETAB_RNE);
//        mRneView.setText(rne);
//        mRneView.setText(data.getString(COL_ETAB_RNE));
//        String tel = data.getString(COL_ETAB_TEL);
//        mTelView.setText(tel);
//        String fax = data.getString(COL_ETAB_FAX);
//        mFaxView.setText(fax);
//        String mail = data.getString(COL_ETAB_EMAIL);
//        mEmailView.setText(mail);
//        String adresse = data.getString(COL_ETAB_ADRESSE)+ " "+data.getString(COL_ETAB_CP)+" "+data.getString(COL_ETAB_VILLE);
//        mAdresseView.setText(adresse);
//        mTelEtabcast = tel;
//        mMailEtabcast = mail;
//        mAdresseEtabcast = adresse;
//        Toast.makeText(getActivity(), adresse, Toast.LENGTH_LONG).show();
        mEtabSharecast = String.format("%s", "test");
        mTelEtabcast = String.format("%s", "0148416064");
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
//            mShareActionProvider.setShareIntent(createPhoneIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
