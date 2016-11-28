package com.creteil.com.danecreteil.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by Mohammed on 27/11/2016.
 */

public class VillesActivity extends AppCompatActivity {
    private final String LOG_TAG = VillesActivity.class.getSimpleName();
    private final String VILLES_TAG = "VILLES_PAR_DEPARTEMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_villes);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new VillesFragment(), VILLES_TAG)
                    .commit();
        }
    }
//
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
}
