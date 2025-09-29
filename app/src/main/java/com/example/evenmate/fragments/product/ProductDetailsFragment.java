package com.example.evenmate.fragments.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.evenmate.R;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.fragments.commentreview.AverageReviewFragment;
import com.example.evenmate.fragments.commentreview.CommentsFragment;
import com.example.evenmate.models.asset.Product;

public class ProductDetailsFragment extends Fragment {
    private static final String ARG_PRODUCT_ID = "product_id";
    private ProductDetailsViewModel viewModel;
    private Product product;

    public static ProductDetailsFragment newInstance(Long productId) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        viewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);

        long productId = getArguments() != null ? getArguments().getLong(ARG_PRODUCT_ID, -1) : -1;
        if (productId != -1) {
            viewModel.fetchProductById(productId);
            if (AuthManager.loggedInUser != null) {
                viewModel.isFavorite(productId);
            }
        }

        Button btnFavorite = view.findViewById(R.id.btn_favorite);
        Button btnPurchase = view.findViewById(R.id.btn_purchase);
        Button btnChat = view.findViewById(R.id.btn_chat);
        TextView name = view.findViewById(R.id.product_name);
        TextView category = view.findViewById(R.id.product_category);
        TextView description = view.findViewById(R.id.product_description);
        LinearLayout layoutProviderInfo = view.findViewById(R.id.layout_provider_info);
        TextView providerName = view.findViewById(R.id.product_provider_name);
        TextView providerPhone = view.findViewById(R.id.product_provider_phone);
        TextView priceOld = view.findViewById(R.id.product_price_old);
        TextView priceNew = view.findViewById(R.id.product_price_new);

        viewModel.getProduct().observe(getViewLifecycleOwner(), product -> {
            this.product = product;
            name.setText(product.getName());
            category.setText(product.getCategory().getName());
            description.setText(product.getDescription());
            providerName.setText(product.getProvider().getFirstName() + " " + product.getProvider().getLastName());
            providerPhone.setText(product.getProvider().getPhone());
            if (!product.getPriceAfterDiscount().equals(product.getPrice())) {
                priceOld.setVisibility(View.VISIBLE);
                priceOld.setText(String.valueOf(product.getPrice()));
                priceNew.setText(String.valueOf(product.getPriceAfterDiscount()));
            } else {
                priceOld.setVisibility(View.GONE);
                priceNew.setText(String.valueOf(product.getPrice()));
            }
            btnFavorite.setVisibility(AuthManager.loggedInUser != null ? View.VISIBLE : View.GONE);
            btnPurchase.setEnabled(product.getIsAvailable() && AuthManager.loggedInUser != null && AuthManager.loggedInUser.getRole().equals("EventOrganizer"));
            btnChat.setEnabled(AuthManager.loggedInUser != null && !AuthManager.loggedInUser.getId().equals(product.getProvider().getId()));

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.product_comments_container, CommentsFragment.newInstance(product.getId(), null))
                    .commit();

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.product_average_review_container, AverageReviewFragment.newInstance(null, product.getId()))
                    .commit();
        });

        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFavorite -> {
            btnFavorite.setText(isFavorite ? "Unfavorite" : "Favorite");
        });

        btnFavorite.setOnClickListener(v -> viewModel.toggleFavorite(productId));
        btnPurchase.setOnClickListener(v -> purchaseProduct(productId));
        btnChat.setOnClickListener(v -> viewModel.initiateChat(product.getProvider().getId()));
        layoutProviderInfo.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("userId", viewModel.getProduct().getValue().getProvider().getId());

            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
            navController.navigate(R.id.action_productDetailsFragment_to_profileFragment, args);
        });

        return view;
    }

    private void purchaseProduct(Long productId) {
        viewModel.fetchEvents();
        viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            ProductPurchaseDialogFragment dialog = new ProductPurchaseDialogFragment(events, eventId -> viewModel.purchaseProduct(productId, eventId));
            dialog.show(getParentFragmentManager(), "ProductPurchaseDialog");
            viewModel.getEvents().removeObservers(getViewLifecycleOwner());
        });
    }
}
