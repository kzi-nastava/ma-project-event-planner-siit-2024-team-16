package com.example.evenmate.fragments.filters;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.DatePickerDialog;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.event.EventType;
import com.example.evenmate.models.user.EventOrganizer;
import com.example.evenmate.models.Location;
import com.google.android.material.slider.RangeSlider;

import retrofit2.Call;
import retrofit2.Callback;

public class FilterSortEvents extends Fragment {

    private Button resetButton, dateFrom, dateTo;
    private Switch showInPast;
    private RangeSlider guestsRangeSlider, ratingRangeSlider;
    private TextView guestsValue, ratingValue;
    private OnFilterApplyListener listener;
    private TextView typesButton, organizersButton, locationsButton;
    private boolean[] selectedTypesArray, selectedOrganizersArray, selectedLocationsArray;
    private String[] locationsNames;
    private HashMap<Long, String> typesMap = new HashMap<>();
    private HashMap<Long, String> organizersMap = new HashMap<>();
    private String[] typesNames;
    private String[] organizersNames;
    private List<Long> selectedTypes = new ArrayList<>();
    private List<Long> selectedOrganizers = new ArrayList<>();
    private List<String> selectedLocations = new ArrayList<>();
    private Button sortButton;
    private final HashMap<String, String> sortOptions = new HashMap<String, String>() {{
        put("Date added ↑", "id,asc");
        put("Date added ↓", "id,desc");
        put("Event date ↑", "date,asc");
        put("Event date ↓", "date,desc");
        put("Event name ↑", "name,asc");
        put("Event name ↓", "name,desc");
    }};
    private int selectedSortIndex = 0;

    public interface OnFilterApplyListener {
        void onApplyFilters(EventFilters filters);
    }

    public void setOnFilterApplyListener(OnFilterApplyListener listener) {
        this.listener = listener;
    }

