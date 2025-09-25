package com.example.evenmate.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.adapters.CommentAdapter;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.clients.CommentReviewService;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.commentreview.Comment;
import com.example.evenmate.models.commentreview.CommentCreate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsView extends LinearLayout {
    private RecyclerView recyclerComments;
    private CommentAdapter commentAdapter;
    private TextInputEditText inputComment;
    private MaterialButton btnAddComment;
    private Long assetId;

    public CommentsView(Context context) {
        super(context);
        init(context);
    }

    public CommentsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommentsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_comments, this, true);
        recyclerComments = findViewById(R.id.recycler_comments);
        inputComment = findViewById(R.id.input_comment);
        btnAddComment = findViewById(R.id.btn_add_comment);
        commentAdapter = new CommentAdapter(new ArrayList<>());
        recyclerComments.setAdapter(commentAdapter);
        recyclerComments.setLayoutManager(new LinearLayoutManager(context));
        btnAddComment.setOnClickListener(v -> addComment());
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
        loadComments();
    }

    private void loadComments() {
        ClientUtils.commentReviewService.getAssetComments(assetId, null, null).enqueue(new Callback<PaginatedResponse<Comment>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Comment>> call, Response<PaginatedResponse<Comment>> response) {
                if (response.isSuccessful()) {
                    commentAdapter.setComments(response.body().getContent());
                }
            }
            @Override
            public void onFailure(Call<PaginatedResponse<Comment>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addComment() {
        String text = inputComment.getText() != null ? inputComment.getText().toString().trim() : "";
        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }
        ClientUtils.commentReviewService.commentAsset(assetId, new CommentCreate(text)).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    inputComment.setText("");
                    loadComments();
                } else {
                    Toast.makeText(getContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

