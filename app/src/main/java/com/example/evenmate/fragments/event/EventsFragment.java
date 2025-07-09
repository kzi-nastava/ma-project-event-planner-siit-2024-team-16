package com.example.evenmate.fragments.event;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.FragmentResultListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.evenmate.adapters.EventAdapter;
import com.example.evenmate.databinding.FragmentEventsBinding;

import java.util.ArrayList;

public class EventsFragment extends ListFragment {
    private FragmentEventsBinding binding;
    private EventsViewModel viewModel;
    private EventAdapter adapter;

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

        viewModel = new ViewModelProvider(this).get(EventsViewModel.class);

        adapter = new EventAdapter(getActivity(), new ArrayList<>());
        adapter.setOnFavoriteClickListener(event ->
                viewModel.updateFavoriteStatus(event)
        );
        adapter.setOnEditClickListener(event -> {
                    EventFormFragment dialogFragment = EventFormFragment.newInstance(event);
                    dialogFragment.show(getParentFragmentManager(), "EditEvent");
                }
        );
        adapter.setOnDeleteClickListener(event -> {
//                    Dele dialogFragment = new EventFormFragment(event);
//                    dialogFragment.show(getParentFragmentManager(), "EditEvent");
                }
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
        binding.btnAddEvent.setOnClickListener(v -> {
            EventFormFragment dialogFragment = EventFormFragment.newInstance(null);
            dialogFragment.show(getParentFragmentManager(), "CreateEvent");
        });
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
        getParentFragmentManager().setFragmentResultListener("event_form_result", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                boolean shouldRefresh = result.getBoolean("refresh_events", false);
                if (shouldRefresh) {
                    viewModel.fetchEvents();
                }
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