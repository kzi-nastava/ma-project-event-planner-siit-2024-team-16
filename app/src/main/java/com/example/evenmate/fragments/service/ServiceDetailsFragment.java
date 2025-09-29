package com.example.evenmate.fragments.service;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.evenmate.R;
import com.example.evenmate.fragments.reservations.ReservationFragment;
import com.example.evenmate.models.asset.Service;

public class ServiceDetailsFragment extends Fragment {

    private long serviceId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            serviceId = getArguments().getLong("SERVICE_ID", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_service_details, container, false);

        Button reserveButton = view.findViewById(R.id.reserve_button);
        reserveButton.setOnClickListener(v -> openReservationFragment());

        return view;
    }

    private void openReservationFragment() {
        Bundle bundle = new Bundle();
        Service service = new Service();
        service.setId(serviceId);
        bundle.putLong("service_id", service.getId());

        ReservationFragment fragment = new ReservationFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
