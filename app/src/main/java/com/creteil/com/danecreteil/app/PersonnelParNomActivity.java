package com.creteil.com.danecreteil.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Mohammed on 11/12/2016.
 */

public class PersonnelParNomActivity extends AppCompatActivity implements ListePersonnelparNomFragment.Callback {
    private final String LOG_TAG = PersonnelParNomActivity.class.getSimpleName();
    private final String ETAB_PAR_NOM_TAG = "PERSONNEL_PAR_NOM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etab);
//        Log.d(LOG_TAG, "onCreate : ");
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(ListePersonnelparNomFragment.PERSONNEL_URI,getIntent().getData());
            ListePersonnelparNomFragment fragment = new ListePersonnelparNomFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(Uri personnelUri) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(DetailFragment.DETAIL_URI, personnelUri);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_personnel_par_nom, fragment)
                .commit();
    }
}
