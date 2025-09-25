package com.example.evenmate.fragments.commentreview;

import android.os.Bundle;
import android.util.Log;
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
import com.example.evenmate.models.commentreview.ReviewCreate;
import com.example.evenmate.models.user.ProductServiceProvider;
import com.example.evenmate.models.user.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AverageReviewFragment extends Fragment {
    private static final String ARG_ASSET_ID = "asset_id";
    private static final String ARG_USER_ID = "user_id";
    private Long assetId;
    private RatingBar ratingBar;
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
        ratingBar = view.findViewById(R.id.average_review_ratingbar);
        if (getArguments() != null) {
            userId = getArguments().containsKey(ARG_USER_ID) ? getArguments().getLong(ARG_USER_ID) : null;
            assetId = getArguments().containsKey(ARG_ASSET_ID) ? getArguments().getLong(ARG_ASSET_ID) : null;
        }
        loadAverageReview();
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser) {
                submitUserReview((int) rating);
            }
        });
        return view;
    }

    private void loadAverageReview() {
        if (assetId != null) {
            ClientUtils.assetService.getById(assetId).enqueue(new Callback<Asset>() {
                @Override
                public void onResponse(Call<Asset> call, Response<Asset> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ratingBar.setRating(response.body().getAverageReview().floatValue());
                    }
                }
                @Override
                public void onFailure(Call<Asset> call, Throwable t) {
                    Log.e("AverageReviewFragment", "Failed to load average review", t);
                    Toast.makeText(requireContext(), "Failed to load average review", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (userId != null) {
            ClientUtils.userService.getById(userId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getRole().equals("ProductServiceProvider"))
                            ratingBar.setRating(response.body().getAverageReview().floatValue());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {
                    Log.e("AverageReviewFragment", "Failed to load average review", throwable);
                    Toast.makeText(requireContext(), "Failed to load average review", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void submitUserReview(int stars) {
        if (assetId != null) {
            ClientUtils.commentReviewService.reviewAsset(assetId, new ReviewCreate(stars)).enqueue(new Callback<Void>() {
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
                    Log.e("AverageReviewFragment", "Failed to submit review", t);
                    Toast.makeText(requireContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (userId != null) {
            ClientUtils.commentReviewService.reviewProvider(userId, new ReviewCreate(stars)).enqueue(new Callback<Void>() {
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
                    Log.e("AverageReviewFragment", "Failed to submit review", t);
                    Toast.makeText(requireContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
