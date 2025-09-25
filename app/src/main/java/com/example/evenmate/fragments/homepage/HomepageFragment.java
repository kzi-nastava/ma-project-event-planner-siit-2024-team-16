package com.example.evenmate.fragments.homepage;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.evenmate.R;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.event.Event;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

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
            top5Events = new TopCardSwiper(null,getTop5Events());
            allEvents = new CardCollection(null,getTop5Events());
            top5ServicesAndProducts = new TopCardSwiper(getTop5ServicesAndProducts(),null);
            allServicesAndProducts = new CardCollection(getTop5ServicesAndProducts(),null);

            manager.beginTransaction().replace(R.id.top_5, top5Events, "top5Events").commit();
            manager.beginTransaction().replace(R.id.all, allEvents,"allEvents").commit();
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
        requireView().findViewById(R.id.sort).setBackgroundColor(requireContext().getColor(backgroundColor));
        requireView().findViewById(R.id.filter).setBackgroundColor(requireContext().getColor(backgroundColor));
    }

    public static List<Event> getTop5Events() {
        List<Event> events = new ArrayList<>();
        //after merging, errors appeared here, since this will be changed, I just put it as a comment
//        events.add(new Event(1L, "wedding", "Miguel and Athena's Wedding", "A beautiful wedding event", 150, false, LocalDateTime.of(2025, 12, 15, 0, 0), new ArrayList<>(), "USA", "California", "", "", "@drawable/img_event", 4.3, true));
//        events.add(new Event(2L, "category2", "Event 2", "Description for Event 2", 150, false, LocalDateTime.of(2025, 12, 15, 0, 0), new ArrayList<>(), "USA", "Loc2", "", "", "@drawable/img_event", 4.3, true));
//        events.add(new Event(3L, "category3", "Event 3", "Description for Event 3", 150, false, LocalDateTime.of(2025, 12, 15, 0, 0), new ArrayList<>(), "USA", "Loc3", "", "", "@drawable/img_event", 4.3, false));
//        events.add(new Event(4L, "category4", "Event 4", "Description for Event 4", 150, false, LocalDateTime.of(2025, 12, 15, 0, 0), new ArrayList<>(), "USA", "Loc4", "", "", "@drawable/img_event", 4.3, false));
//        events.add(new Event(5L, "category5", "Event 5", "Description for Event 5", 150, false, LocalDateTime.of(2025, 12, 15, 0, 0), new ArrayList<>(), "USA", "Loc5", "", "", "@drawable/img_event", 4.3, false));
        return events;
    }

    public static List<Asset> getTop5ServicesAndProducts() {
        List<Asset> assets = new ArrayList<>();
//        assets.add(new Asset(1L, "Maya's Catering", new ArrayList<>(List.of("@drawable/img_service")), "High-quality catering service", 500, "food", 0, "USA", "California", "", "", 4.3, AssetType.SERVICE,true));
//        assets.add(new Asset(2L, "Lilly Bloom's Flower Arrangements", new ArrayList<>(List.of("@drawable/img_product")), "Beautiful flower arrangements", 350, "decoration", 0, "USA", "California", "", "", 4.3, AssetType.PRODUCT,false));
//        assets.add(new Asset(3L, "Service 3", new ArrayList<>(List.of("@drawable/img_service")), "Description for service 3", 500, "food", 0, "USA", "California", "", "", 4.3, AssetType.SERVICE,false));
//        assets.add(new Asset(4L, "Product 4", new ArrayList<>(List.of("@drawable/img_product")), "Description for product 4", 350, "decoration", 0, "USA", "California", "", "", 4.3, AssetType.PRODUCT,false));
//        assets.add(new Asset(5L, "Service 5", new ArrayList<>(List.of("@drawable/img_service")), "Description for service 5", 500, "food", 0, "USA", "California", "", "", 4.3, AssetType.SERVICE,false));
        return assets;
    }
}
