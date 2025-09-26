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
import com.example.evenmate.fragments.filters.FilterSortAssets;
import com.example.evenmate.fragments.filters.FilterSortEvents;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class HomepageFragment extends Fragment {

    private Fragment top5Events;
    private Fragment allEvents;
    private Fragment top5ServicesAndProducts;
    private Fragment allServicesAndProducts;
    private SwitchMaterial fragmentSwitch;
    private SearchView searchView;

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
            top5Events = new TopCardSwiper(CollectionType.Event);
            manager.beginTransaction().replace(R.id.top_5, top5Events, "top5Events").commit();
            allEvents = new CardCollection(CollectionType.Event);
            manager.beginTransaction().replace(R.id.all, allEvents,"allEvents").commit();
            top5ServicesAndProducts = new TopCardSwiper(CollectionType.Asset);
            allServicesAndProducts = new CardCollection(CollectionType.Asset);
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
                if (!fragmentSwitch.isChecked() && allEvents instanceof CardCollection) {
                    ((CardCollection) allEvents).searchEvents(query);
                } else if (fragmentSwitch.isChecked() && allServicesAndProducts instanceof CardCollection) {
                    ((CardCollection) allServicesAndProducts).searchAssets(query);
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
            Fragment filterFragment;

            if (!fragmentSwitch.isChecked()) {
                filterFragment = getChildFragmentManager().findFragmentById(R.id.filter_container);
                if (!(filterFragment instanceof FilterSortEvents)) {
                    filterFragment = new FilterSortEvents();
                    ((FilterSortEvents) filterFragment).setOnFilterApplyListener(filters -> {
                        if (allEvents instanceof CardCollection) {
                            CardCollection cardCollection = (CardCollection) allEvents;
                            cardCollection.eventFilters = filters;
                            cardCollection.currentPage = 0;
                            cardCollection.loadNewEvents();
                        }
                    });
                }
            } else {
                filterFragment = getChildFragmentManager().findFragmentById(R.id.filter_container);
                if (!(filterFragment instanceof FilterSortAssets)) {
                    filterFragment = new FilterSortAssets();
                    ((FilterSortAssets) filterFragment).setOnFilterApplyListener(filters -> {
                        if (allServicesAndProducts instanceof CardCollection) {
                            CardCollection cardCollection = (CardCollection) allServicesAndProducts;
                            cardCollection.assetFilters = filters;
                            cardCollection.currentPage = 0;
                            cardCollection.loadNewAssets();
                        }
                    });
                }
            }

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.filter_container, filterFragment)
                    .setReorderingAllowed(true)
                    .commit();

            filterContainer.setVisibility(View.VISIBLE);
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
}