    public FilterSortEvents() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_sort_events, container, false);

        guestsRangeSlider = view.findViewById(R.id.guests_range_slider);
        guestsValue = view.findViewById(R.id.guests_value);
        guestsRangeSlider.setValues(0f, 50000f);

        ratingRangeSlider = view.findViewById(R.id.rating_range_slider);
        ratingValue = view.findViewById(R.id.rating_value);
        ratingRangeSlider.setValues(0f, 5f);

        resetButton = view.findViewById(R.id.reset_filters_button);
        dateFrom = view.findViewById(R.id.date_from);
        dateTo = view.findViewById(R.id.date_to);
        showInPast = view.findViewById(R.id.show_in_past);

        typesButton = view.findViewById(R.id.types_button);
        organizersButton = view.findViewById(R.id.organizers_button);
        locationsButton = view.findViewById(R.id.locations_button);

        loadTypesFromBackend();
        loadOrganizersFromBackend();
        loadLocationsFromBackend();

        // Range slider listeners
        guestsRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            float min = guestsRangeSlider.getValues().get(0);
            float max = guestsRangeSlider.getValues().get(1);
            guestsValue.setText((int) min + " - " + (int) max);
        });

        ratingRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            float min = ratingRangeSlider.getValues().get(0);
            float max = ratingRangeSlider.getValues().get(1);
            ratingValue.setText(String.format("%.1f - %.1f", min, max));
        });

        dateFrom.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(requireContext(), (_view, year, month, dayOfMonth) -> {
                dateFrom.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, y, m, d).show();
        });

        dateTo.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(requireContext(), (_view, year, month, dayOfMonth) -> {
                dateTo.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, y, m, d).show();
        });

        typesButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Types");

            builder.setMultiChoiceItems(typesNames, selectedTypesArray, (dialog, which, isChecked) -> {
                selectedTypesArray[which] = isChecked;
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                selectedTypes.clear();
                for (int i = 0; i < typesNames.length; i++) {
                    if (selectedTypesArray[i]) {
                        String name = typesNames[i];
                        typesMap.entrySet().stream()
                                .filter(e -> e.getValue().equals(name))
                                .map(Map.Entry::getKey)
                                .findFirst().ifPresent(typeId -> selectedTypes.add(typeId));
                    }
                }
                List<String> chosenNames = new ArrayList<>();
                for (Long id : selectedTypes) {
                    chosenNames.add(typesMap.get(id));
                }
                typesButton.setText(chosenNames.isEmpty() ? "Select Types" : TextUtils.join(", ", chosenNames));
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        sortButton = view.findViewById(R.id.sort_button);

        final String[] sortLabels = sortOptions.keySet().toArray(new String[0]);

        sortButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Sort by");

            builder.setSingleChoiceItems(sortLabels, selectedSortIndex, (dialog, which) -> selectedSortIndex = which);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String selectedLabel = sortLabels[selectedSortIndex];
                sortButton.setText(selectedLabel);
                String sortValue = sortOptions.get(selectedLabel);
                if (listener != null) {
                    EventFilters data = new EventFilters();
                    data.setSortOption(sortValue);
                    listener.onApplyFilters(data);
                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });


        Button applyButton = view.findViewById(R.id.filter_button);
        applyButton.setOnClickListener(v -> {
            if (listener != null) {
                EventFilters data = new EventFilters();
                int minMaxGuests = guestsRangeSlider.getValues().get(0).intValue();
                data.setMinMaxGuests(minMaxGuests!=0?minMaxGuests:null);
                int maxMaxGuests = guestsRangeSlider.getValues().get(1).intValue();
                data.setMaxMaxGuests(maxMaxGuests!=50000?maxMaxGuests:null);
                double minRating = ratingRangeSlider.getValues().get(0).doubleValue();
                data.setMinRating(minRating!=0?minRating:null);
                double maxRating = ratingRangeSlider.getValues().get(1).doubleValue();
                data.setMaxRating(maxRating!=5?maxRating:null);
                String dateFromStr = dateFrom.getText().toString();
                data.setDateFrom(!dateFromStr.equals(getString(R.string.from_date))?dateFromStr:null);
                String dateToStr = dateTo.getText().toString();
                data.setDateTo(!dateToStr.equals(getString(R.string.to_date))?dateToStr:null);
                data.setShowInPast(showInPast.isChecked());
                data.setSelectedTypes(new ArrayList<>(selectedTypes));
                data.setSelectedOrganizers(new ArrayList<>(selectedOrganizers));
                data.setSelectedLocations(new ArrayList<>(selectedLocations));

                listener.onApplyFilters(data);
            }

        });

        // Reset button
        resetButton.setOnClickListener(v -> resetFilters());

        return view;
    }

    private void resetFilters() {
        guestsRangeSlider.setValues(0f, 50000f);
        guestsValue.setText("0 - 50000");

        ratingRangeSlider.setValues(0f, 5f);
        ratingValue.setText("0.0 - 5.0");

        showInPast.setChecked(false);
        dateFrom.setText(R.string.from_date);
        dateTo.setText(R.string.to_date);

        // Types
        if (selectedTypesArray != null) Arrays.fill(selectedTypesArray, false);
        selectedTypes.clear();
        typesButton.setText(R.string.select_types);

        // Organizers
        if (selectedOrganizersArray != null) Arrays.fill(selectedOrganizersArray, false);
        selectedOrganizers.clear();
        organizersButton.setText(R.string.select_organizers);

        // Locations
        if (selectedLocationsArray != null) Arrays.fill(selectedLocationsArray, false);
        selectedLocations.clear();
        locationsButton.setText(R.string.select_location);
    }

    private void loadTypesFromBackend() {
        ClientUtils.eventTypeService.getActiveTypes(true).enqueue(new retrofit2.Callback<PaginatedResponse<EventType>>() {
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
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<EventType>> call, Throwable throwable) {
                Toast.makeText(requireContext(), "Error fetching types: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrganizersFromBackend() {
        ClientUtils.eventOrganizerService.getAllOrganizers().enqueue(new retrofit2.Callback<List<EventOrganizer>>() {
            @Override
            public void onResponse(Call<List<EventOrganizer>> call, retrofit2.Response<List<EventOrganizer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventOrganizer> organizersList = response.body();

                    organizersNames = new String[organizersList.size()];
                    selectedOrganizersArray = new boolean[organizersList.size()];

                    for (int i = 0; i < organizersList.size(); i++) {
                        String fullName = organizersList.get(i).getUser().getFirstName() + " " +
                                organizersList.get(i).getUser().getLastName();
                        organizersNames[i] = fullName;
                        selectedOrganizersArray[i] = false;
                        organizersMap.put(organizersList.get(i).getUser().getId(), fullName);
                    }

                    organizersButton.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Select Organizers");

                        builder.setMultiChoiceItems(organizersNames, selectedOrganizersArray, (dialog, which, isChecked) -> {
                            selectedOrganizersArray[which] = isChecked;
                        });

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            selectedOrganizers.clear();
                            for (int i = 0; i < organizersNames.length; i++) {
                                String name = organizersNames[i];
                                if (selectedOrganizersArray[i]) {
                                    organizersMap.entrySet().stream()
                                            .filter(e -> e.getValue().equals(name))
                                            .map(Map.Entry::getKey)
                                            .findFirst().ifPresent(orgId -> selectedOrganizers.add(orgId));
                                }
                            }
                            List<String> chosenNames = new ArrayList<>();
                            for (Long id : selectedOrganizers) {
                                chosenNames.add(organizersMap.get(id));
                            }
                            organizersButton.setText(chosenNames.isEmpty() ? "Select Organizers" : TextUtils.join(", ", chosenNames));
                        });

                        builder.setNegativeButton("Cancel", null);
                        builder.show();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<EventOrganizer>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error fetching organizers: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
}
