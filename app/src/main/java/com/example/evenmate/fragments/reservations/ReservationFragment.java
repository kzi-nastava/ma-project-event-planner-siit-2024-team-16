package com.example.evenmate.fragments.reservations;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.evenmate.R;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.service.Service;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.asset.ReservationRequest;
import com.example.evenmate.models.asset.Reservation;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationFragment extends Fragment {

    private Spinner eventSpinner;
    private EditText durationInput, timeInput;
    private Button nextButton, backButton, confirmButton;
    private LinearLayout step1Layout, step2Layout;

    private List<Event> events;
    private Long serviceId;
    private Event selectedEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        step1Layout = view.findViewById(R.id.step1Layout);
        step2Layout = view.findViewById(R.id.step2Layout);

        eventSpinner = view.findViewById(R.id.eventSpinner);
        nextButton = view.findViewById(R.id.nextButton);

        durationInput = view.findViewById(R.id.durationInput);
        timeInput = view.findViewById(R.id.timeInput);
        backButton = view.findViewById(R.id.backButton);
        confirmButton = view.findViewById(R.id.confirmButton);

        if (getArguments() != null) {
            serviceId = (Long) getArguments().getLong("service_id");
        }

        loadEvents();

        nextButton.setOnClickListener(v -> {
            int pos = eventSpinner.getSelectedItemPosition();
            if (pos >= 0 && events != null) {
                selectedEvent = events.get(pos);
                step1Layout.setVisibility(View.GONE);
                step2Layout.setVisibility(View.VISIBLE);
            }
        });

        backButton.setOnClickListener(v -> {
            step2Layout.setVisibility(View.GONE);
            step1Layout.setVisibility(View.VISIBLE);
        });

        confirmButton.setOnClickListener(v -> submitReservation());

        return view;
    }

    private void loadEvents() {
        ClientUtils.eventService.getEventsByOrganizer(0, 1000).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Event>> call, Response<PaginatedResponse<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events = response.body().getContent();
                    ArrayAdapter<Event> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, events);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    eventSpinner.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<PaginatedResponse<Event>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReservation() {
        if (selectedEvent == null || durationInput.getText().toString().isEmpty() || timeInput.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = Integer.parseInt(durationInput.getText().toString());
        String startTimeStr = timeInput.getText().toString(); // HH:mm

        try {
            LocalDateTime eventDate = selectedEvent.getDate().atStartOfDay();

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startLocalTime = LocalTime.parse(startTimeStr, timeFormatter);

            LocalDateTime startDateTime = eventDate.withHour(startLocalTime.getHour()).withMinute(startLocalTime.getMinute());

            ReservationRequest request = new ReservationRequest();
            request.setEventId(selectedEvent.getId());
            request.setServiceId(serviceId);
            request.setDesirableLength(duration);
            request.setDesirableStart(startDateTime);

            ClientUtils.serviceService.createReservation(request).enqueue(new Callback<Reservation>() {
                @Override
                public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Reservation res = response.body();
                        Toast.makeText(requireContext(), "Reservation confirmed: " + res.getId(), Toast.LENGTH_LONG).show();
                    } else {
                        String errorMessage = "Failed to create reservation";
                        if (response.errorBody() != null) {
                            try {
                                String errorJson = response.errorBody() != null ? response.errorBody().string() : null;
                                if (errorJson != null) {
                                    JSONObject obj = new JSONObject(errorJson);
                                    String rawMsg = obj.optString("message");
                                    errorMessage= rawMsg.replaceAll(".*\"(.*)\".*", "$1");
                                }
                            } catch (Exception ignored) {}
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Reservation> call, Throwable t) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Invalid time format", Toast.LENGTH_SHORT).show();
        }
    }

}
