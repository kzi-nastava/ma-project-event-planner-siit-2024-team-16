package com.example.evenmate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.evenmate.databinding.FragmentRegisterBinding;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class RegisterFragment extends Fragment {

    private SwitchMaterial switchProvider;
    private ConstraintLayout companyInfoLayout;
    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        switchProvider = binding.switchProvider;
        companyInfoLayout = binding.companyInfoLayout;

        switchProvider.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                companyInfoLayout.setVisibility(View.VISIBLE);
            } else {
                companyInfoLayout.setVisibility(View.GONE);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}