package com.creteil.com.danecreteil.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.data.DaneContract;

/**
 * Created by Mohammed on 27/11/2016.
 */

public class VillesActivity extends AppCompatActivity implements VillesFragment.Callback {
    private final String LOG_TAG = VillesActivity.class.getSimpleName();
    private final String VILLESFRAGMENT_TAG = "VILLES_PAR_DEPARTEMENT";
    private String mdepartement;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_villes);
        mdepartement = Utility.getPreferredDepart(this);
//        Log.d(LOG_TAG, "onCreate : "+mdepartement);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new VillesFragment(), VILLESFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.villeactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_departement:
                startActivity(new Intent(this, ParametresActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onItemSelected(Uri villeUri) {
//        Log.d(LOG_TAG, "onItemSelected : "+villeUri+"----"+DaneContract.EtablissementEntry.getNomEtablissementFromUri(villeUri));
            Intent intent = new Intent(this, EtablissementActivity.class)
                    .setData(villeUri);
            intent.putExtra("avecville",false);
            startActivity(intent);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        RadioGroup liste_depart = (RadioGroup) this.findViewById(R.id.liste_departement);
//        liste_depart.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                switch (checkedId) {
//                    case R.id.dep77:
//                        Toast.makeText(getBaseContext(), "dep77", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.dep93:
//                        Toast.makeText(getBaseContext(), "dep93", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.dep94:
//                        Toast.makeText(getBaseContext(), "dep94", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        });
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        String departement = Utility.getPreferredDepart(this);
//        Log.d(LOG_TAG, "onResume : "+departement+"----"+mdepartement);
////        // update the location in our second pane using the fragment manager
//        if (departement != null && !departement.equals(mdepartement)) {
////            VillesFragment ff = (VillesFragment)getSupportFragmentManager().findFragmentByTag(VILLESFRAGMENT_TAG);
////            if ( null != ff ) {
////                ff.onDepartementChanged();
////            }
//            mdepartement = departement;
//        }
//    }

}
