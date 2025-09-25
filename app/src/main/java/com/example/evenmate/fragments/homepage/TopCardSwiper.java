package com.example.evenmate.fragments.homepage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.event.Event;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class TopCardSwiper extends Fragment {

    private final CollectionType type;
    private List<Asset> assets = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private TopCardAdapter cardAdapter;

    public TopCardSwiper(CollectionType type) {
        this.type = type;
        if (type.equals(CollectionType.Asset)){
            this.fetchAssets();
            this.events = null;
        }
        else {
            fetchEvents();
            this.assets = null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_cards_swiper, container, false);
        ViewPager2 viewPager = view.findViewById(R.id.cardSwiper);
        LinearProgressIndicator progressIndicator = view.findViewById(R.id.progressIndicatorOfCurrentSwiper);

        cardAdapter = new TopCardAdapter(events, assets);
        viewPager.setAdapter(cardAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                float progress = ((float) position + 1) / (float) cardAdapter.getItemCount();
                progressIndicator.setProgress((int) (progress * 100));
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (type == CollectionType.Asset) {
            fetchAssets();
        } else {
            fetchEvents();
        }
    }

    public void setEvents(List<Event> newEvents) {
        this.events = newEvents;
        cardAdapter.notifyDataSetChanged();
    }
    public void setAssets(List<Asset> newAssets) {
        this.assets = newAssets;
        cardAdapter.notifyDataSetChanged();
    }

    private void fetchAssets() {
        ClientUtils.assetService.getTop5().enqueue(new Callback<List<Asset>>() {
            @Override
            public void onResponse(Call<List<Asset>> call, Response<List<Asset>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    assets.clear();
                    assets.addAll(response.body());
                    setAssets(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Asset>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error fetching assets: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchEvents() {
        ClientUtils.eventService.getTop5Events().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    events.clear();
                    events.addAll(response.body());
                   setEvents(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error fetching events: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
