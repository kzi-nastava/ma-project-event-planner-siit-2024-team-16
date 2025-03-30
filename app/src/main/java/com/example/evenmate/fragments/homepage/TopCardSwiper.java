package com.example.evenmate.fragments.homepage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.evenmate.R;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.event.Event;
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

    @Nullable
    private final List<Asset> assets;
    @Nullable
    private final List<Event> events;
    private int savedPosition;
    public TopCardSwiper(@Nullable List<Asset> assets,@Nullable List<Event> events) {
        this.assets=assets;
        this.events=events;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_top_cards_swiper, container, false);
        ViewPager2 viewPager = view.findViewById(R.id.cardSwiper);
        LinearProgressIndicator progressIndicator = view.findViewById(R.id.progressIndicatorOfCurrentSwiper);

        TopCardAdapter cardAdapter=new TopCardAdapter(events,assets);
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
        if (events!=null) {
            progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.green));
            progressIndicator.setTrackColor(ContextCompat.getColor(requireContext(), R.color.light_green));
        }
        else if (assets!=null){
            progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.purple));
            progressIndicator.setTrackColor(ContextCompat.getColor(requireContext(), R.color.light_purple));
        }

        return view;
    }

    private int getItemCount() {
        return assets != null ? assets.size() : (events!=null?events.size():0);
    }

}
