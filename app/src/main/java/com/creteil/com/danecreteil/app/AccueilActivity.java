package com.creteil.com.danecreteil.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.creteil.com.danecreteil.app.data.JSONParser;
import com.creteil.com.danecreteil.app.service.DaneService;
import com.creteil.com.danecreteil.app.service.DaneServiceAdapter;

public class AccueilActivity extends AppCompatActivity {
    private final String LOG_TAG =AccueilActivity.class.getSimpleName();
    private String BASE_URL ="http://www.bouami.fr/gestionetabs/web/listedetailvilles";
    public final static String ETAT_BASE = "etat.base.de.donnees";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
//        FetchVillesTask villesTask = new FetchVillesTask(this);
//        villesTask.execute("Initialiser");
//        DaneServiceAdapter.syncImmediately(this);
//        Intent mServiceIntent = new Intent(AccueilActivity.this, DaneService.class);
//        mServiceIntent.putExtra(ETAT_BASE, "Initialiser");
//        startService(mServiceIntent);
        Cursor NombreetablissementCursor = getBaseContext().getContentResolver().query(
                DaneContract.EtablissementEntry.CONTENT_URI,
                new String[]{DaneContract.EtablissementEntry._ID},
                null,
                null,
                null);
        if (NombreetablissementCursor.getCount() > 0) {
            Log.d(LOG_TAG, "updateDatabase : " + NombreetablissementCursor.getCount());
        } else {
//            DaneServiceAdapter.syncImmediately(this);
            FetchVillesTask villesTask = new FetchVillesTask(AccueilActivity.this);
            villesTask.execute("update");
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        Log.d(LOG_TAG, "onPostResume : ");
//        Toast.makeText(this, "onPostResume : ", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(LOG_TAG, "onResume : ");
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
