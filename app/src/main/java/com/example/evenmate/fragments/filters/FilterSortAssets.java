package com.example.evenmate.fragments.filters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.Location;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.event.EventType;
import com.example.evenmate.models.user.ProductServiceProvider;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class FilterSortAssets extends Fragment {

    private Button availableFromButton;
    private Button availableToButton;
    private Button sortButton;
    private Switch showOnlyServicesSwitch, showOnlyProductsSwitch;
    private RangeSlider priceRangeSlider, ratingRangeSlider;
    private TextView priceValue, ratingValue;
    private TextView categoriesButton, typesButton, providersButton, locationsButton;

    private boolean[] selectedCategoriesArray, selectedTypesArray, selectedProvidersArray, selectedLocationsArray;
    private String[] categoriesNames, typesNames, providersNames, locationsNames;

    private List<Long> selectedCategories = new ArrayList<>();
    private List<Long> selectedTypes = new ArrayList<>();
    private List<Long> selectedProviders = new ArrayList<>();
    private List<String> selectedLocations = new ArrayList<>();

    private HashMap<Long, String> providersMap = new HashMap<>();
    private HashMap<Long, String> typesMap = new HashMap<>();
    private HashMap<Long, String> categoriesMap = new HashMap<>();
    private OnFilterApplyListener listener;
    private HashMap<String, String> sortOptionsMap = new HashMap<>(){{
        put("id,asc", "Date added ↑");
        put("id,desc", "Date added ↓");
        put("name,asc", "Asset name ↑");
        put("name,desc", "Asset name ↓");
        put("price,asc", "Asset price ↑");
        put("price,desc", "Asset price ↓");
        put("discount,asc", "Asset discount ↑");
        put("discount,desc", "Asset discount ↓");

    }};
    private int selectedSortIndex = 0;
    private String selectedSortKey;
    public interface OnFilterApplyListener {
        void onApplyFilters(AssetFilters filters);
    }

    public void setOnFilterApplyListener(OnFilterApplyListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_sort_assets, container, false);

        // Range sliders
        priceRangeSlider = view.findViewById(R.id.price_range_slider);
        priceValue = view.findViewById(R.id.price_value);
        priceRangeSlider.setValues(0f, 10000f);

        ratingRangeSlider = view.findViewById(R.id.rating_range_slider);
        ratingValue = view.findViewById(R.id.rating_value);
        ratingRangeSlider.setValues(0f, 5f);

        Button resetButton = view.findViewById(R.id.reset_filters_button);
        availableFromButton = view.findViewById(R.id.available_from);
        availableToButton = view.findViewById(R.id.available_to);
        showOnlyServicesSwitch = view.findViewById(R.id.show_services);
        showOnlyProductsSwitch = view.findViewById(R.id.show_products);

        categoriesButton = view.findViewById(R.id.categories_button);
        typesButton = view.findViewById(R.id.event_types_button);
        providersButton = view.findViewById(R.id.providers_button);
        locationsButton = view.findViewById(R.id.locations_button);

        sortButton = view.findViewById(R.id.sort_button);

        loadCategoriesFromBackend();
        loadProvidersFromBackend();
        loadTypesFromBackend();
        loadLocationsFromBackend();

        setupRangeSliderListeners();
        setupDatePickers();
        setupSortDialog();
        setupApplyButton(view);

        resetButton.setOnClickListener(v -> resetFilters());

        return view;
    }

    private void setupRangeSliderListeners() {
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            float min = priceRangeSlider.getValues().get(0);
            float max = priceRangeSlider.getValues().get(1);
            priceValue.setText((int) min + " - " + (int) max);
        });

        ratingRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            float min = ratingRangeSlider.getValues().get(0);
            float max = ratingRangeSlider.getValues().get(1);
            ratingValue.setText(String.format("%.1f - %.1f", min, max));
        });
    }

    private void setupDatePickers() {
        availableFromButton.setOnClickListener(v -> showDatePicker(availableFromButton));
        availableToButton.setOnClickListener(v -> showDatePicker(availableToButton));
    }

    private void showDatePicker(Button button) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (_view, year, month, day) -> button.setText(year + "-" + (month + 1) + "-" + day),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupMultiChoiceDialogs() {
        categoriesButton.setOnClickListener(v -> {
            if (categoriesNames != null)
                showMultiChoiceDialog("Select Categories", categoriesNames, selectedCategoriesArray, selectedCategories,categoriesMap, categoriesButton);
            else Toast.makeText(requireContext(), "Categories not loaded yet", Toast.LENGTH_SHORT).show();
        });

        typesButton.setOnClickListener(v -> {
            if (typesNames != null)
                showMultiChoiceDialog("Select Event Types", typesNames, selectedTypesArray, selectedTypes,typesMap, typesButton);
            else Toast.makeText(requireContext(), "Types not loaded yet", Toast.LENGTH_SHORT).show();
        });

        providersButton.setOnClickListener(v -> {
            if (providersNames != null)
                showMultiChoiceDialog("Select Providers", providersNames, selectedProvidersArray, selectedProviders,providersMap, providersButton);
            else Toast.makeText(requireContext(), "Providers not loaded yet", Toast.LENGTH_SHORT).show();
        });

        if (selectedLocationsArray != null) Arrays.fill(selectedLocationsArray, false);
        selectedLocations.clear();
        locationsButton.setText(R.string.select_location);
    }

    private void showMultiChoiceDialog(String title, String[] items, boolean[] checkedItems,
                                       List<Long> selectedIdList, HashMap<Long, String> idToNameMap,
                                       TextView targetButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        builder.setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
            Long id = (Long) idToNameMap.keySet().toArray()[which];

            if (isChecked) {
                if (!selectedIdList.contains(id)) selectedIdList.add(id);
            } else {
                selectedIdList.remove(id);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder sb = new StringBuilder();
            for (Long id : selectedIdList) {
                sb.append(idToNameMap.get(id)).append(", ");
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 2);
            targetButton.setText(sb.length() > 0 ? sb.toString() : title);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void setupSortDialog() {
        sortButton.setOnClickListener(v -> {
            String[] sortLabels =  sortOptionsMap.values().toArray(new String[0]);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setSingleChoiceItems( sortLabels, selectedSortIndex, (dialog, which) -> selectedSortIndex = which);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String label = sortLabels[selectedSortIndex];
                String key = "";
                for (HashMap.Entry<String, String> entry : sortOptionsMap.entrySet()) {
                    if (entry.getValue().equals(label)) {
                        key = entry.getKey();
                        break;
                    }
                }
                selectedSortKey = key;
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }


    private void setupApplyButton(View view) {
        Button applyButton = view.findViewById(R.id.filter_button);
        applyButton.setOnClickListener(v -> {
            if (listener != null) {
                AssetFilters data = new AssetFilters();
                double minPrice = priceRangeSlider.getValues().get(0);
                data.setMinPrice(minPrice != 0 ? minPrice : null);
                double maxPrice = priceRangeSlider.getValues().get(1);
                data.setMaxPrice(maxPrice != 10000 ? maxPrice : null);

                double minRating = ratingRangeSlider.getValues().get(0);
                data.setMinRating(minRating != 0 ? minRating : null);
                double maxRating = ratingRangeSlider.getValues().get(1);
                data.setMaxRating(maxRating != 5 ? maxRating : null);

                String dateFrom = availableFromButton.getText().toString();
                data.setDateFrom(!dateFrom.equals(getString(R.string.from_date)) ? dateFrom : null);
                String dateTo = availableToButton.getText().toString();
                data.setDateTo(!dateTo.equals(getString(R.string.to_date)) ? dateTo : null);

                data.setShowServicesOnly(showOnlyServicesSwitch.isChecked());
                data.setShowProductsOnly(showOnlyProductsSwitch.isChecked());

                data.setSelectedCategories(new ArrayList<>(selectedCategories));
                data.setSelectedTypes(new ArrayList<>(selectedTypes));
                data.setSelectedProviders(new ArrayList<>(selectedProviders));
                data.setSelectedLocations(new ArrayList<>(selectedLocations));

                data.setSortOption(selectedSortKey);

                listener.onApplyFilters(data);
            }
        });
    }

    private void resetFilters() {
        priceRangeSlider.setValues(0f, 10000f);
        priceValue.setText("0 - 10000");
        ratingRangeSlider.setValues(0f, 5f);
        ratingValue.setText("0.0 - 5.0");
        showOnlyServicesSwitch.setChecked(false);
        showOnlyProductsSwitch.setChecked(false);
        availableFromButton.setText(R.string.from_date);
        availableToButton.setText(R.string.to_date);

        if (selectedCategoriesArray != null) Arrays.fill(selectedCategoriesArray, false);
        selectedCategories.clear();
        categoriesButton.setText(R.string.select_category);

        if (selectedTypesArray != null) Arrays.fill(selectedTypesArray, false);
        selectedTypes.clear();
        typesButton.setText(R.string.select_event_type);

        if (selectedProvidersArray != null) Arrays.fill(selectedProvidersArray, false);
        selectedProviders.clear();
        providersButton.setText(R.string.select_provider);

        if (selectedLocationsArray != null) Arrays.fill(selectedLocationsArray, false);
        selectedLocations.clear();
        locationsButton.setText(R.string.select_location);

        selectedSortIndex = 0;
        sortButton.setText(R.string.sort);
    }

    private void loadTypesFromBackend() {
        ClientUtils.eventTypeService.getActiveTypes(true).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PaginatedResponse<EventType>> call, retrofit2.Response<PaginatedResponse<EventType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventType> typesList = response.body().getContent();

                    typesNames = new String[typesList.size()];
                    selectedTypesArray = new boolean[typesList.size()];

                    for (int i = 0; i < typesList.size(); i++) {
                        EventType t = typesList.get(i);
                        typesNames[i] = t.getName();
                        selectedTypesArray[i] = false;
                        typesMap.put(t.getId(), t.getName());
                    }

                    setupMultiChoiceDialogs();
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<EventType>> call, Throwable throwable) {
                Toast.makeText(requireContext(), "Error fetching types: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProvidersFromBackend() {
        ClientUtils.providerService.getAll().enqueue(new Callback<List<ProductServiceProvider>>() {
            @Override
            public void onResponse(Call<List<ProductServiceProvider>> call, retrofit2.Response<List<ProductServiceProvider>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductServiceProvider> providersList = response.body();

                    providersNames = new String[providersList.size()];
                    selectedProvidersArray = new boolean[providersList.size()];

                    for (int i = 0; i < providersList.size(); i++) {
                        String fullName = providersList.get(i).getFirstName() + " " + providersList.get(i).getLastName();
                        providersNames[i] = fullName;
                        selectedProvidersArray[i] = false;
                        providersMap.put(providersList.get(i).getId(), fullName);
                    }

                    setupMultiChoiceDialogs();
                }
            }

            @Override
            public void onFailure(Call<List<ProductServiceProvider>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error fetching providers: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLocationsFromBackend() {
        ClientUtils.addressService.getAllAddresses().enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, retrofit2.Response<List<Location>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Location> locationsList = response.body();

                    locationsNames = new String[locationsList.size()];
                    selectedLocationsArray = new boolean[locationsList.size()];

                    for (int i = 0; i < locationsList.size(); i++) {
                        Location loc = locationsList.get(i);
                        locationsNames[i] = loc.getCity() != null ? loc.getCity() + ", " + loc.getCountry() : loc.getCountry();
                        selectedLocationsArray[i] = false;
                    }

                    locationsButton.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Select Locations");

                        builder.setMultiChoiceItems(locationsNames, selectedLocationsArray, (dialog, which, isChecked) -> {
                            selectedLocationsArray[which] = isChecked;
                        });

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            selectedLocations.clear();
                            for (int i = 0; i < locationsNames.length; i++) {
                                if (selectedLocationsArray[i]) selectedLocations.add(locationsNames[i]);
                            }
                            locationsButton.setText(selectedLocations.isEmpty() ? "Select Locations" : TextUtils.join(", ", selectedLocations));
                        });

                        builder.setNegativeButton("Cancel", null);
                        builder.show();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error fetching locations: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadCategoriesFromBackend() {
        ClientUtils.categoryService.getCategories(null,null).enqueue(new Callback<PaginatedResponse<Category>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Category>> call, retrofit2.Response<PaginatedResponse<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categoryList = response.body().getContent();

                    categoriesNames = new String[categoryList.size()];
                    selectedCategoriesArray = new boolean[categoryList.size()];

                    for (int i = 0; i < categoryList.size(); i++) {
                        Category c = categoryList.get(i);
                        categoriesNames[i] = c.getName();
                        selectedCategoriesArray[i] = false;
                        categoriesMap.put(c.getId(), c.getName());
                    }

                    setupMultiChoiceDialogs();
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<Category>> call, Throwable throwable) {
                Toast.makeText(requireContext(), "Error fetching categories: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
