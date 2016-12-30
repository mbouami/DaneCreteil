package com.creteil.com.danecreteil.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.data.DaneContract;

public class AccueilActivity extends AppCompatActivity {
    private final String LOG_TAG =AccueilActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
    }
    @Override
    protected void onResume() {
        super.onResume();

        Button annuaire_etabs = (Button) this.findViewById(R.id.annuaire_etabs);
        Button recherche_etab = (Button) this.findViewById(R.id.recherche_etab);
        Button recherche_personnel = (Button) this.findViewById(R.id.recherche_personnel);
        Button recherche_etablissement_par_animateur = (Button) this.findViewById(R.id.recherche_etablissement_par_animateur);
        annuaire_etabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "annuaire_etabs", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), VillesActivity.class);
                startActivity(intent);
            }
        });

        recherche_etab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "recherche_etab", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), EtablissementsparNomActivity.class)
                        .setData(DaneContract.EtablissementEntry.buildEtablissements());
                startActivity(intent);
            }
        });
        recherche_personnel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "recherche_personnel", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), PersonnelParNomActivity.class)
                        .setData(DaneContract.PersonnelEntry.buildPersonnel());
                startActivity(intent);
            }
        });
        recherche_etablissement_par_animateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AnimateurParNomActivity.class)
                        .setData(DaneContract.AnimateurEntry.buildAnimateurs());
                startActivity(intent);
            }
        });

    }

}
