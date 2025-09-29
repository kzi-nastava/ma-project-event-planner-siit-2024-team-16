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
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.fragments.filters.AssetFilters;
import com.example.evenmate.fragments.filters.EventFilters;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.event.Event;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardCollection extends Fragment {

    private CollectionType type;
    private LinearLayout cardCollectionHolder;
    @Nullable
    private List<Event> events;
    @Nullable
    private List<Asset> assets;
    private Button loadMoreButton;
    private Button loadLessButton;
    public EventFilters eventFilters;
    public AssetFilters assetFilters;
    public int currentPage = 0;
    private final int pageSize = 5;

    public CardCollection() {}

    public CardCollection(CollectionType type) {
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_collection, container, false);
        cardCollectionHolder = rootView.findViewById(R.id.card_collection_holder);
        loadMoreButton = rootView.findViewById(R.id.load_more_button);
        loadLessButton = rootView.findViewById(R.id.load_less_button);

        if (type == CollectionType.Asset) {
            this.events = null;
            loadLessButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.purple));
            loadMoreButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.purple));

            loadMoreButton.setOnClickListener(v -> {
                currentPage++;
                loadNewAssets();
            });
            loadLessButton.setOnClickListener(v -> {
                if (currentPage > 0) currentPage--;
                loadNewAssets();
            });

        } else {
            this.assets = null;
            loadLessButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green));
            loadMoreButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green));

            loadMoreButton.setOnClickListener(v -> {
                currentPage++;
                loadNewEvents();
            });
            loadLessButton.setOnClickListener(v -> {
                if (currentPage > 0) currentPage--;
                loadNewEvents();
            });
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (type == CollectionType.Asset) {
            loadNewAssets();
        } else {
            loadNewEvents();
        }
    }

    private void loadCards() {
        if (!isAdded() || getContext() == null) {
            return;
        }
        cardCollectionHolder.removeAllViews();
        if (events != null) {
            for (Event item : events) {
                View cardView = getLayoutInflater().inflate(R.layout.item_card_general, cardCollectionHolder, false);
                new CardAdapter(cardView, this, item);
                cardCollectionHolder.addView(cardView);
            }
        } else if (assets != null) {
            for (Asset item : assets) {
                View cardView = getLayoutInflater().inflate(R.layout.item_card_general, cardCollectionHolder, false);
                new CardAdapter(cardView, this, item);
                cardCollectionHolder.addView(cardView);
            }
        }
    }

    void loadNewEvents() {
        getEvents(eventsList -> {
            events = eventsList;
            loadCards();
            loadLessButton.setEnabled(currentPage > 0);
            loadMoreButton.setEnabled(eventsList.size() == pageSize);
        }, eventFilters);
    }

    public void getEvents(Consumer<List<Event>> callback, @Nullable EventFilters filters) {
        Call<PaginatedResponse<Event>> call = ClientUtils.eventService.getAllEvents(
                filters != null ? filters.getMinMaxGuests() : null,
                filters != null ? filters.getMaxMaxGuests() : null,
                filters != null ? filters.getDateFrom() : null,
                filters != null ? filters.getDateTo() : null,
                filters != null ? filters.getSelectedTypes() : null,
                filters != null ? filters.getSelectedOrganizers() : null,
                filters != null ? filters.getSelectedLocations() : null,
                filters != null ? filters.getMinRating() : null,
                filters != null ? filters.getMaxRating() : null,
                filters != null && filters.isShowInPast(),
                currentPage, pageSize, filters != null ? filters.getSortOption() : "id,asc"
        );

        call.enqueue(new Callback<PaginatedResponse<Event>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Event>> call,
                                   Response<PaginatedResponse<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaginatedResponse<Event> body = response.body();
                    callback.accept(body.getContent());
                    loadMoreButton.setEnabled(currentPage < body.getTotalPages() - 1);
                    loadLessButton.setEnabled(currentPage > 0);
                } else {
                    callback.accept(List.of());
                    loadMoreButton.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<Event>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load events: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                callback.accept(List.of());
                loadMoreButton.setEnabled(false);
            }
        });
    }
    public void searchEvents(String keywords) {
        if (events != null) {
            Call<PaginatedResponse<Event>> call = ClientUtils.eventService.searchEvents(keywords, currentPage, pageSize);
            call.enqueue(new Callback<PaginatedResponse<Event>>() {
                @Override
                public void onResponse(Call<PaginatedResponse<Event>> call, Response<PaginatedResponse<Event>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        events = response.body().getContent();
                        loadCards();
                        Toast.makeText(requireContext(), "Found " + events.size() + " events", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "No events found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PaginatedResponse<Event>> call, Throwable t) {
                    Toast.makeText(requireContext(), "Error searching events: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void loadNewAssets() {
        getAssets(assetsList -> {
            assets = assetsList;
            loadCards();
            loadLessButton.setEnabled(currentPage > 0);
            loadMoreButton.setEnabled(assetsList.size() == pageSize);

        }, assetFilters);
    }
    public void getAssets(Consumer<List<Asset>> callback, @Nullable AssetFilters filters) {
        String type = null;
        if (filters != null && filters.isShowServicesOnly()&&!filters.isShowProductsOnly()) {type = "SERVICE";}
        if (filters != null && !filters.isShowServicesOnly()&&filters.isShowProductsOnly()) {type = "PRODUCT";}
        Call<PaginatedResponse<Asset>> call = ClientUtils.assetService.getAll(
                filters != null ? filters.getSelectedLocations() : null,
                filters != null ? filters.getSelectedCategories() : null,
                filters != null ? filters.getSelectedTypes() : null,
                filters != null ? filters.getSelectedProviders() : null,
                filters != null ? filters.getDateFrom() : null,
                filters != null ? filters.getDateTo() : null,
                filters != null ? filters.getMinPrice() : null,
                filters != null ? filters.getMaxPrice() : null,
                filters != null ? filters.getMinRating() : null,
                filters != null ? filters.getMaxRating() : null,
                type,
                currentPage,
                pageSize,
                filters != null ? filters.getSortOption() : null
        );

        call.enqueue(new Callback<PaginatedResponse<Asset>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Asset>> call, Response<PaginatedResponse<Asset>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaginatedResponse<Asset> body = response.body();
                    callback.accept(body.getContent());
                    loadMoreButton.setEnabled(currentPage < body.getTotalPages() - 1);
                    loadLessButton.setEnabled(currentPage > 0);
                } else {
                    callback.accept(List.of());
                    loadMoreButton.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<Asset>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load assets: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                callback.accept(List.of());
                loadMoreButton.setEnabled(false);
            }
        });
    }
    public void searchAssets(String keywords) {
        if (assets != null) {
            Call<PaginatedResponse<Asset>> call = ClientUtils.assetService.search(keywords, currentPage, pageSize);
            call.enqueue(new Callback<PaginatedResponse<Asset>>() {
                @Override
                public void onResponse(Call<PaginatedResponse<Asset>> call, Response<PaginatedResponse<Asset>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        assets = response.body().getContent();
                        loadCards();
                        Toast.makeText(requireContext(), "Found " + assets.size() + " assets", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "No assets found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PaginatedResponse<Asset>> call, Throwable t) {
                    Toast.makeText(requireContext(), "Error searching assets: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
