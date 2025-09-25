package com.example.evenmate.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.adapters.ReviewAdapter;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.clients.CommentReviewService;
import com.example.evenmate.models.commentreview.Review;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsView extends LinearLayout {
    private RecyclerView recyclerReviews;
    private ReviewAdapter reviewAdapter;
    private RatingBar inputReviewStars;
    private MaterialButton btnAddReview;
    private Long assetId;

    public ReviewsView(Context context) {
        super(context);
        init(context);
    }

    public ReviewsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReviewsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reviews, this, true);
        recyclerReviews = findViewById(R.id.recycler_reviews);
        inputReviewStars = findViewById(R.id.input_review_stars);
        btnAddReview = findViewById(R.id.btn_add_review);
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        recyclerReviews.setAdapter(reviewAdapter);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(context));
        btnAddReview.setOnClickListener(v -> addReview());
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
//        loadReviews();
    }

//    private void loadReviews() {
//        ClientUtils.commentReviewService.getAss(assetId).enqueue(new Callback<List<Review>>() {
//            @Override
//            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
//                if (response.isSuccessful()) {
//                    reviewAdapter.setReviews(response.body());
//                }
//            }
//            @Override
//            public void onFailure(Call<List<Review>> call, Throwable t) {
//                Toast.makeText(getContext(), "Failed to load reviews", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void addReview() {
        int stars = (int) inputReviewStars.getRating();
        if (stars < 1) {
            Toast.makeText(getContext(), "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }
        ClientUtils.commentReviewService.reviewAsset(assetId, stars).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    inputReviewStars.setRating(0);
//                    loadReviews();
                } else {
                    Toast.makeText(getContext(), "Failed to add review", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to add review", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

