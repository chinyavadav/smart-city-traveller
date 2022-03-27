package com.smartcitytraveller.mobile.ui.panick;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartcitytraveller.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class NextOfKinFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_next_of_kin, container, false);
    }
}