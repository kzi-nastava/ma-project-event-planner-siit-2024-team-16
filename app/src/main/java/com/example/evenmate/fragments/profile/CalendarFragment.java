package com.example.evenmate.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.evenmate.adapters.CalendarAdapter;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.databinding.FragmentCalendarBinding;
import com.example.evenmate.models.user.CalendarDay;
import com.example.evenmate.models.user.CalendarItem;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.CalendarUtils;
import com.example.evenmate.utils.ToastUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private CalendarViewModel calendarViewModel;
    private CalendarAdapter calendarAdapter;
    private LocalDateTime currentMonth = LocalDateTime.now();
    private String specialLabel = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        setupCalendar();
        setupLegend();
        setupObservers();
        setupClickListeners();

        loadCalendarData();
    }

    private void setupCalendar() {
        calendarAdapter = new CalendarAdapter(
                this::handleDateClick,
                this::handleEventClick
        );

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 7); // 7 days per week
        binding.calendarRecyclerView.setLayoutManager(layoutManager);
        binding.calendarRecyclerView.setAdapter(calendarAdapter);

        updateCalendarHeader();
    }

    private void setupLegend() {
        User currentUser = AuthManager.loggedInUser;

        if (currentUser != null) {
            if (currentUser.getRole().contains("EventOrganizer")) {
                specialLabel = "Events";
            } else if (currentUser.getRole().contains("ProductServiceProvider")) {
                specialLabel = "Reservations";
            }
        }

        if (specialLabel != null) {
            binding.legendSpecialEvents.setVisibility(View.VISIBLE);
            binding.legendSpecialLabel.setText(String.format("Your %s", specialLabel));
        } else {
            binding.legendSpecialEvents.setVisibility(View.GONE);
        }
    }

    private void setupObservers() {
        calendarViewModel.getCalendarItems().observe(getViewLifecycleOwner(), events -> {
            if (events != null) {
                updateCalendarWithEvents(events);
            }
        });

        calendarViewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Log.e("CalendarFragment", errorMessage);
                ToastUtils.showCustomToast(requireContext(), errorMessage, true);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnPrevMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendarHeader();
            updateCalendarDays();
        });

        binding.btnNextMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendarHeader();
            updateCalendarDays();
        });

        binding.btnToday.setOnClickListener(v -> {
            currentMonth = LocalDateTime.now();
            updateCalendarHeader();
            updateCalendarDays();
        });
    }

    private void updateCalendarHeader() {
        String monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int year = currentMonth.getYear();
        binding.calendarTitle.setText(String.format("%s %d", monthName, year));
    }

    private void updateCalendarDays() {
        List<CalendarDay> days = CalendarUtils.getDaysForMonth(currentMonth);

        List<CalendarItem> currentEvents = calendarViewModel.getCalendarItems().getValue();
        if (currentEvents != null) {
            List<CalendarDay> daysWithEvents = CalendarUtils.mapEventsToDays(days, currentEvents);
            calendarAdapter.updateDays(daysWithEvents);
        } else {
            calendarAdapter.updateDays(days);
            calendarViewModel.loadCalendarItems();
        }
    }

    private void updateCalendarWithEvents(List<CalendarItem> events) {
        List<CalendarDay> days = CalendarUtils.getDaysForMonth(currentMonth);
        List<CalendarDay> daysWithEvents = CalendarUtils.mapEventsToDays(days, events);
        calendarAdapter.updateDays(daysWithEvents);
    }

    private void loadCalendarData() {
        updateCalendarDays();
        calendarViewModel.loadCalendarItems();
    }

    private void handleEventClick(CalendarItem event) {
        //todo: navigate to details page
    }

    private void handleDateClick(LocalDate date) {
        //todo: event and reservation details
        System.out.println("Date clicked: " + date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}