package com.example.evenmate.fragments.event_type;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.evenmate.adapters.EventTypeAdapter;
import com.example.evenmate.databinding.FragmentEventTypesBinding;

import java.util.ArrayList;


public class EventTypesFragment extends Fragment {
    private FragmentEventTypesBinding binding;
    private EventTypesViewModel viewModel;
    private EventTypeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventTypesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EventTypesViewModel.class);

        setupRecyclerView();

        setupPagination();

        binding.btnAddEventType.setOnClickListener(v -> {
            // TODO: Implement add event type functionality
            Toast.makeText(getContext(), "Add Event Type", Toast.LENGTH_SHORT).show();
        });

        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new EventTypeAdapter(
                new ArrayList<>(),
                eventType -> {
                    // TODO: Implement edit event type logic
                    Toast.makeText(getContext(), "Edit " + eventType.getName(), Toast.LENGTH_SHORT).show();
                },
                eventType -> viewModel.toggleEventTypeStatus(eventType.getId())
        );

        binding.recyclerViewEventTypes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewEventTypes.setAdapter(adapter);
    }

    private void setupPagination() {
        binding.btnPrevious.setOnClickListener(v -> viewModel.previousPage());
        binding.btnNext.setOnClickListener(v -> viewModel.nextPage());
    }

    private void observeViewModel() {
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes ->
                adapter.setEventTypes(eventTypes)
        );

        viewModel.getCurrentPage().observe(getViewLifecycleOwner(), page -> {
            Integer totalPages = viewModel.getTotalPages().getValue();
            if (totalPages != null) {
                binding.tvPageInfo.setText(String.format("Page %d of %d", page, totalPages));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}