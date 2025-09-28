package com.example.evenmate.fragments.admin_approvals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.adapters.CommentApprovalsAdapter;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.commentreview.Comment;
import com.example.evenmate.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentApprovalsFragment extends Fragment {
    private RecyclerView commentsRecyclerView;
    private CommentApprovalsAdapter adapter;
    private Button prevPageButton, nextPageButton;

    private List<Comment> comments = new ArrayList<>();
    private int currentPage = 0;
    private final int pageSize = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance){
        return inflater.inflate(R.layout.fragment_comments_approval,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        prevPageButton = view.findViewById(R.id.prevPageButton);
        nextPageButton = view.findViewById(R.id.nextPageButton);

        adapter = new CommentApprovalsAdapter(requireContext(), comments, this::loadComments);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentsRecyclerView.setAdapter(adapter);

        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                loadComments();
            }
        });

        nextPageButton.setOnClickListener(v -> {
            currentPage++;
            loadComments();
        });

        loadComments();
    }

    private void loadComments() {
        ClientUtils.commentReviewService.getPendingComments(false,currentPage,pageSize).enqueue(new Callback<PaginatedResponse<Comment>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Comment>> call, Response<PaginatedResponse<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comment> loadComments = response.body().getContent();
                    comments.clear();
                    comments.addAll(loadComments);
                    adapter.notifyDataSetChanged();
                    prevPageButton.setEnabled(currentPage > 0);
                    nextPageButton.setEnabled(loadComments.size() == pageSize);
                }else{
                    ToastUtils.showCustomToast(requireContext(), "Failed to load comments", true);
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<Comment>> call, Throwable throwable) {
                ToastUtils.showCustomToast(requireContext(), "Error: " + throwable.getMessage(), true);

            }
        });
    }

}
