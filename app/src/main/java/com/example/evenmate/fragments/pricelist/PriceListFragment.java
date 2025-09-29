package com.example.evenmate.fragments.pricelist;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.adapters.PriceListAdapter;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.asset.AssetType;
import com.example.evenmate.models.pricelist.PriceListItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class PriceListFragment extends Fragment implements PriceListAdapter.OnPriceListActionListener {
    private PriceListViewModel viewModel;
    private PriceListAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MaterialButton btnDownloadPdf;
    private MaterialButton btnProducts;
    private MaterialButton btnServices;
    private MaterialButtonToggleGroup toggleType;
    private MaterialButton btnPrevPage;
    private MaterialButton btnNextPage;
    private android.widget.TextView txtPageIndicator;
    private String selectedType = String.valueOf(AssetType.SERVICE);
    private int currentPage = 0;
    private int totalPages = 1;
    private final int PAGE_SIZE = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_price_list);
        progressBar = view.findViewById(R.id.progress_bar);
        adapter = new PriceListAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        btnDownloadPdf = view.findViewById(R.id.btn_download_pdf);
        btnProducts = view.findViewById(R.id.pricelist_btn_products);
        btnServices = view.findViewById(R.id.pricelist_btn_services);
        toggleType = view.findViewById(R.id.toggle_pricelist_type);
        btnPrevPage = view.findViewById(R.id.btn_prev_page);
        btnNextPage = view.findViewById(R.id.btn_next_page);
        txtPageIndicator = view.findViewById(R.id.txt_page_indicator);
        btnProducts.setOnClickListener(v -> switchType(String.valueOf(AssetType.PRODUCT)));
        btnServices.setOnClickListener(v -> switchType(String.valueOf(AssetType.SERVICE)));
        btnDownloadPdf.setOnClickListener(v -> downloadPdf());
        btnPrevPage.setOnClickListener(v -> goToPage(currentPage - 1));
        btnNextPage.setOnClickListener(v -> goToPage(currentPage + 1));
        setupViewModel();
        return view;
    }

    private void switchType(String type) {
        if (!type.equals(selectedType)) {
            selectedType = type;
            viewModel.fetchPriceList(selectedType, 0, PAGE_SIZE);
        }
    }

    private void downloadPdf() {
        progressBar.setVisibility(View.VISIBLE);
        ClientUtils.priceListService.getPriceListPdf(selectedType).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "price_list_" + selectedType + ".pdf");
                        InputStream is = response.body().byteStream();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                        is.close();
                        Toast.makeText(requireContext(), "PDF downloaded.", Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Toast.makeText(requireContext(), "Failed to download PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to download PDF", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PriceListViewModel.class);
        viewModel.getPriceList().observe(getViewLifecycleOwner(), this::onPriceListChanged);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::onLoadingChanged);
        viewModel.getError().observe(getViewLifecycleOwner(), this::onError);
        viewModel.getTotalPages().observe(getViewLifecycleOwner(), this::onTotalPagesChanged);
        viewModel.getCurrentPage().observe(getViewLifecycleOwner(), this::onCurrentPageChanged);
        viewModel.fetchPriceList(selectedType, 0, PAGE_SIZE);
    }

    private void onTotalPagesChanged(Integer pages) {
        totalPages = pages != null ? pages : 1;
        updatePaginationControls();
    }
    private void onCurrentPageChanged(Integer page) {
        currentPage = page != null ? page : 0;
        updatePaginationControls();
    }
    private void goToPage(int page) {
        if (page >= 0 && page < totalPages) {
            viewModel.fetchPriceList(selectedType, page, PAGE_SIZE);
        }
    }
    private void updatePaginationControls() {
        txtPageIndicator.setText("Page " + (currentPage + 1) + " / " + totalPages);
        btnPrevPage.setEnabled(currentPage > 0);
        btnNextPage.setEnabled(currentPage < totalPages - 1);
    }

    private void onPriceListChanged(List<PriceListItem> items) {
        adapter.setItems(items);
    }

    private void onLoadingChanged(Boolean isLoading) {
        progressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
    }

    private void onError(String error) {
        if (error != null) {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEdit(PriceListItem item) {
        EditPriceListItemDialogFragment dialog = EditPriceListItemDialogFragment.newInstance(item);
        dialog.setListener((price, discount) -> {
            viewModel.updatePriceListItem(item.getId(), price, discount, selectedType);
        });
        dialog.show(getParentFragmentManager(), "EditPriceListItemDialog");
    }
}
