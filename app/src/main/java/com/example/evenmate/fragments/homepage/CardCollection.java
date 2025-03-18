package com.example.evenmate.fragments.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.evenmate.R;
import com.example.evenmate.activities.PageActivity;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CardCollection extends Fragment {

    private LinearLayout cardCollectionHolder;
    @Nullable
    private List<Event> events;
    @Nullable
    private List<Asset> assets;
    private Button loadMoreButton; // not local because int the future it will have more functionality
    private Button loadLessButton; // not local because int the future it will have more functionality

    public CardCollection() {}
    public CardCollection(@Nullable List<Asset> assets,@Nullable List<Event>events) {
        this.events=events;
        this.assets=assets;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_collection, container, false);
        cardCollectionHolder = rootView.findViewById(R.id.card_collection_holder);
        loadMoreButton = rootView.findViewById(R.id.load_more_button);
        loadLessButton = rootView.findViewById(R.id.load_less_button);

        loadCards();

        if (events!=null) {
            loadMoreButton.setOnClickListener(v -> loadNewEvents());
            loadLessButton.setOnClickListener(v -> loadNewEvents());
            loadLessButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green));
            loadMoreButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green));
        } else if (assets!=null){
            loadMoreButton.setOnClickListener(v -> loadNewAssets());
            loadLessButton.setOnClickListener(v -> loadNewAssets());
            loadLessButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.purple));
            loadMoreButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.purple));
        }

        return rootView;
    }

    private void loadCards() {
        cardCollectionHolder.removeAllViews();
        if (events!=null){
            for (Event item : events) {
                View cardView = getLayoutInflater().inflate(R.layout.item_card_general, cardCollectionHolder, false);
                new CardAdapter(cardView, this, item);
                cardCollectionHolder.addView(cardView);
            }
        }
        else if(assets!=null){
            for (Asset item: assets){
                View cardView = getLayoutInflater().inflate(R.layout.item_card_general,cardCollectionHolder,false);
                new CardAdapter(cardView,this,item);
                cardCollectionHolder.addView(cardView);
            }
        }
    }

    private void loadNewEvents() {
        events = HomepageFragment.getTop5Events();
        loadCards();
        Toast.makeText(this.getContext(),R.string.new_page,Toast.LENGTH_SHORT).show();
    }
    private void loadNewAssets() {
        assets = HomepageFragment.getTop5ServicesAndProducts();
        loadCards();
        Toast.makeText(this.getContext(),R.string.new_page,Toast.LENGTH_SHORT).show();

    }


}
