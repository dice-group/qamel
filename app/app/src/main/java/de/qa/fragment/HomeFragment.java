package de.qa.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.qa.R;
import de.qa.misc.Utils;
import de.qa.qa.dataextraction.DataExtraction;
import de.qa.qa.triplestore.TripleStore;
import de.qa.synchronizer.OfflineDataManager;

/**
 * Created by paramjot on 7/9/18.
 */

public class HomeFragment extends Fragment{
    View view;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new QAFragment())
                .commit();
        return view;
    }



}
