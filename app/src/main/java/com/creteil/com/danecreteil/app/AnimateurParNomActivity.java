package com.creteil.com.danecreteil.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by mbouami on 28/12/2016.
 */

public class AnimateurParNomActivity extends AppCompatActivity implements ListeAnimParNomFragment.Callback {
    private final String LOG_TAG = AnimateurParNomActivity.class.getSimpleName();
    private final String ANIM_PAR_NOM_TAG = "ANIMATEUR_PAR_NOM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etab);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(ListeAnimParNomFragment.ANIMATEUR_URI,getIntent().getData());
            ListeAnimParNomFragment fragment = new ListeAnimParNomFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(Uri animUri) {
        Intent intent = new Intent(this, EtablissementActivity.class)
                .setData(animUri);
        startActivity(intent);
    }
}
