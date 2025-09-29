package com.example.evenmate.fragments.service;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.evenmate.R;
import com.example.evenmate.fragments.commentreview.AverageReviewFragment;
import com.example.evenmate.fragments.commentreview.CommentsFragment;
import com.example.evenmate.models.service.Service;

public class ServiceDetailsFragment extends Fragment {
    private static final String ARG_SERVICE_ID = "service_id";
    private ServiceDetailsViewModel viewModel;
    private Service service;

    public static ServiceDetailsFragment newInstance(Long serviceId) {
        ServiceDetailsFragment fragment = new ServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVICE_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_details, container, false);
        viewModel = new ViewModelProvider(this).get(ServiceDetailsViewModel.class);

        long serviceId = getArguments() != null ? getArguments().getLong(ARG_SERVICE_ID, -1) : -1;
        if (serviceId != -1) {
            viewModel.fetchServiceById(serviceId);
            viewModel.isFavorite(serviceId);
        }

        Button btnFavorite = view.findViewById(R.id.btn_favorite);
        Button btnReserve = view.findViewById(R.id.btn_reserve);
        Button btnChat = view.findViewById(R.id.btn_chat);
        TextView tvServiceName = view.findViewById(R.id.service_name);
        TextView tvCategory = view.findViewById(R.id.service_category);
        TextView tvDescription = view.findViewById(R.id.service_description);
        TextView tvDistinctiveness = view.findViewById(R.id.service_distinctiveness);
        TextView tvReservationDeadline = view.findViewById(R.id.service_reservation_deadline);
        TextView tvCancellationDeadline = view.findViewById(R.id.service_cancellation_deadline);
        LinearLayout layoutProviderInfo = view.findViewById(R.id.layout_provider_info);
        TextView tvProviderName = view.findViewById(R.id.provider_name);
        TextView tvProviderPhone = view.findViewById(R.id.provider_phone);
        TextView tvPriceOld = view.findViewById(R.id.service_price_old);
        TextView tvPriceNew = view.findViewById(R.id.service_price_new);
        TextView tvLength = view.findViewById(R.id.service_length);

        viewModel.getService().observe(getViewLifecycleOwner(), service -> {
            this.service = service;
            tvServiceName.setText(service.getName());
            tvCategory.setText(service.getCategory().getName());
            tvDescription.setText(service.getDescription());
            tvDistinctiveness.setText(service.getDistinctiveness() != null ? service.getDistinctiveness() : "N/A");
            tvProviderName.setText(service.getProvider().getFirstName() + " " + service.getProvider().getLastName());
            tvProviderPhone.setText(service.getProvider().getPhone());
            if (!service.getPriceAfterDiscount().equals(service.getPrice())) {
                tvPriceOld.setVisibility(View.VISIBLE);
                tvPriceOld.setText(String.valueOf(service.getPrice()));
                tvPriceNew.setText(String.valueOf(service.getPriceAfterDiscount()));
            } else {
                tvPriceOld.setVisibility(View.GONE);
                tvPriceNew.setText(String.valueOf(service.getPrice()));
            }
            tvLength.setText(formatLength(service.getLength(), service.getMinLength(), service.getMaxLength()));

            tvCancellationDeadline.setText(service.getCancellationDeadline() + "d");
            tvReservationDeadline.setText(service.getReservationDeadline() + "d");

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.service_comments_container, CommentsFragment.newInstance(service.getId(), null))
                    .commit();

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.service_average_review_container, AverageReviewFragment.newInstance(null, service.getId()))
                    .commit();
        });

        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFavorite -> {
            btnFavorite.setText(isFavorite ? "Unfavorite" : "Favorite");
        });

        btnFavorite.setOnClickListener(v -> viewModel.toggleFavorite(service.getId()));
        btnReserve.setOnClickListener(v -> viewModel.reserveService());
        btnChat.setOnClickListener(v -> viewModel.initiateChat(service.getProvider().getId()));
        layoutProviderInfo.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("userId", service.getProvider().getId());
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
            navController.navigate(R.id.action_serviceDetailsFragment_to_profileFragment, args);
        });

        return view;
    }

    private String formatLength(Integer length, Integer minLength, Integer maxLength) {
        if (length != null) {
            return formatTime(length);
        } else {
            return formatTime(minLength) + " - " + formatTime(maxLength);
        }
    }

    private String formatTime(Integer time) {
        return time / 60 + "h " + time % 60 + "m";
    }
}
