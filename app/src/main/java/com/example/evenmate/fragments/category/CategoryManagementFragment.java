package com.example.evenmate.fragments.category;

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
import com.example.evenmate.adapters.CategorySuggestionAdapter;
import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.category.CategoryRequest;
import com.example.evenmate.models.category.CategorySuggestion;
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
    private RecyclerView recyclerSuggestions;
    private CategorySuggestionAdapter suggestionAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_management, container, false);
        recyclerView = view.findViewById(R.id.recycler_categories);
        progressBar = view.findViewById(R.id.progress_bar);
        btnAddCategory = view.findViewById(R.id.btn_add_category);
        adapter = new CategoryAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        recyclerSuggestions = view.findViewById(R.id.recycler_category_suggestions);
        suggestionAdapter = new CategorySuggestionAdapter(new ArrayList<>(), new CategorySuggestionAdapter.OnSuggestionActionListener() {
            @Override
            public void onApprove(CategorySuggestion suggestion) {
                viewModel.approveSuggestion(suggestion.getId());
            }
            @Override
            public void onEdit(CategorySuggestion suggestion) {
                showEditSuggestionDialog(suggestion);
            }
            @Override
            public void onUpdateAssetCategory(CategorySuggestion suggestion) {
                showUpdateAssetCategoryDialog(suggestion);
            }
        });
        recyclerSuggestions.setAdapter(suggestionAdapter);
        setupViewModel();
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
        return view;
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.getCategories().observe(getViewLifecycleOwner(), this::onCategoriesChanged);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::onLoadingChanged);
        viewModel.getError().observe(getViewLifecycleOwner(), this::onError);
        viewModel.getSuggestions().observe(getViewLifecycleOwner(), this::onSuggestionsChanged);
        viewModel.fetchCategories();
        viewModel.fetchSuggestions();
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

    private void onSuggestionsChanged(List<CategorySuggestion> suggestions) {
        suggestionAdapter.setSuggestions(suggestions);
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
        viewModel.deleteCategory(category.getId());
    }

    private void showEditSuggestionDialog(CategorySuggestion suggestion) {
        AddCategoryDialogFragment dialog = AddCategoryDialogFragment.newInstance(suggestion.getName(), suggestion.getDescription());
        dialog.setListener((name, description) -> viewModel.editSuggestion(suggestion.getId(), new CategoryRequest(name, description)));
        dialog.show(getParentFragmentManager(), "EditSuggestionDialog");
    }

    private void showUpdateAssetCategoryDialog(CategorySuggestion suggestion) {
        List<Category> categories = adapter != null ? adapter.getCategories() : new ArrayList<>();
        UpdateAssetCategoryDialogFragment dialog = UpdateAssetCategoryDialogFragment.newInstance(suggestion, categories);
        dialog.setListener((selectedCategory) -> {
            viewModel.updateAssetCategory(suggestion.getAssetId(), selectedCategory.getId());
        });
        dialog.show(getParentFragmentManager(), "UpdateAssetCategoryDialog");
    }
}
