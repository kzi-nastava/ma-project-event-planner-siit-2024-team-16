package com.example.evenmate.fragments;

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
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.adapters.CategoryAdapter;
import com.example.evenmate.models.Category;
import com.example.evenmate.models.CategoryRequest;
import com.example.evenmate.viewmodels.CategoryViewModel;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class CategoryManagementFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {
    private CategoryViewModel viewModel;
    private CategoryAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MaterialButton btnAddCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_management, container, false);
        recyclerView = view.findViewById(R.id.recycler_categories);
        progressBar = view.findViewById(R.id.progress_bar);
        btnAddCategory = view.findViewById(R.id.btn_add_category);
        adapter = new CategoryAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        setupViewModel();
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
        return view;
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.getCategories().observe(getViewLifecycleOwner(), this::onCategoriesChanged);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::onLoadingChanged);
        viewModel.getError().observe(getViewLifecycleOwner(), this::onError);
        viewModel.fetchCategories();
    }

    private void onCategoriesChanged(List<Category> categories) {
        adapter.setCategories(categories);
    }

    private void onLoadingChanged(Boolean isLoading) {
        progressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
    }

    private void onError(String error) {
        if (error != null) {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddCategoryDialog() {
        AddCategoryDialogFragment dialog = new AddCategoryDialogFragment();
        dialog.setListener((name, description) -> {
            viewModel.createCategory(new CategoryRequest(name, description));
        });
        dialog.show(getParentFragmentManager(), "AddCategoryDialog");
    }

    @Override
    public void onEdit(Category category) {
        AddCategoryDialogFragment dialog = AddCategoryDialogFragment.newInstance(category.getName(), category.getDescription());
        dialog.setListener((name, description) -> {
            viewModel.updateCategory(category.getId(), new CategoryRequest(name, description));
        });
        dialog.show(getParentFragmentManager(), "EditCategoryDialog");
    }

    @Override
    public void onDelete(Category category) {
        // Simple confirmation, can be replaced with a Material dialog
        viewModel.deleteCategory(category.getId());
    }
}

