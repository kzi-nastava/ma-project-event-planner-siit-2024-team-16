package com.example.evenmate.fragments.event_type;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.evenmate.adapters.EventTypeAdapter;
import com.example.evenmate.databinding.FragmentEventTypesBinding;

import java.util.ArrayList;


public class EventTypesFragment extends ListFragment {
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

        adapter = new EventTypeAdapter(getActivity(), new ArrayList<>());
        adapter.setOnStatusClickListener(eventType ->
                viewModel.updateEventTypeStatus(eventType)
        );
        adapter.setOnEditClickListener(eventType -> {
                EventTypeFormFragment dialogFragment = new EventTypeFormFragment(eventType);
                dialogFragment.show(getParentFragmentManager(), "EditEventType");
            }
        );
        setListAdapter(adapter);
        setupPagination();

        setupAddEventTypeButton();

        observeViewModel();
    }

    private void setupPagination() {
        binding.btnPrevious.setOnClickListener(v -> viewModel.previousPage());
        binding.btnNext.setOnClickListener(v -> viewModel.nextPage());
        viewModel.getCurrentPage().observe(getViewLifecycleOwner(), currentPage ->
                updatePaginationUI(currentPage, viewModel.getTotalPages().getValue())
        );
        viewModel.getTotalPages().observe(getViewLifecycleOwner(), totalPages ->
                updatePaginationUI(viewModel.getCurrentPage().getValue(), totalPages)
        );
    }

    private void updatePaginationUI(Integer currentPage, Integer totalPages) {
        if (currentPage != null && totalPages != null) {
            binding.tvPageInfo.setText(String.format("Page %d of %d", currentPage, totalPages));
            binding.btnPrevious.setEnabled(currentPage > 1);
            binding.btnNext.setEnabled(currentPage < totalPages);
            binding.paginationLayout.setVisibility(totalPages > 1 ? View.VISIBLE : View.GONE);
        }
    }

    private void setupAddEventTypeButton() {
        binding.btnAddEventType.setOnClickListener(v -> {
            EventTypeFormFragment dialogFragment = new EventTypeFormFragment();
            dialogFragment.show(getParentFragmentManager(), "CreateEventType");
        });
    }

    private void observeViewModel() {
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            Log.d("EventTypes", "Received event types: " + eventTypes.size());
            assert getActivity() != null;
            adapter.setEventTypes(new ArrayList<>(eventTypes));            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
            binding.list.setVisibility(eventTypes.isEmpty() ? View.GONE : View.VISIBLE);
            binding.textViewNoEventTypes.setVisibility(eventTypes.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getCurrentPage().observe(getViewLifecycleOwner(), page -> {
            Integer totalPages = viewModel.getTotalPages().getValue();
            if (totalPages != null) {
                binding.tvPageInfo.setText(String.format("Page %d of %d", page, totalPages));
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.fetchEventTypes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}