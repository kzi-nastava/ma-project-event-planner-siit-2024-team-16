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
import com.example.evenmate.models.user.Report;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final Context context;
    private List<Report> reports;
    private final OnReportUpdated callback;

    public interface OnReportUpdated {
        void onUpdated();
    }

    public ReportAdapter(Context context, List<Report> reports, OnReportUpdated callback) {
        this.context = context;
        this.reports = reports;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.reasonText.setText(report.getReason());
        holder.userText.setText(report.getReportedUser().getFirstName() + " "
                + report.getReportedUser().getLastName() + " (" + report.getReportedUser().getRole() + ")");

        holder.approveButton.setOnClickListener(v -> approveReport(report));
        holder.deleteButton.setOnClickListener(v -> deleteReport(report));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    private void approveReport(Report report) {
        ClientUtils.userService.approveReport(report.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "User suspended successfully", Toast.LENGTH_SHORT).show();
                    callback.onUpdated();
                } else {
                    Toast.makeText(context, "Failed to suspend user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteReport(Report report) {
        ClientUtils.userService.deleteReport(report.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Report deleted", Toast.LENGTH_SHORT).show();
                    callback.onUpdated();
                } else {
                    Toast.makeText(context, "Failed to delete report", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reasonText, userText;
        ImageButton approveButton, deleteButton;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reasonText = itemView.findViewById(R.id.report_reason);
            userText = itemView.findViewById(R.id.report_user);
            approveButton = itemView.findViewById(R.id.approve_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
