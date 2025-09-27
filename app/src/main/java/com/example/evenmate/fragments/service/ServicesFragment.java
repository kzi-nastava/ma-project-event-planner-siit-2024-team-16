package com.example.evenmate.fragments.service;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.event.EventType;
import com.example.evenmate.models.service.Service;
import com.example.evenmate.models.service.ServiceFilter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class ServicesFragment extends Fragment {
    private FragmentServicesBinding binding;
    private ServicesViewModel viewModel;
    private ServiceAdapter adapter;
    private List<Service> services = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<EventType> eventTypes = new ArrayList<>();
    private int currentPage = 0;
    private int pageSize = 10;

    private Long selectedCategoryId = null;
    private Long selectedEventTypeId = null;
    private Double minPrice = null;
    private Double maxPrice = null;
    private Boolean isAvailable = null;

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
        fetchServices();
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
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            this.categories.clear();
            this.categories.addAll(categories);
            List<String> categoryNames = new ArrayList<>();
            categoryNames.add(getString(R.string.select_category));
            for (Category c : categories) categoryNames.add(c.getName());
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.categoryDropdown.setAdapter(categoryAdapter);
        });
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            this.eventTypes.clear();
            this.eventTypes.addAll(eventTypes);
            List<String> eventTypeNames = new ArrayList<>();
            eventTypeNames.add(getString(R.string.select_event_type));
            for (EventType e : eventTypes) eventTypeNames.add(e.getName());
            ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventTypeNames);
            eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.eventTypeDropdown.setAdapter(eventTypeAdapter);
        });
    }

    private void setupUI() {
        binding.addServiceFab.setOnClickListener(v -> navigateToAddService());
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            fetchServices();
            return true;
        });
        binding.minPriceInput.setOnEditorActionListener((v, actionId, event) -> {
            fetchServices();
            return true;
        });
        binding.maxPriceInput.setOnEditorActionListener((v, actionId, event) -> {
            fetchServices();
            return true;
        });
        binding.categoryDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedCategoryId = null;
                } else {
                    selectedCategoryId = categories.get(position - 1).getId();
                }
                fetchServices();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoryId = null;
            }
        });
        binding.eventTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedEventTypeId = null;
                } else {
                    selectedEventTypeId = eventTypes.get(position - 1).getId();
                }
                fetchServices();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEventTypeId = null;
            }
        });
        binding.availableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAvailable = isChecked;
            fetchServices();
        });
        binding.nextPageButton.setOnClickListener(v -> {
            currentPage++;
            fetchServices();
        });
        binding.prevPageButton.setOnClickListener(v -> {
            if (currentPage > 0) currentPage--;
            fetchServices();
        });
        binding.availableSwitch.setChecked(true);
    }

    private void fetchServices() {
        String searchQuery = binding.searchInput.getText().toString().trim();
        minPrice = TextUtils.isEmpty(binding.minPriceInput.getText().toString()) ? null : Double.parseDouble(binding.minPriceInput.getText().toString());
        maxPrice = TextUtils.isEmpty(binding.maxPriceInput.getText().toString()) ? null : Double.parseDouble(binding.maxPriceInput.getText().toString());
        ServiceFilter filters = new ServiceFilter();
        filters.setName(searchQuery);
        filters.setMinPrice(minPrice);
        filters.setMaxPrice(maxPrice);
        filters.setCategoryId(selectedCategoryId);
        filters.setEventTypeId(selectedEventTypeId);
        filters.setIsAvailable(isAvailable);
        viewModel.fetchServices(currentPage, pageSize, filters);
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
