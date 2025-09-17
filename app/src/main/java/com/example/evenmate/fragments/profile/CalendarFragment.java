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

        // Show/hide legend items based on user role
        if (specialLabel != null) {
            binding.legendSpecialEvents.setVisibility(View.VISIBLE);
            binding.legendSpecialLabel.setText("Your " + specialLabel);
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
                // Handle error display (could show toast, snackbar, etc.)
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
        binding.calendarTitle.setText(monthName + " " + year);
    }

    private void updateCalendarDays() {
        List<CalendarDay> days = CalendarUtils.getDaysForMonth(currentMonth);
        calendarAdapter.updateDays(days);
    }

    private void updateCalendarWithEvents(List<CalendarItem> events) {
        List<CalendarDay> days = CalendarUtils.getDaysForMonth(currentMonth);
        List<CalendarDay> daysWithEvents = CalendarUtils.mapEventsToDays(days, events);
        calendarAdapter.updateDays(daysWithEvents);
    }

    private void loadCalendarData() {
        calendarViewModel.loadCalendarItems();
        updateCalendarDays();
        testDateParsing();

    }
    //todo: otvarati detalje i prebacivanje sa jednog na drugi mesec

    private void handleDateClick(LocalDate date) {
        System.out.println("Date clicked: " + date);
        // Handle date click - could navigate to day view or create event
    }

    private void handleEventClick(CalendarItem event) {
        //todo: navigate to details
//        if ("reservation".equals(event.getType())) {
//             NavController navController = Navigation.findNavController(requireView());
//             CalendarFragmentDirections.ActionCalendarToEventDetails action =
//                 CalendarFragmentDirections.actionCalendarToEventDetails(event.getId());
//             navController.navigate(action);
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Add this method to your CalendarFragment for testing
    private void testDateParsing() {
        // Test with your actual API date format
        String testDate = "2025-09-28T00:00:00";

        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(testDate);
            java.time.LocalDate date = dateTime.toLocalDate();

            Log.d("DateTest", "Original string: " + testDate);
            Log.d("DateTest", "Parsed LocalDateTime: " + dateTime);
            Log.d("DateTest", "Parsed LocalDate: " + date);
            Log.d("DateTest", "Current month being viewed: " + currentMonth.toLocalDate());

            // Check if the event date is in the current month being viewed
            boolean sameMonth = date.getMonth() == currentMonth.getMonth() &&
                    date.getYear() == currentMonth.getYear();
            Log.d("DateTest", "Event is in current viewing month: " + sameMonth);

        } catch (Exception e) {
            Log.e("DateTest", "Error parsing test date: " + e.getMessage());
        }
    }

// Call this method in your loadCalendarData() method to test:
}