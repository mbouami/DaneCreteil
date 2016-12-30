package com.creteil.com.danecreteil.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.creteil.com.danecreteil.app.data.DaneContract.AnimateurEntry;

/**
 * Created by mbouami on 28/12/2016.
 */

public class ListeAnimParNomFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = ListeAnimParNomFragment.class.getSimpleName();
    static final String ANIMATEUR_URI = "URI";
    private Uri mUri;
    AutoCompleteTextView listeanims;
    private static final int ANIM_PAR_NOM_LOADER = 0;
    AnimateursAdapter mAnimsAdapter;

    private static final String[] ANIM_COLUMNS = {
            AnimateurEntry.TABLE_NAME + "." + AnimateurEntry._ID,
            AnimateurEntry.TABLE_NAME + "." + AnimateurEntry.COLUMN_NOM
    };

    static final int COL_ANIM_ID = 0;
    static final int COL_ANIM_NOM = 1;

    static String Animateurencours = null;

    public ListeAnimParNomFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ANIM_PAR_NOM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ListeAnimParNomFragment.ANIMATEUR_URI);
        }
        mAnimsAdapter = new AnimateursAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.liste_anims_par_nom, container, false);
        listeanims = (AutoCompleteTextView) rootView.findViewById(R.id.rechercher_anim_par_nom);
        listeanims.setAdapter(mAnimsAdapter);
        listeanims.setThreshold(3);
        listeanims.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Animateurencours = cursor.getString(COL_ANIM_NOM);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(DaneContract.AnimateurEntry.buildEtabParIdAnimateur(cursor.getString(COL_ANIM_ID),"etab"));
                }
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            String sortOrder = AnimateurEntry.COLUMN_NOM + " ASC";
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    ANIM_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAnimsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAnimsAdapter.swapCursor(null);
    }

    public interface Callback {
        public void onItemSelected(Uri animUri);
    }
}
