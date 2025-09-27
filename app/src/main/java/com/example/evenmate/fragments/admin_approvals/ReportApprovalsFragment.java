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
import com.example.evenmate.adapters.ReportAdapter;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.user.Report;
import com.example.evenmate.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportApprovalsFragment extends Fragment {

    private RecyclerView reportsRecyclerView;
    private ReportAdapter adapter;
    private Button prevPageButton, nextPageButton;

    private List<Report> reports = new ArrayList<>();
    private int currentPage = 0;
    private final int pageSize = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_approvals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reportsRecyclerView = view.findViewById(R.id.reportsRecyclerView);
        prevPageButton = view.findViewById(R.id.prevPageButton);
        nextPageButton = view.findViewById(R.id.nextPageButton);

        adapter = new ReportAdapter(requireContext(), reports, this::loadReports);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reportsRecyclerView.setAdapter(adapter);

        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                loadReports();
            }
        });

        nextPageButton.setOnClickListener(v -> {
            currentPage++;
            loadReports();
        });

        loadReports();
    }

    private void loadReports() {
        ClientUtils.userService.getPendingReports(currentPage, pageSize)
                .enqueue(new Callback<PaginatedResponse<Report>>() {
                    @Override
                    public void onResponse(Call<PaginatedResponse<Report>> call, Response<PaginatedResponse<Report>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Report> loadedReports = response.body().getContent();
                            reports.clear();
                            reports.addAll(loadedReports);
                            adapter.notifyDataSetChanged();

                            prevPageButton.setEnabled(currentPage > 0);
                            nextPageButton.setEnabled(loadedReports.size() == pageSize);
                        } else {
                            ToastUtils.showCustomToast(requireContext(), "Failed to load reports", true);
                        }
                    }

                    @Override
                    public void onFailure(Call<PaginatedResponse<Report>> call, Throwable t) {
                        ToastUtils.showCustomToast(requireContext(), "Error: " + t.getMessage(), true);
                    }
                });
    }

    private Report convertToReport(Report r) {
        Report report = new Report();
        report.setId(r.getId());
        report.setReason(r.getReason());
        report.setReportedUser(r.getReportedUser());
        return report;
    }
}
