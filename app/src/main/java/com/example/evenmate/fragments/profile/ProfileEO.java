package com.example.evenmate.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.evenmate.R;

public class ProfileEO extends Fragment {

   public ProfileEO() {
        // Required empty public constructor
    }
    public static ProfileEO newInstance(String param1, String param2) {
        ProfileEO fragment = new ProfileEO();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_e_o, container, false);
    }
}