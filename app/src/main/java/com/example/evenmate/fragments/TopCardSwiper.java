package com.example.evenmate.fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.evenmate.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

public class TopCardSwiper extends Fragment {

    private final List<Map<String, String>> data;

    public TopCardSwiper(List<Map<String, String>> _data) {
        data = _data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_cards_swiper, container, false);
        ViewPager2 viewPager = view.findViewById(R.id.cardSwiper);
        LinearProgressIndicator progressIndicator = view.findViewById(R.id.progressIndicatorOfCurrentSwiper);
        TopCardAdapter cardAdapter = new TopCardAdapter(data);
        viewPager.setAdapter(cardAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                float progress = ((float) position + 1) / (float) getItemCount();
                progressIndicator.setProgress((int) (progress * 100));  //progress 0 to 100
            }
        });
        if (data.get(0).containsKey("date")) {
            progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.green));
            progressIndicator.setTrackColor(ContextCompat.getColor(requireContext(), R.color.light_green));
        } else {
            progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.purple));
            progressIndicator.setTrackColor(ContextCompat.getColor(requireContext(), R.color.light_purple));
        }



        return view;
    }

    private int getItemCount() {
        return data != null ? data.size() : 0;
    }

}
