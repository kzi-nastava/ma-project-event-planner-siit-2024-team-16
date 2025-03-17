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
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomepageFragment extends Fragment {

    private Fragment top5Events;
    private Fragment allEvents;
    private Fragment top5ServicesAndProducts;
    private Fragment allServicesAndProducts;
    private SwitchMaterial fragmentSwitch;
    private SearchView searchView;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        top5ServicesAndProducts.onSaveInstanceState(outState);
        allServicesAndProducts.onSaveInstanceState(outState);
        top5Events.onSaveInstanceState(outState);
        allEvents.onSaveInstanceState(outState);
    }
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
            top5Events = new TopCardSwiper(getTop5Events());
            allEvents = new CardCollection(getTop5Events());
            top5ServicesAndProducts = new TopCardSwiper(getTop5ServicesAndProducts());
            allServicesAndProducts = new CardCollection(getTop5ServicesAndProducts());

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

    public static List<Map<String, String>> getTop5Events(){
        List<Map<String,String>> data=new ArrayList<>();
        data.add(Map.of("id","1", "title","miguel and athena's wedding", "date","15.12.2025.", "location", "california", "category","wedding", "max_guests","150", "rating","4.3", "image","@drawable/img_event", "isFavorite","true"));
        data.add(Map.of("id","2", "title","event2", "date","15.12.2025.", "location", "loc2", "category","cat2", "max_guests","150", "rating","4.3", "image","@drawable/img_event", "isFavorite","true"));
        data.add(Map.of("id","3", "title","event3", "date","15.12.2025.", "location", "loc3", "category","cat3", "max_guests","150", "rating","4.3", "image","@drawable/img_event", "isFavorite","false"));
        data.add(Map.of("id","4", "title","event4", "date","15.12.2025.", "location", "loc4", "category","cat4", "max_guests","150", "rating","4.3", "image","@drawable/img_event", "isFavorite","false"));
        data.add(Map.of("id","5", "title","event5", "date","15.12.2025.", "location", "loc5", "category","cat5", "max_guests","150", "rating","4.3", "image","@drawable/img_event", "isFavorite","false"));
        return data;
    }
    public static List<Map<String, String>> getTop5ServicesAndProducts(){
        List<Map<String,String>> data=new ArrayList<>();
        data.add(Map.of("id","1", "title","Maya's catering", "location", "california", "category","food", "price","500", "rating","4.3", "image","@drawable/img_service", "isFavorite","false"));
        data.add(Map.of("id","2", "title","Lilly Bloom's flower arrangements", "location", "california", "category","decoration", "price","350", "rating","4.3", "image","@drawable/img_product", "isFavorite","true"));
        data.add(Map.of("id","3", "title","service 3", "location", "california", "category","food", "price","500", "rating","4.3", "image","@drawable/img_service", "isFavorite","false"));
        data.add(Map.of("id","4", "title","product 4", "location", "california", "category","decoration", "price","350", "rating","4.3", "image","@drawable/img_product", "isFavorite","false"));
        data.add(Map.of("id","5", "title","service 5", "location", "california", "category","food", "price","500", "rating","4.3", "image","@drawable/img_service", "isFavorite","false"));
        return data;
    }
}
