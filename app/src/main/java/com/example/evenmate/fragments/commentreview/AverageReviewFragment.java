package com.example.evenmate.fragments.commentreview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.asset.Asset;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AverageReviewFragment extends Fragment {
    private static final String ARG_ASSET_ID = "asset_id";
    private static final String ARG_AVERAGE_REVIEW = "average_review";
    private Long assetId;
    private Double averageReview;
    private RatingBar ratingBar;

    public static AverageReviewFragment newInstance(Long assetId, Double averageReview) {
        AverageReviewFragment fragment = new AverageReviewFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ASSET_ID, assetId);
        args.putDouble(ARG_AVERAGE_REVIEW, averageReview);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_average_review, container, false);
        ratingBar = view.findViewById(R.id.average_review_ratingbar);
        if (getArguments() != null) {
            assetId = getArguments().getLong(ARG_ASSET_ID, 0);
            averageReview = getArguments().getDouble(ARG_AVERAGE_REVIEW, 0.0);
        }
        ratingBar.setRating(averageReview.floatValue());
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser) {
                submitUserReview((int) rating);
            }
        });
        return view;
    }

    private void loadAverageReview() {
        ClientUtils.assetService.getById(assetId).enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ratingBar.setRating(response.body().getAverageReview().floatValue());
                }
            }
            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load average review", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitUserReview(int stars) {
        ClientUtils.commentReviewService.reviewAsset(assetId, stars).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Review submitted", Toast.LENGTH_SHORT).show();
                    loadAverageReview();
                } else {
                    Toast.makeText(requireContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

