package com.example.evenmate.fragments.service;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.evenmate.R;
import com.example.evenmate.adapters.ServiceManagementAdapter;
import com.example.evenmate.databinding.FragmentServicesBinding;
import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.event.EventType;
import com.example.evenmate.models.service.Service;
import com.example.evenmate.models.service.ServiceFilter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class ServicesFragment extends Fragment {
    private FragmentServicesBinding binding;
    private ServicesViewModel viewModel;
    private ServiceManagementAdapter adapter;
    private List<Service> services = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<EventType> eventTypes = new ArrayList<>();

    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 1;
    private int totalElemens = 0;

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
        adapter = new ServiceManagementAdapter(services, this::onEditService, this::onDeleteService, this::onClickService);
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
            updateIndicators();
        });
        viewModel.getTotalPages().observe(getViewLifecycleOwner(), totalPages -> {
            this.totalPages = totalPages;
            updateIndicators();
            binding.prevPageButton.setEnabled(currentPage > 0);
            binding.nextPageButton.setEnabled(currentPage < totalPages - 1);
        });
        viewModel.getTotalElements().observe(getViewLifecycleOwner(), totalElements -> {
            this.totalElemens = totalElements;
            updateIndicators();
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            this.categories.clear();
            this.categories.addAll(categories);
        });
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            this.eventTypes.clear();
            this.eventTypes.addAll(eventTypes);
        });
    }

    private void setupUI() {
        binding.addServiceFab.setOnClickListener(v -> navigateToAddService());
        binding.filtersButton.setOnClickListener(v -> showFiltersBottomSheet());
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                fetchServices();
                return true;
            }
            return false;
        });
        binding.nextPageButton.setOnClickListener(v -> {
            currentPage++;
            fetchServices();
        });
        binding.prevPageButton.setOnClickListener(v -> {
            currentPage--;
            fetchServices();
        });
    }

    private void fetchServices() {
        String searchQuery = binding.searchInput.getText().toString().trim();
        ServiceFilter filters = new ServiceFilter();
        filters.setName(searchQuery);
        filters.setMinPrice(minPrice);
        filters.setMaxPrice(maxPrice);
        filters.setCategoryId(selectedCategoryId);
        filters.setEventTypeId(selectedEventTypeId);
        filters.setIsAvailable(isAvailable);
        currentPage = 0;
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

    private void onClickService(Service service) {
        Bundle args = new Bundle();
        args.putSerializable("service_id", service.getId());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
        navController.navigate(R.id.action_servicesFragment_to_serviceDetailsFragment, args);
    }

    private void showFiltersBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_service_filters, null);
        bottomSheetDialog.setContentView(sheetView);

        Spinner categorySpinner = sheetView.findViewById(R.id.filterCategoryDropdown);
        Spinner eventTypeSpinner = sheetView.findViewById(R.id.filterEventTypeDropdown);
        TextInputEditText minPriceInput = sheetView.findViewById(R.id.filterMinPriceInput);
        TextInputEditText maxPriceInput = sheetView.findViewById(R.id.filterMaxPriceInput);
        SwitchMaterial availableSwitch = sheetView.findViewById(R.id.filterAvailableSwitch);
        MaterialButton clearButton = sheetView.findViewById(R.id.clearFiltersButton);
        MaterialButton applyButton = sheetView.findViewById(R.id.applyFiltersButton);

        List<String> categoryNames = new ArrayList<>();
        categoryNames.add(getString(R.string.select_category));
        for (Category c : categories) categoryNames.add(c.getName());
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(selectedCategoryId == null ? 0 : getCategoryIndex(selectedCategoryId));

        List<String> eventTypeNames = new ArrayList<>();
        eventTypeNames.add(getString(R.string.select_event_type));
        for (EventType e : eventTypes) eventTypeNames.add(e.getName());
        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventTypeNames);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(eventTypeAdapter);
        eventTypeSpinner.setSelection(selectedEventTypeId == null ? 0 : getEventTypeIndex(selectedEventTypeId));

        minPriceInput.setText(minPrice == null ? "" : String.valueOf(minPrice));
        maxPriceInput.setText(maxPrice == null ? "" : String.valueOf(maxPrice));
        availableSwitch.setChecked(isAvailable != null && isAvailable);

        clearButton.setOnClickListener(v -> {
            categorySpinner.setSelection(0);
            eventTypeSpinner.setSelection(0);
            minPriceInput.setText("");
            maxPriceInput.setText("");
            availableSwitch.setChecked(false);
        });

        applyButton.setOnClickListener(v -> {
            selectedCategoryId = categorySpinner.getSelectedItemPosition() == 0 ? null : categories.get(categorySpinner.getSelectedItemPosition() - 1).getId();
            selectedEventTypeId = eventTypeSpinner.getSelectedItemPosition() == 0 ? null : eventTypes.get(eventTypeSpinner.getSelectedItemPosition() - 1).getId();
            minPrice = minPriceInput.getText().toString().isEmpty() ? null : Double.parseDouble(minPriceInput.getText().toString());
            maxPrice = maxPriceInput.getText().toString().isEmpty() ? null : Double.parseDouble(maxPriceInput.getText().toString());
            isAvailable = availableSwitch.isChecked();
            fetchServices();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void updateIndicators() {
        binding.pageIndicator.setText("Page " + (currentPage + 1) + " / " + totalPages);
        int start = totalElemens == 0 ? 0 : currentPage * pageSize + 1;
        int end = Math.min((currentPage + 1) * pageSize, totalElemens);
        binding.totalElementsIndicator.setText(start + " - " + end + " / " + totalElemens);
    }

    private int getCategoryIndex(Long categoryId) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(categoryId)) return i + 1;
        }
        return 0;
    }
    private int getEventTypeIndex(Long eventTypeId) {
        for (int i = 0; i < eventTypes.size(); i++) {
            if (eventTypes.get(i).getId().equals(eventTypeId)) return i + 1;
        }
        return 0;
    }
}
