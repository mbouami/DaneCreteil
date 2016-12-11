package com.creteil.com.danecreteil.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Mohammed on 10/12/2016.
 */

public class EtablissementsparNomActivity extends AppCompatActivity implements ListeEtabParNomFragment.Callback{
    private final String LOG_TAG = EtablissementsparNomActivity.class.getSimpleName();
    private final String ETAB_PAR_NOM_TAG = "ETABLISSEMENTS_PAR_NOM";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etab);
        Log.d(LOG_TAG, "onCreate : ");
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(ListeEtabParNomFragment.ETAB_URI,getIntent().getData());
            ListeEtabParNomFragment fragment = new ListeEtabParNomFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(Uri etabUri) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(DetailFragment.DETAIL_URI, etabUri);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_etab_par_nom, fragment)
                .commit();
    }
}
