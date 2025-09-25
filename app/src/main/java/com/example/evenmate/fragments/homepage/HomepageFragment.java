package com.example.evenmate.fragments.homepage;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.fragments.filters.EventFilters;
import com.example.evenmate.fragments.filters.FilterSortEvents;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.asset.AssetType;
import com.example.evenmate.models.event.Event;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class HomepageFragment extends Fragment {

    private Fragment top5Events;
    private Fragment allEvents;
    private Fragment top5ServicesAndProducts;
    private Fragment allServicesAndProducts;
    private SwitchMaterial fragmentSwitch;
    private SearchView searchView;
    private EventFilters currentFilters = new EventFilters();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentSwitch = view.findViewById(R.id.fragment_switch);

        FragmentManager manager = getChildFragmentManager();

        if (savedInstanceState == null) {
            getTop5Events(top5-> {
                top5Events = new TopCardSwiper(null, top5);
                manager.beginTransaction().replace(R.id.top_5, top5Events, "top5Events").commit();
            });
            getTop5Events(top5-> {
                allEvents = new CardCollection(null, top5);
                manager.beginTransaction().replace(R.id.all, allEvents,"allEvents").commit();
            });
            top5ServicesAndProducts = new TopCardSwiper(getTop5ServicesAndProducts(),null);
            allServicesAndProducts = new CardCollection(getTop5ServicesAndProducts(),null);
        } else {
            top5Events = manager.findFragmentByTag("top5Events");
            allEvents = manager.findFragmentByTag( "allEvents");
            top5ServicesAndProducts = manager.findFragmentByTag("top5ServicesAndProducts");
            allServicesAndProducts = manager.findFragmentByTag("allServicesAndProducts");
        }

        fragmentSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> switchedFragments(isChecked));
        updateSwitchColors(false);

        this.searchView = view.findViewById(R.id.search_bar);
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!fragmentSwitch.isChecked()) {
                    Toast.makeText(requireContext(), "You event searched for: " + query, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "You s/p searched for: " + query, Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        FrameLayout filterContainer = view.findViewById(R.id.filter_container);
        Button filterButton = view.findViewById(R.id.filter);

        filterButton.setOnClickListener(v -> {
            FilterSortEvents filterFragment = (FilterSortEvents) getChildFragmentManager().findFragmentById(R.id.filter_container);
            if (filterFragment == null) {
                filterFragment = new FilterSortEvents();
                filterFragment.setOnFilterApplyListener(filters -> {
                    if (allEvents instanceof CardCollection) {
                        CardCollection cardCollection = (CardCollection) allEvents;
                        cardCollection.eventFilters = filters;
                        cardCollection.currentPage = 0;
                        cardCollection.loadNewEvents();
                    }
                });
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.filter_container, filterFragment)
                        .setReorderingAllowed(true)
                        .commit();
            }
            filterContainer.setVisibility(View.VISIBLE);
        });

        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!fragmentSwitch.isChecked() && allEvents instanceof CardCollection) {
                    ((CardCollection) allEvents).searchEvents(query);
                } else if (fragmentSwitch.isChecked() && allServicesAndProducts instanceof CardCollection) {
//                    ((CardCollection) allServicesAndProducts).searchAssets(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void switchedFragments(boolean isChecked) {
        FragmentManager manager=getChildFragmentManager();
        if (isChecked) {
            manager.beginTransaction().replace(R.id.top_5, top5ServicesAndProducts,"top5ServicesAndProducts").commit();
            manager.beginTransaction().replace(R.id.all, allServicesAndProducts,"allServicesAndProducts").commit();
            Toast.makeText(requireContext(), R.string.services_and_products, Toast.LENGTH_SHORT).show();
            searchView.setQuery("", false);
        } else {
            manager.beginTransaction().replace(R.id.top_5, top5Events,"top5Events").commit();
            manager.beginTransaction().replace(R.id.all, allEvents,"allEvents").commit();
            Toast.makeText(requireContext(), R.string.events, Toast.LENGTH_SHORT).show();
            searchView.setQuery("", false);
        }
        updateSwitchColors(isChecked);
    }

    private void updateSwitchColors(boolean isChecked) {
        int trackColor = isChecked ? R.color.light_purple : R.color.light_green;
        int thumbColor = isChecked ? R.color.purple : R.color.green;
        int backgroundColor = isChecked ? R.color.purple : R.color.green;

        fragmentSwitch.setTrackTintList(ContextCompat.getColorStateList(requireContext(), trackColor));
        fragmentSwitch.setThumbTintList(ContextCompat.getColorStateList(requireContext(), thumbColor));
        FrameLayout frameLayout = requireView().findViewById(R.id.search_bar_frame);
        GradientDrawable background = (GradientDrawable) frameLayout.getBackground();
        background.setColor(ContextCompat.getColor(requireContext(), backgroundColor));
        requireView().findViewById(R.id.filter).setBackgroundColor(requireContext().getColor(backgroundColor));
    }

    public void getTop5Events(Consumer<List<Event>> callback) {
        ClientUtils.eventService.getTop5Events().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.accept(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error fetching events: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static List<Asset> getTop5ServicesAndProducts() {
        List<Asset> assets = new ArrayList<>();
        assets.add(new Asset(1L, "Maya's Catering", new ArrayList<>(List.of("@drawable/img_service")), "High-quality catering service", 500, "food", 0, "USA", "California", "", "", 4.3, AssetType.SERVICE,true));
        assets.add(new Asset(2L, "Lilly Bloom's Flower Arrangements", new ArrayList<>(List.of("@drawable/img_product")), "Beautiful flower arrangements", 350, "decoration", 0, "USA", "California", "", "", 4.3, AssetType.PRODUCT,false));
        assets.add(new Asset(3L, "Service 3", new ArrayList<>(List.of("@drawable/img_service")), "Description for service 3", 500, "food", 0, "USA", "California", "", "", 4.3, AssetType.SERVICE,false));
        assets.add(new Asset(4L, "Product 4", new ArrayList<>(List.of("@drawable/img_product")), "Description for product 4", 350, "decoration", 0, "USA", "California", "", "", 4.3, AssetType.PRODUCT,false));
        assets.add(new Asset(5L, "Service 5", new ArrayList<>(List.of("@drawable/img_service")), "Description for service 5", 500, "food", 0, "USA", "California", "", "", 4.3, AssetType.SERVICE,false));
        return assets;
    }
}
