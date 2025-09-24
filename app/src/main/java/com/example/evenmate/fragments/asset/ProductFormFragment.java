package com.example.evenmate.fragments.asset;

import static com.example.evenmate.utils.ImageUtils.MAX_COMPANY_IMAGES;
import static com.example.evenmate.utils.ImageUtils.setImageFromBase64;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.evenmate.R;
import com.example.evenmate.databinding.FragmentProductFormBinding;
import com.example.evenmate.models.Category;
import com.example.evenmate.models.asset.ProductRequest;
import com.example.evenmate.models.asset.Product;
import com.example.evenmate.utils.ImageUtils;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductFormFragment extends DialogFragment implements ImageUtils.ImageHandlerCallback {

    private ProductFormViewModel viewModel;
    private List<Category> categoryList;
    private Category selectedCategory = null;
    private FragmentProductFormBinding binding;
    private ImageUtils imageUtils;
    private ProductRequest product;
    private List<String> imagesBase64 = new ArrayList<>();
    private LinearLayout imagesContainer;
    private String title;
    private boolean isEdit;

    public ProductFormFragment() {
    }

    public static ProductFormFragment newInstance(Product product) {
        ProductFormFragment fragment = new ProductFormFragment();
        Bundle args = new Bundle();
        args.putParcelable("product", product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageUtils = new ImageUtils(this, this);

        if (getArguments() != null) {
            Product receivedProduct = getArguments().getParcelable("product");
            if (receivedProduct != null) {
                product = receivedProduct.toRequest();
                if (product != null) {
                    imagesBase64 = product.getImages();
                }
            }
        }
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentProductFormBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(ProductFormViewModel.class);

        imagesContainer = binding.imagesContainer;
        title = product == null ? "Add Product" : "Edit Product";
        isEdit = product != null;

        binding.iconUpload.setOnClickListener(v -> {
            if (imagesBase64.size() < MAX_COMPANY_IMAGES) {
                imageUtils.selectImage();
            } else {
                Toast.makeText(requireContext(), "Maximum " + MAX_COMPANY_IMAGES + " images allowed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSuggestCategory.setOnClickListener(v -> {
            binding.layoutNewCategory.setVisibility(View.VISIBLE);
            binding.btnSuggestCategory.setVisibility(View.GONE);
        });

        binding.btnCancelSuggestion.setOnClickListener(v -> {
            binding.layoutNewCategory.setVisibility(View.GONE);
            binding.btnSuggestCategory.setVisibility(View.VISIBLE);
        });


        setupCategories();
        setupSaveButton();
        setupFormFields();
        observeViewModel();

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    private void setupCategories() {
        viewModel.fetchCategories();
        viewModel.getCategories().observe(this, categories -> {
            categoryList = categories;
            if (categories != null) {
                List<String> categoryNames = new ArrayList<>();
                categoryNames.add("Select category");
                for (Category c : categories) {
                    categoryNames.add(c.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        categoryNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerCategory.setAdapter(adapter);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getSuccess().observe(this, success -> {
            if (success != null) {
                ToastUtils.showCustomToast(requireContext(),
                        success,
                        false);
                sendResultAndDismiss();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                ToastUtils.showCustomToast(requireContext(),
                        error,
                        true);
            }
        });
    }

    private void sendResultAndDismiss() {
        Bundle result = new Bundle();
        result.putBoolean("refresh_products", true);
        getParentFragmentManager().setFragmentResult("product_form_result", result);
        dismissAllowingStateLoss();
    }

    private void setupFormFields() {
        if (product != null) {
            binding.editName.setText(product.getName());
            binding.spinnerCategory.setVisibility(View.GONE);
            binding.layoutNewCategory.setVisibility(View.GONE);
            binding.suggestionRow.setVisibility(View.GONE);
            binding.editDescription.setText(product.getDescription());
            binding.editDiscount.setText(Integer.toString(product.getDiscount()));
            binding.editPrice.setText(Double.toString(product.getPrice()));
            binding.switchVisible.setChecked(product.getIsVisible());
            binding.switchAvailable.setChecked(product.getIsAvailable());
            updateImagesUI();
        }
    }

    private void updateImagesUI() {
        imagesContainer.removeAllViews();

        for (int i = 0; i < imagesBase64.size(); i++) {
            final int index = i;
            String base64Image = imagesBase64.get(i);

            LinearLayout imageContainer = new LinearLayout(requireContext());
            imageContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                    dpToPx(80), dpToPx(100));
            containerParams.setMargins(dpToPx(8), 0, dpToPx(8), 0);
            imageContainer.setLayoutParams(containerParams);

            ImageView imageView = new ImageView(requireContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    dpToPx(80), dpToPx(80));
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            setImageFromBase64(imageView, base64Image);

            ImageView deleteButton = new ImageView(requireContext());
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                    dpToPx(20), dpToPx(20));
            deleteParams.gravity = Gravity.CENTER;
            deleteButton.setLayoutParams(deleteParams);
            deleteButton.setImageResource(R.drawable.ic_red_delete);
            deleteButton.setOnClickListener(v -> {
                imagesBase64.remove(index);
                updateImagesUI();
            });

            imageContainer.addView(imageView);
            imageContainer.addView(deleteButton);
            imagesContainer.addView(imageContainer);
        }

        binding.iconUpload.setVisibility(
                imagesBase64.size() < MAX_COMPANY_IMAGES ? View.VISIBLE : View.GONE);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                getProductInput();
                if (Objects.equals(this.title, "Add Product")) {
                    viewModel.addProduct(product);
                } else {
                    viewModel.updateProduct(product);
                }
            }
        });
    }

    private void getProductInput() {
        if (product == null)
            product = new ProductRequest();
        product.setName(binding.editName.getText().toString());
        product.setDescription(binding.editDescription.getText().toString());
        product.setPrice(Double.parseDouble(binding.editPrice.getText().toString()));
        product.setDiscount(Integer.parseInt(binding.editDiscount.getText().toString()));
        product.setIsVisible(binding.switchVisible.isChecked());
        product.setIsAvailable(binding.switchAvailable.isChecked());
        if(!isEdit) {
            if (binding.layoutNewCategory.getVisibility() == View.VISIBLE) {
                product.setNewCategoryName(binding.editNewCategoryName.getText().toString());
                product.setNewCategoryDescription(binding.editNewCategoryDescription.getText().toString());
            } else {
                product.setCategoryId(selectedCategory.getId());
            }
        }
        product.setImages(imagesBase64);
    }

    private boolean validateInput() {
        boolean isValid = true;

        clearAllErrors();
        String name = Objects.requireNonNull(binding.editName.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.editDescription.getText()).toString().trim();
        String price = Objects.requireNonNull(binding.editPrice.getText()).toString().trim();
        String discount = Objects.requireNonNull(binding.editDiscount.getText()).toString().trim();
        int pos = binding.spinnerCategory.getSelectedItemPosition();

        if (pos == 0) {
            selectedCategory = null;
        } else {
            selectedCategory = categoryList.get(pos - 1);
        }
        String newCategoryName = Objects.requireNonNull(binding.editNewCategoryName.getText()).toString().trim();
        String newCategoryDescription = Objects.requireNonNull(binding.editNewCategoryDescription.getText()).toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.editName.setError("Name is required");
            isValid = false;
        } else {
            binding.editName.setError(null);
        }

        if (TextUtils.isEmpty(description)) {
            binding.editDescription.setError("Description is required");
            isValid = false;
        } else {
            binding.editDescription.setError(null);
        }

        if (TextUtils.isEmpty(price)) {
            binding.editPrice.setError("Price is required");
            isValid = false;
        } else {
            try {
                double priceDouble = Double.parseDouble(price);
                if (priceDouble <= 0) {
                    binding.editPrice.setError("Price must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                binding.editPrice.setError("Price must be a valid number");
                isValid = false;
            }
        }
        if (TextUtils.isEmpty(discount)) {
            binding.editDiscount.setError("Discount is required");
            isValid = false;
        } else {
            try {
                int discountDouble = Integer.parseInt(discount);
                if (discountDouble <= 0) {
                    binding.editDiscount.setError("Discount must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                binding.editDiscount.setError("Discount must be a valid number");
                isValid = false;
            }
        }

        if(!isEdit) {
            if (binding.layoutNewCategory.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(newCategoryName)) {
                    binding.editNewCategoryName.setError("New category name is required");
                    isValid = false;
                } else {
                    binding.editNewCategoryName.setError(null);
                }
                if (TextUtils.isEmpty(newCategoryDescription)) {
                    binding.editNewCategoryDescription.setError("New category description is required");
                    isValid = false;
                } else {
                    binding.editNewCategoryDescription.setError(null);
                }
            } else if (selectedCategory == null) {
                isValid = false;
                ToastUtils.showCustomToast(getContext(), "Category must be selected", true);
            }
        }
        return isValid;
    }
    
    private void clearAllErrors() {
        binding.editName.setError(null);
        binding.editDiscount.setError(null);
        binding.editDescription.setError(null);
        binding.editPrice.setError(null);
        binding.editNewCategoryDescription.setError(null);
        binding.editNewCategoryName.setError(null);
    }

    @Override
    public void onImageSelected(String base64Image, Bitmap bitmap) {
        imagesBase64.add(base64Image);
        updateImagesUI();
    }

    @Override
    public void onImageError(String error) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
    }
}
