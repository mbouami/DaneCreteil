package com.creteil.com.danecreteil.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AccueilActivity extends AppCompatActivity {
    private final String LOG_TAG =AccueilActivity.class.getSimpleName();
//    private String BASE_URL ="http://www.bouami.fr/gestionetabs/web/listedetailvilles";
//    private String BASE_URL = DaneContract.BASE_URL + "/listedetailvilles";
    public final static String ETAT_BASE = "etat.base.de.donnees";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        Cursor NombreetablissementCursor = getBaseContext().getContentResolver().query(
                DaneContract.EtablissementEntry.CONTENT_URI,
                new String[]{DaneContract.EtablissementEntry._ID},
                null,
                null,
                null);
        if (!(NombreetablissementCursor.getCount() > 0)) {
            final Context mContext = AccueilActivity.this;
            final ProgressDialog pDialog = new ProgressDialog(mContext);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(DaneContract.BASE_URL_LISTE_DETAIL_VILLES, new BaseJsonHttpResponseHandler<JSONObject>() {
                @Override
                public void onStart() {
                    pDialog.setMessage("Synchronisation des données en cours. Merci de patienter...");
                    pDialog.show();
                }

                @Override
                public void onFinish() {
                    pDialog.hide();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                    DaneContract.initialiserBase(mContext);
                    try {
                        DaneContract.getVillesDataFromJson(mContext,rawJsonResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {
                    pDialog.hide();
                    // When Http response code is '404'
                    if (statusCode == 404) {
                        Toast.makeText(mContext,
                                "Ressorces de la requête non trouvées",
                                Toast.LENGTH_LONG).show();
                    }
                    // When Http response code is '500'
                    else if (statusCode == 500) {
                        Toast.makeText(mContext,
                                "Lz serveur ne répond pas",
                                Toast.LENGTH_LONG).show();
                    }
                    // When Http response code other than 404, 500
                    else {
                        Toast.makeText(mContext,
                                "Erreurs \n Sources d'erreurs: \n1. Pas de connection à internet\n2. Application non déployée sur le serveur\n3. Le serveur Web est à l'arrêt\n HTTP Status code : "
                                        + statusCode, Toast.LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return null;
                }
            });

        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(LOG_TAG, "onResume : ");
        Button annuaire_etabs = (Button) this.findViewById(R.id.annuaire_etabs);
        Button recherche_etab = (Button) this.findViewById(R.id.recherche_etab);
        Button recherche_personnel = (Button) this.findViewById(R.id.recherche_personnel);
        Button recherche_etablissement_par_animateur = (Button) this.findViewById(R.id.recherche_etablissement_par_animateur);
        Button liste_animateurs = (Button) this.findViewById(R.id.liste_animateurs);
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

        liste_animateurs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DepartementsActivity.class);
                startActivity(intent);
            }
        });
    }

}
