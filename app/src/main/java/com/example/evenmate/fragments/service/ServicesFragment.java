package com.example.evenmate.fragments.service;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.evenmate.R;
import com.example.evenmate.adapters.ServiceAdapter;
import com.example.evenmate.databinding.FragmentServicesBinding;
import com.example.evenmate.models.service.Service;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class ServicesFragment extends Fragment {
    private FragmentServicesBinding binding;
    private ServicesViewModel viewModel;
    private ServiceAdapter adapter;
    private List<Service> services = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentServicesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ServicesViewModel.class);
        adapter = new ServiceAdapter(services, this::onEditService, this::onDeleteService);
        binding.servicesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.servicesRecyclerView.setAdapter(adapter);
        setupObservers();
        setupUI();
        viewModel.fetchServices();
        viewModel.fetchCategories();
        viewModel.fetchEventTypes();
    }

    private void setupObservers() {
        viewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            this.services.clear();
            this.services.addAll(services);
            adapter.notifyDataSetChanged();
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupUI() {
        binding.addServiceFab.setOnClickListener(v -> navigateToAddService());
    }

    private void navigateToAddService() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
        navController.navigate(R.id.action_servicesFragment_to_addServiceFragment);
    }

    private void onEditService(Service service) {
        Bundle args = new Bundle();
        args.putSerializable("service", service);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
        navController.navigate(R.id.action_servicesFragment_to_editServiceFragment, args);
    }

    private void onDeleteService(Service service) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_service)
            .setMessage(R.string.confirm_delete_service)
            .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deleteService(service.getId()))
            .setNegativeButton(R.string.cancel, null)
            .show();
    }
}
