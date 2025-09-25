package com.example.evenmate.fragments.asset;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.evenmate.R;
import com.example.evenmate.adapters.ProductAdapter;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.databinding.FragmentProductsBinding;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class ProductsFragment extends ListFragment {
        private FragmentProductsBinding binding;
        private ProductsViewModel viewModel;
        private ProductAdapter adapter;
        private String fetchMode;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            binding = FragmentProductsBinding.inflate(inflater, container, false);
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            fetchMode = requireArguments().getString("fetch_mode", "ALL_PRODUCTS");
            viewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
            if ("FAVORITES".equals(fetchMode)) {
                viewModel.setFetchMode("FAVORITES");
                binding.productsHeading.setText(R.string.favorite_products);
            } else if ("YOUR_PRODUCTS".equals(fetchMode)){
                binding.productsHeading.setText(R.string.your_products);
                viewModel.setFetchMode("YOUR_PRODUCTS");
            } else {
                binding.productsHeading.setText(R.string.products);
                viewModel.setFetchMode("ALL_PRODUCTS");

            }

            adapter = new ProductAdapter(getActivity(), new ArrayList<>());

            adapter.setOnEditClickListener(product -> {
                        ProductFormFragment dialogFragment = ProductFormFragment.newInstance(product);
                        dialogFragment.show(getParentFragmentManager(), "EditProduct");
                    }
            );
            adapter.setOnDeleteClickListener(product -> new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Product")
                    .setMessage(String.format("Are you sure you want to delete %s? This action cannot be undone.", product.getName()))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Delete", (dialog, which) -> {
                        viewModel.deleteProduct(product.getId());
                        if(viewModel.getDeleteFailed())
                            ToastUtils.showCustomToast(requireContext(),
                                    viewModel.getDeleteFailed().toString(),
                                    true);
                        else
                            viewModel.fetchProducts();
                        viewModel.resetDeleteFailed();
                    })
                    .show()
            );

            setListAdapter(adapter);
            setupPagination();

            setupAddProductButton();
            setupFragmentResultListener();
            observeViewModel();
        }
        private void filter(){
            //todo andjela
        }

        private void setupPagination() {
            binding.btnPrevious.setOnClickListener(v -> viewModel.previousPage());
            binding.btnNext.setOnClickListener(v -> viewModel.nextPage());
            viewModel.getCurrentPage().observe(getViewLifecycleOwner(), currentPage ->
                    updatePaginationUI(currentPage, viewModel.getTotalPages().getValue())
            );
            viewModel.getTotalPages().observe(getViewLifecycleOwner(), totalPages ->
                    updatePaginationUI(viewModel.getCurrentPage().getValue(), totalPages)
            );
        }

        private void updatePaginationUI(Integer currentPage, Integer totalPages) {
            if (currentPage != null && totalPages != null) {
                binding.tvPageInfo.setText(String.format("Page %d of %d", currentPage, totalPages));
                binding.btnPrevious.setEnabled(currentPage > 1);
                binding.btnNext.setEnabled(currentPage < totalPages);
                binding.paginationLayout.setVisibility(totalPages > 1 ? View.VISIBLE : View.GONE);
            }
        }

        private void setupAddProductButton() {
            boolean isFavoritesMode = this.fetchMode.equals("FAVORITES");
            boolean isProvider = AuthManager.loggedInUser != null && AuthManager.loggedInUser.getRole().equals("ProductServiceProvider");

            if (isFavoritesMode || !isProvider) {
                binding.btnAddProduct.setVisibility(View.GONE);
            } else {
                binding.btnAddProduct.setVisibility(View.VISIBLE);
                binding.btnAddProduct.setOnClickListener(v -> {
                    ProductFormFragment dialogFragment = ProductFormFragment.newInstance(null);
                    dialogFragment.show(getParentFragmentManager(), "CreateProduct");
                });
            }
        }

        private void observeViewModel() {
            viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
                Log.d("Products", "Received products: " + products.size());
                assert getActivity() != null;
                adapter.setProducts(new ArrayList<>(products));
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();
                binding.list.setVisibility(products.isEmpty() ? View.GONE : View.VISIBLE);
                binding.textViewNoProducts.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
            });

            viewModel.getCurrentPage().observe(getViewLifecycleOwner(), page -> {
                Integer totalPages = viewModel.getTotalPages().getValue();
                if (totalPages != null) {
                    binding.tvPageInfo.setText(String.format("Page %d of %d", page, totalPages));
                }
            });

            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
                if (message != null && !message.isEmpty()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setupFragmentResultListener() {
            getParentFragmentManager().setFragmentResultListener("product_form_result", this, (requestKey, result) -> {
                boolean shouldRefresh = result.getBoolean("refresh_products", false);
                if (shouldRefresh) {
                    viewModel.fetchProducts();
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            viewModel.fetchProducts();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }