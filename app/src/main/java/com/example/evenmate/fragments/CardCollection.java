package com.example.evenmate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.evenmate.R;
import com.example.evenmate.activities.HomepageActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class CardCollection extends Fragment {

    private LinearLayout cardCollectionHolder;
    private List<Map<String, String>> data = new ArrayList<>();
    private Button loadMoreButton; // not local because int the future it will have more functionality
    private Button loadLessButton; // not local because int the future it will have more functionality

    public CardCollection(List<Map<String, String>> initialData) {
        this.data.addAll(initialData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_collection, container, false);
        cardCollectionHolder = rootView.findViewById(R.id.card_collection_holder);
        loadMoreButton = rootView.findViewById(R.id.load_more_button);
        loadLessButton = rootView.findViewById(R.id.load_less_button);

        loadCards();

        if (data.get(0).containsKey("date")) {
            loadMoreButton.setOnClickListener(v -> loadNewEvents());
            loadLessButton.setOnClickListener(v -> loadNewEvents());
            loadLessButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green));
            loadMoreButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green));
        } else {
            loadMoreButton.setOnClickListener(v -> loadNewAssets());
            loadLessButton.setOnClickListener(v -> loadNewAssets());
            loadLessButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.purple));
            loadMoreButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.purple));
        }

        return rootView;
    }

    private void loadCards() {
        cardCollectionHolder.removeAllViews();
        for (Map<String, String> item : data) {
            View cardView = getLayoutInflater().inflate(R.layout.card_item, cardCollectionHolder, false);
            new CardAdapter(cardView, this, item);
            cardCollectionHolder.addView(cardView);
        }
    }

    private void loadNewEvents() {
        data= HomepageActivity.getTop5Events();
        loadCards();
        Toast.makeText(this.getContext(),R.string.new_page,Toast.LENGTH_SHORT).show();
    }
    private void loadNewAssets() {
        data= HomepageActivity.getTop5ServicesAndProducts();
        loadCards();
        Toast.makeText(this.getContext(),R.string.new_page,Toast.LENGTH_SHORT).show();

    }


}
