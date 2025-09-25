package com.example.evenmate.clients;

import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.commentreview.Comment;
import com.example.evenmate.models.commentreview.CommentCreate;
import com.example.evenmate.models.commentreview.Review;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentReviewService {
    @GET("assets/{assetId}/comments")
    Call<PaginatedResponse<Comment>> getAssetComments(@Path("assetId") Long assetId, @Query("page") Integer page, @Query("size") Integer size);

    @GET("users/{userId}/comments")
    Call<PaginatedResponse<Comment>> getProviderComments(@Path("userId") Long userId, @Query("page") Integer page, @Query("size") Integer size);

    @POST("assets/{assetId}/comment")
    Call<Comment> commentAsset(@Path("assetId") Long assetId, @Body CommentCreate comment);

    @POST("users/{userId}/comment")
    Call<Comment> commentProvider(@Path("userId") Long userId, @Body CommentCreate comment);

    @POST("assets/{assetId}/review")
    Call<Void> reviewAsset(@Path("assetId") Long assetId, @Body int stars);
}

