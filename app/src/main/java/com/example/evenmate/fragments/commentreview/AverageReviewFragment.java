package com.example.evenmate.fragments.commentreview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.evenmate.R;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.commentreview.ReviewCreate;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AverageReviewFragment extends Fragment {
    private static final String ARG_ASSET_ID = "asset_id";
    private static final String ARG_USER_ID = "user_id";
    private AverageReviewViewModel viewModel;
    private Long assetId;
    private RatingBar ratingBar;
    private TextView averageReviewTextView;
    private Long userId;

    public static AverageReviewFragment newInstance(Long userId, Long assetId) {
        AverageReviewFragment fragment = new AverageReviewFragment();
        Bundle args = new Bundle();
        if (userId != null) args.putLong(ARG_USER_ID, userId);
        if (assetId != null) args.putLong(ARG_ASSET_ID, assetId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_average_review, container, false);
        viewModel = new ViewModelProvider(this).get(AverageReviewViewModel.class);

        ratingBar = view.findViewById(R.id.average_review_ratingbar);
        averageReviewTextView = view.findViewById(R.id.average_review_value);
        if (getArguments() != null) {
            userId = getArguments().containsKey(ARG_USER_ID) ? getArguments().getLong(ARG_USER_ID) : null;
            assetId = getArguments().containsKey(ARG_ASSET_ID) ? getArguments().getLong(ARG_ASSET_ID) : null;
        }

        viewModel.loadAverageReview(assetId, userId);

        if (AuthManager.loggedInUser == null || AuthManager.loggedInUser.getId().equals(userId)) {
            ratingBar.setEnabled(false);
        }

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser) {
                viewModel.submitUserReview(assetId, userId, (int) rating);
            }
        });

        viewModel.getAverageReview().observe(getViewLifecycleOwner(), averageReview -> {
            averageReviewTextView.setText(String.format("%.1f", averageReview));
            ratingBar.setRating(averageReview.floatValue());
        });

        return view;
    }
}
