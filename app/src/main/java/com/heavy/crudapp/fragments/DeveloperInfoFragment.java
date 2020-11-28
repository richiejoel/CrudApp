package com.heavy.crudapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heavy.crudapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeveloperInfoFragment extends Fragment {

    public DeveloperInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_developer_info, container, false);
    }
}
