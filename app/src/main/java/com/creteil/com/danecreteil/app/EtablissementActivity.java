package com.creteil.com.danecreteil.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Mohammed on 02/12/2016.
 */
public class EtablissementActivity extends AppCompatActivity implements EtabsFragment.Callback {
    private final String LOG_TAG = EtablissementActivity.class.getSimpleName();
    private static final String ETABFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etab);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(EtabsFragment.ETAB_URI,getIntent().getData());
            EtabsFragment fragment = new EtabsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
    }
}
