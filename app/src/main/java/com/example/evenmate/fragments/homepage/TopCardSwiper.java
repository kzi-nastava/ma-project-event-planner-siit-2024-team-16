package com.example.evenmate.fragments.homepage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.evenmate.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopCardSwiper extends Fragment {

    private final List<Map<String, String>> data;
    private int savedPosition;
    public TopCardSwiper(List<Map<String, String>> _data) {
        data = _data;
    }
    public TopCardSwiper() {
        data=new ArrayList<>();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d("TopCardSwiper", "Here should be saving position: " + savedPosition);
        super.onSaveInstanceState(outState);

        // Save data
        ArrayList<Bundle> dataBundles = new ArrayList<>();
        for (Map<String, String> item : data) {
            Bundle bundle = new Bundle();
            for (Map.Entry<String, String> entry : item.entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
            }
            dataBundles.add(bundle);
        }
        outState.putParcelableArrayList("data", dataBundles);

        // Save progress position
        outState.putInt("progressBar", savedPosition);
        Log.d("TopCardSwiper", "Saving position: " + savedPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            List<Bundle> dataBundles = savedInstanceState.getParcelableArrayList("data");
            if (dataBundles != null) {
                data.clear();
                for (Bundle bundle : dataBundles) {
                    Map<String, String> item = new HashMap<>();
                    for (String key : bundle.keySet()) {
                        item.put(key, bundle.getString(key));
                    }
                    data.add(item);
                }
            }

            savedPosition = savedInstanceState.getInt("progressBar", 0); // Default to 0 if not found
            Log.d("TopCardSwiper", "Restored position: " + savedPosition);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_top_cards_swiper, container, false);
        ViewPager2 viewPager = view.findViewById(R.id.cardSwiper);
        LinearProgressIndicator progressIndicator = view.findViewById(R.id.progressIndicatorOfCurrentSwiper);

        TopCardAdapter cardAdapter = new TopCardAdapter(data);
        viewPager.setAdapter(cardAdapter);

        if (savedInstanceState != null) {
            savedPosition = savedInstanceState.getInt("progressBar", 0);
            Log.d("TopCardSwiper", "Restored position from savedInstanceState: " + savedPosition);
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                savedPosition = position;
                Log.d("TopCardSwiper", "Page selected: " + savedPosition +", and position: "+position);
                float progress = ((float) savedPosition + 1) / (float) getItemCount();
                progressIndicator.setProgress((int) (progress * 100));  // progress 0 to 100
            }
        });

        // Set the progress indicator style
        if (!data.isEmpty() && data.get(0).containsKey("date")) {
            progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.green));
            progressIndicator.setTrackColor(ContextCompat.getColor(requireContext(), R.color.light_green));
        }
        else {
            progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.purple));
            progressIndicator.setTrackColor(ContextCompat.getColor(requireContext(), R.color.light_purple));
        }

        return view;
    }

    private int getItemCount() {
        return data != null ? data.size() : 0;
    }

}
