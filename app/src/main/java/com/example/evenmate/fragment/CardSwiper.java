package com.example.evenmate.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.evenmate.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

public class CardSwiper extends Fragment {

    private List<Map<String, String>> data;

    public CardSwiper(List<Map<String, String>> _data) {
        data = _data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_cards_swiper, container, false);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        LinearProgressIndicator progressIndicator = view.findViewById(R.id.progressIndicatorOfCurrentSwiper);
        CardAdapter cardAdapter = new CardAdapter(data);
        viewPager.setAdapter(cardAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                float progress = ((float) position + 1) / (float) getItemCount();
                progressIndicator.setProgress((int) (progress * 100));  //progress 0 to 100
            }
        });
        if (data.get(0).containsKey("date")){
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.green));
            progressIndicator.setTrackColor(getResources().getColor(R.color.light_green));
        }
        else{
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.purple));
            progressIndicator.setTrackColor(getResources().getColor(R.color.light_purple));
        }


        return view;
    }

    private int getItemCount() {
        return data != null ? data.size() : 0;
    }

}
