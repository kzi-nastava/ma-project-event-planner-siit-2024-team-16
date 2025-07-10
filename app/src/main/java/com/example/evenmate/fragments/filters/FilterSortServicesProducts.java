package com.example.evenmate.fragments.filters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.evenmate.R;


public class FilterSortServicesProducts extends Fragment {

    public FilterSortServicesProducts() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_sort_services_products, container, false);
    }
}