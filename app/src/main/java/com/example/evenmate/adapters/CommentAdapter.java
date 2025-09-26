package com.example.evenmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.models.commentreview.Comment;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.username.setText(comment.getUser() != null ? comment.getUser().getFirstName() + " " + comment.getUser().getLastName() : "");
        holder.text.setText(comment.getText());
        holder.date.setText(comment.getDate() != null ? comment.getDate().toString() : "");
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView username, text, date;
        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.comment_username);
            text = itemView.findViewById(R.id.comment_text);
            date = itemView.findViewById(R.id.comment_date);
        }
    }
}

