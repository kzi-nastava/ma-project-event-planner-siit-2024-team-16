package com.example.evenmate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.commentreview.Comment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentApprovalsAdapter extends RecyclerView.Adapter<CommentApprovalsAdapter.CommentApprovalViewHolder>{

     private final Context context;
     private List<Comment> comments;
     private final OnCommentUpdated callback;
     public interface OnCommentUpdated{
         void onUpdated();
     }
     public CommentApprovalsAdapter(Context context, List<Comment> comments, OnCommentUpdated callback){
         this.context = context;
         this.comments = comments;
         this.callback = callback;
     }

     @NonNull
     @Override
     public CommentApprovalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(context).inflate(R.layout.item_comment_approval, parent, false);
         return new CommentApprovalViewHolder(view);
     }

    @Override
    public void onBindViewHolder(@NonNull CommentApprovalViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.text.setText(comment.getText());
        holder.username.setText(comment.getUser().getFirstName() + " "
                + comment.getUser().getLastName() + " (" + comment.getUser().getRole() + ")");
        if (comment.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            holder.date.setText(sdf.format(comment.getDate()));
        } else {
            holder.date.setText("");
        }
        holder.approveButton.setOnClickListener(v->approveComment(comment));
        holder.deleteButton.setOnClickListener(v->deleteComment(comment));
    }

    private void deleteComment(Comment comment) {
        ClientUtils.commentReviewService.deleteComment(comment.getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                    callback.onUpdated();
                }else {
                    Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Toast.makeText(context, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approveComment(Comment comment) {
        ClientUtils.commentReviewService.approveComment(comment.getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                    callback.onUpdated();
                }else {
                    Toast.makeText(context, "Failed to leave a comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Toast.makeText(context, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static  class CommentApprovalViewHolder extends RecyclerView.ViewHolder{
        TextView text, username, date;
        ImageButton approveButton, deleteButton;

        public CommentApprovalViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.comment_text);
            username = itemView.findViewById(R.id.comment_username);
            date = itemView.findViewById(R.id.comment_date);
            approveButton = itemView.findViewById(R.id.approve_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

}
