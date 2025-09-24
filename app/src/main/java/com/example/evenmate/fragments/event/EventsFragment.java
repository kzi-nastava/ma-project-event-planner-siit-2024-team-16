package com.example.evenmate.fragments.event;


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

import com.example.evenmate.R;
import com.example.evenmate.adapters.EventAdapter;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.databinding.FragmentEventsBinding;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class EventsFragment extends ListFragment {
    private FragmentEventsBinding binding;
    private EventsViewModel viewModel;
    private EventAdapter adapter;
    private String fetchMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchMode = requireArguments().getString("fetch_mode", "ALL_EVENTS");
        viewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        if ("FAVORITES".equals(fetchMode)) {
            viewModel.setFetchMode("FAVORITES");
            binding.eventsHeading.setText(R.string.favorite_events_up);
        } else if ("YOUR_EVENTS".equals(fetchMode)){
            binding.eventsHeading.setText(R.string.your_events_up);
            viewModel.setFetchMode("YOUR_EVENTS");
        } else {
            binding.eventsHeading.setText(R.string.events);
            viewModel.setFetchMode("ALL_EVENTS");

        }

        adapter = new EventAdapter(getActivity(), new ArrayList<>());

        adapter.setOnEditClickListener(event -> {
                    EventFormFragment dialogFragment = EventFormFragment.newInstance(event);
                    dialogFragment.show(getParentFragmentManager(), "EditEvent");
                }
        );
        adapter.setOnDeleteClickListener(event -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Event")
                .setMessage(String.format("Are you sure you want to delete %s? This action cannot be undone.", event.getName()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteEvent(event.getId());
                    if(viewModel.getDeleteFailed())
                        ToastUtils.showCustomToast(requireContext(),
                            viewModel.getDeleteFailed().toString(),
                            true);
                    else
                        ToastUtils.showCustomToast(requireContext(),
                                String.format("%s successfully deleted", event.getName()),
                                false);
                    viewModel.resetDeleteFailed();
                })
                .show()
        );

        setListAdapter(adapter);
        setupPagination();

        setupAddEventButton();
        setupFragmentResultListener();
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

    private void setupAddEventButton() {
        boolean isFavoritesMode = this.fetchMode.equals("FAVORITES");
        boolean isEventOrganizer = AuthManager.loggedInUser != null && AuthManager.loggedInUser.getRole().equals("EventOrganizer");

        if (isFavoritesMode || !isEventOrganizer) {
            binding.btnAddEvent.setVisibility(View.GONE);
        } else {
            binding.btnAddEvent.setVisibility(View.VISIBLE);
            binding.btnAddEvent.setOnClickListener(v -> {
                EventFormFragment dialogFragment = EventFormFragment.newInstance(null);
                dialogFragment.show(getParentFragmentManager(), "CreateEvent");
            });
        }
    }

    private void observeViewModel() {
        viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            Log.d("Events", "Received events: " + events.size());
            assert getActivity() != null;
            adapter.setEvents(new ArrayList<>(events));
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
            binding.list.setVisibility(events.isEmpty() ? View.GONE : View.VISIBLE);
            binding.textViewNoEvents.setVisibility(events.isEmpty() ? View.VISIBLE : View.GONE);
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

    private void setupFragmentResultListener() {
        getParentFragmentManager().setFragmentResultListener("event_form_result", this, (requestKey, result) -> {
            boolean shouldRefresh = result.getBoolean("refresh_events", false);
            if (shouldRefresh) {
                viewModel.fetchEvents();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.fetchEvents();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}