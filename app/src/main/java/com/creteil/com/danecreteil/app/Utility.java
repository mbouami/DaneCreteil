package com.creteil.com.danecreteil.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Mohammed on 09/12/2016.
 */

public class Utility {

    public static String getPreferredDepart(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_depart_key),
                context.getString(R.string.pref_depart_label_default));
    }

    public static boolean is77(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_depart_key),
                context.getString(R.string.pref_depart_77))
                .equals(context.getString(R.string.pref_depart_77));
    }

    public static boolean is93(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_depart_key),
                context.getString(R.string.pref_depart_93))
                .equals(context.getString(R.string.pref_depart_93));
    }

    public static boolean is94(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_depart_key),
                context.getString(R.string.pref_depart_94))
                .equals(context.getString(R.string.pref_depart_94));
    }

    static String formatDepartement(Context context, String departement) {
        String depart = departement;
        return context.getString(R.string.format_departement,depart);
    }

    static String formatAdresse(Context context, String ad) {
        String adresse = ad;
        return context.getString(R.string.format_departement,adresse);
    }

    static String formatListeVilles(Context context, String ad) {
        String villes = ad;
        return context.getString(R.string.format_ville,villes);
    }

    static String formatListeEtabs(Context context, String ad) {
        String etabs = ad;
        return context.getString(R.string.format_etab,etabs);
    }

    static String formatDetailEtabs(Context context, String ad) {
        String detailetab = ad;
        return context.getString(R.string.format_detail_etab,detailetab);
    }
}
