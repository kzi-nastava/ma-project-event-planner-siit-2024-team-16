package com.example.evenmate.clients;

import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.commentreview.Comment;
import com.example.evenmate.models.commentreview.CommentCreate;
import com.example.evenmate.models.commentreview.ReviewCreate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentReviewService {
    @GET("assets/{assetId}/comments")
    Call<PaginatedResponse<Comment>> getAssetComments(@Path("assetId") Long assetId, @Query("page") Integer page, @Query("size") Integer size);

    @GET("users/{userId}/comments")
    Call<PaginatedResponse<Comment>> getProviderComments(@Path("userId") Long userId, @Query("page") Integer page, @Query("size") Integer size);

    @POST("assets/{assetId}/comment")
    Call<Void> commentAsset(@Path("assetId") Long assetId, @Body CommentCreate comment);

    @POST("users/{userId}/comment")
    Call<Void> commentProvider(@Path("userId") Long userId, @Body CommentCreate comment);

    @POST("assets/{assetId}/review")
    Call<Void> reviewAsset(@Path("assetId") Long assetId, @Body ReviewCreate review);

    @POST("users/{userId}/review")
    Call<Void> reviewProvider(@Path("userId") Long userId, @Body ReviewCreate review);

    @PUT("comments/{id}/approve")
    Call<String> approveComment(@Path("id") Long id);

    @GET("comments")
    Call<PaginatedResponse<Comment>> getPendingComments(
            @Query("approved") Boolean approved,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @DELETE("comments/{id}")
    Call<String> deleteComment(@Path("id") Long id);
}

