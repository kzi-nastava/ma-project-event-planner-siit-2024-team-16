package com.example.evenmate.fragments.pricelist;

import android.os.Bundle;
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
import com.example.evenmate.models.asset.AssetType;
import com.example.evenmate.models.pricelist.PriceListItem;
import com.example.evenmate.viewmodels.PriceListViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class PriceListFragment extends Fragment implements PriceListAdapter.OnPriceListActionListener {
    private PriceListViewModel viewModel;
    private PriceListAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MaterialButton btnDownloadPdf;
    private MaterialButton btnProducts;
    private MaterialButton btnServices;
    private MaterialButtonToggleGroup toggleType;
    private String selectedType = String.valueOf(AssetType.SERVICE);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_price_list);
        progressBar = view.findViewById(R.id.progress_bar);
        adapter = new PriceListAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        btnProducts = view.findViewById(R.id.pricelist_btn_products);
        btnServices = view.findViewById(R.id.pricelist_btn_services);
        toggleType = view.findViewById(R.id.toggle_pricelist_type);
        btnProducts.setOnClickListener(v -> switchType(String.valueOf(AssetType.PRODUCT)));
        btnServices.setOnClickListener(v -> switchType(String.valueOf(AssetType.SERVICE)));
        setupViewModel();
        return view;
    }

    private void switchType(String type) {
        if (!type.equals(selectedType)) {
            selectedType = type;
            viewModel.fetchPriceList(selectedType, null, null);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PriceListViewModel.class);
        viewModel.getPriceList().observe(getViewLifecycleOwner(), this::onPriceListChanged);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::onLoadingChanged);
        viewModel.getError().observe(getViewLifecycleOwner(), this::onError);
        viewModel.fetchPriceList(selectedType, null, null);
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
