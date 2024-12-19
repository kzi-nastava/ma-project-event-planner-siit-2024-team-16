package com.example.evenmate.fragments.event_type;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.evenmate.adapters.EventTypeAdapter;
import com.example.evenmate.databinding.FragmentEventTypesBinding;


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

        setupPagination();

        setupAddEventTypeButton();

        observeViewModel();

        viewModel.fetchEventTypes();
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
            binding.tvPageInfo.setText(String.format("%d / %d", currentPage, totalPages));
            binding.btnPrevious.setEnabled(currentPage > 1);
            binding.btnNext.setEnabled(currentPage < totalPages);
        }
    }
    private void setupAddEventTypeButton() {
        binding.btnAddEventType.setOnClickListener(v -> {
            CreateEventTypeFragment dialogFragment = new CreateEventTypeFragment();
            dialogFragment.show(getParentFragmentManager(), "CreateEventType");
        });
    }
    private void observeViewModel() {

        viewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            assert getActivity() != null;
            adapter = new EventTypeAdapter(getActivity(), getActivity().getSupportFragmentManager(), eventTypes);
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
            binding.recyclerViewEventTypes.setVisibility(eventTypes.isEmpty() ? View.GONE : View.VISIBLE);
            binding.textViewNoEventTypes.setVisibility(eventTypes.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getCurrentPage().observe(getViewLifecycleOwner(), page -> {
            Integer totalPages = viewModel.getTotalPages().getValue();
            if (totalPages != null) {
                binding.tvPageInfo.setText(String.format("Page %d of %d", page, totalPages));
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