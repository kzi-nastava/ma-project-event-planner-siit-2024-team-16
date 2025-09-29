package com.example.evenmate.fragments.commentreview;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.adapters.CommentAdapter;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.commentreview.Comment;
import com.example.evenmate.models.commentreview.CommentCreate;
import com.example.evenmate.utils.ErrorUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsFragment extends Fragment {
    private static final String ARG_ASSET_ID = "asset_id";
    private static final String ARG_USER_ID = "user_id";
    private Long assetId;
    private Long userId;
    private CommentAdapter commentAdapter;
    private RecyclerView recyclerComments;
    private TextInputEditText inputComment;
    private MaterialButton btnAddComment;

    public static CommentsFragment newInstance(Long assetId, Long userId) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        if (assetId != null) args.putLong(ARG_ASSET_ID, assetId);
        if (userId != null) args.putLong(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        recyclerComments = view.findViewById(R.id.recycler_comments);
        inputComment = view.findViewById(R.id.input_comment);
        btnAddComment = view.findViewById(R.id.btn_add_comment);
        commentAdapter = new CommentAdapter(new ArrayList<>());
        recyclerComments.setAdapter(commentAdapter);
        recyclerComments.setLayoutManager(new LinearLayoutManager(getContext()));
        btnAddComment.setOnClickListener(v -> addComment());
        if (getArguments() != null) {
            assetId = getArguments().containsKey(ARG_ASSET_ID) ? getArguments().getLong(ARG_ASSET_ID) : null;
            userId = getArguments().containsKey(ARG_USER_ID) ? getArguments().getLong(ARG_USER_ID) : null;
        }
        if (AuthManager.loggedInUser == null || AuthManager.loggedInUser.getId().equals(userId)) {
            inputComment.setEnabled(false);
            btnAddComment.setEnabled(false);
        }
        loadComments();
        return view;
    }

    private void loadComments() {
        if (assetId != null) {
            ClientUtils.commentReviewService.getAssetComments(assetId, null, null).enqueue(new Callback<PaginatedResponse<Comment>>() {
                @Override
                public void onResponse(Call<PaginatedResponse<Comment>> call, Response<PaginatedResponse<Comment>> response) {
                    if (response.isSuccessful()) {
                        commentAdapter.setComments(response.body().getContent());
                    }
                }
                @Override
                public void onFailure(Call<PaginatedResponse<Comment>> call, Throwable t) {
                    Log.e("CommentsFragment", "Failed to load comments", t);
                    Toast.makeText(requireContext(), "Failed to load comments", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (userId != null) {
            ClientUtils.commentReviewService.getProviderComments(userId, null, null).enqueue(new Callback<PaginatedResponse<Comment>>() {
                @Override
                public void onResponse(Call<PaginatedResponse<Comment>> call, Response<PaginatedResponse<Comment>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        commentAdapter.setComments(response.body().getContent());
                    }
                }
                @Override
                public void onFailure(Call<PaginatedResponse<Comment>> call, Throwable t) {
                    Log.e("CommentsFragment", "Failed to load comments", t);
                    Toast.makeText(requireContext(), "Failed to load comments", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addComment() {
        String text = inputComment.getText() != null ? inputComment.getText().toString().trim() : "";
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(requireContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }
        if (assetId != null) {
            ClientUtils.commentReviewService.commentAsset(assetId, new CommentCreate(text)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Comment added successfully", Toast.LENGTH_SHORT).show();
                        inputComment.setText("");
                        loadComments();
                    } else {
                        ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("CommentsFragment", "Failed to add comment", t);
                    Toast.makeText(requireContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (userId != null) {
            ClientUtils.commentReviewService.commentProvider(userId, new CommentCreate(text)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Comment added successfully", Toast.LENGTH_SHORT).show();
                        inputComment.setText("");
                        loadComments();
                    } else {
                        ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("CommentsFragment", "Failed to add comment", t);
                    Toast.makeText(requireContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
