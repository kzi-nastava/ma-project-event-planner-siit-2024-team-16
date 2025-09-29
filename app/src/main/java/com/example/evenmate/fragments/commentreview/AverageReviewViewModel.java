package com.example.evenmate.fragments.commentreview;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.commentreview.ReviewCreate;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AverageReviewViewModel extends ViewModel {
    private final MutableLiveData<Double> averageReview = new MutableLiveData<>(0.0);

    public LiveData<Double> getAverageReview() {
        return averageReview;
    }

    public void setAverageReview(Double averageReview) {
        this.averageReview.setValue(averageReview);
    }

    public void loadAverageReview(Long assetId, Long userId) {
        if (assetId != null) {
            ClientUtils.assetService.getById(assetId).enqueue(new Callback<Asset>() {
                @Override
                public void onResponse(Call<Asset> call, Response<Asset> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        averageReview.setValue(response.body().getAverageReview());
                    }
                }
                @Override
                public void onFailure(Call<Asset> call, Throwable t) {
                    Log.e("AverageReviewFragment", "Failed to load average review", t);
                }
            });
        } else if (userId != null) {
            ClientUtils.userService.getById(userId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getRole().equals("ProductServiceProvider")) {
                            averageReview.setValue(response.body().getAverageReview());
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {
                    Log.e("AverageReviewFragment", "Failed to load average review", throwable);
                }
            });
        }
    }

    public void submitUserReview(Long assetId, Long userId, int stars) {
        if (assetId != null) {
            ClientUtils.commentReviewService.reviewAsset(assetId, new ReviewCreate(stars)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        loadAverageReview(assetId, userId);
                    } else {
                        ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("AverageReviewFragment", "Failed to submit review", t);
                }
            });
        } else if (userId != null) {
            ClientUtils.commentReviewService.reviewProvider(userId, new ReviewCreate(stars)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        loadAverageReview(assetId, userId);
                    } else {
                        ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("AverageReviewFragment", "Failed to submit review", t);
                }
            });
        }
    }
}
