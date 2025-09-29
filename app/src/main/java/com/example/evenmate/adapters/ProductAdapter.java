package com.example.evenmate.adapters;

import android.app.Activity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.asset.Product;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductAdapter extends ArrayAdapter<Product> {
    private List<Product> products;
    @Setter
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Product product);
    }

    @Setter
    private OnEditClickListener onEditClickListener;

    public interface OnEditClickListener {
        void onEditClick(Product product);
    }

    @Setter
    private OnCardClickListener onCardClickListener;
    public interface OnCardClickListener {
        void onCardClick(Product product);
    }

    public ProductAdapter(Activity context, List<Product> products){
        super(context, R.layout.item_card_general, products);
        this.products = products;
    }
    @Override
    public int getCount() {
        return products.size();
    }

    @Nullable
    @Override
    public Product getItem(int position) {
        return products.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View itemView, @NonNull ViewGroup parent) {
        Product product = getItem(position);
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_card_general,
                    parent, false);
            itemView.findViewById(R.id.card).setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.light_green));
            itemView.findViewById(R.id.favorite).setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.purple));
            itemView.findViewById(R.id.title_frame).setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.green));
        }
        TextView title = itemView.findViewById(R.id.title);
        TextView name = itemView.findViewById(R.id.box1);
        TextView description = itemView.findViewById(R.id.box2);
        TextView category = itemView.findViewById(R.id.box3);
        TextView price = itemView.findViewById(R.id.box4);
        TextView priceAfterDiscount = itemView.findViewById(R.id.box5);
        ImageView imageView = itemView.findViewById(R.id.image);
        ImageButton btnEdit = itemView.findViewById(R.id.btnEditEvent);
        MaterialButton btnFavorite = itemView.findViewById(R.id.favorite);
        ImageButton btnDelete = itemView.findViewById(R.id.btnDeleteEvent);

        if(product != null) {
            title.setText(product.getName());
            description.setText(String.format("%s: %s", getContext().getString(R.string.description), product.getDescription()));
            name.setText(String.format("%s: %s", getContext().getString(R.string.name), product.getName()));
            category.setText(String.format("%s%s", getContext().getString(R.string.category), product.getCategory() != null ? product.getCategory().getName() : null));
            price.setText(String.format("%s: %s", getContext().getString(R.string.price), product.getPrice()));
            priceAfterDiscount.setText(String.format("%s: %s", getContext().getString(R.string.priceAfterDiscount), product.getPriceAfterDiscount()));

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                String imageStr = product.getImages().get(0);
                if (imageStr.contains("http")) {
                    Glide.with(getContext()).load(imageStr).placeholder(R.drawable.no_img).into(imageView);
                } else {
                    try {
                        if (imageStr.contains(",")) {
                            imageStr = imageStr.substring(imageStr.indexOf(",") + 1);
                        }
                        byte[] decoded = Base64.decode(imageStr, Base64.DEFAULT);
                        Glide.with(getContext()).asBitmap().load(decoded).into(imageView);
                    } catch (IllegalArgumentException e) {
                        imageView.setImageResource(R.drawable.no_img);
                    }
                }
            } else {
                imageView.setImageResource(R.drawable.no_img);
            }

            btnEdit.setOnClickListener(v -> {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(product);
                }
            });
            btnDelete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(product);
                }
            });
            itemView.findViewById(R.id.card).setOnClickListener(v -> {
                if (onCardClickListener != null) {
                    onCardClickListener.onCardClick(product);
                }
            });
            User loggedInUser = AuthManager.loggedInUser;
            boolean isLoggedIn = loggedInUser!=null;
            boolean isProvider = isLoggedIn && loggedInUser.getRole().equals("ProductServiceProvider");
            btnEdit.setVisibility(isProvider ? View.VISIBLE : View.GONE);
            btnDelete.setVisibility(isLoggedIn ? (isProvider ? View.VISIBLE : View.GONE) : View.GONE);
            btnFavorite.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
            if(isLoggedIn) {
                btnFavorite.setOnClickListener(v -> this.changeFavoriteStatus(product.getId(), btnFavorite));
                checkFavoriteStatus(product.getId(), btnFavorite);
            }
        }
        return itemView;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    private void checkFavoriteStatus(Long productId, MaterialButton btnFavorite) {
        retrofit2.Call<Boolean> call = ClientUtils.productService.isFavorite(productId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFavorite = response.body();

                    btnFavorite.setBackgroundResource(
                            isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite
                    );
                    btnFavorite.setSelected(isFavorite);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                ToastUtils.showCustomToast(getContext(), R.string.network_error + t.getMessage(), true);
            }
        });
    }

    private void changeFavoriteStatus(Long productId, MaterialButton btnFavorite) {
        retrofit2.Call<Void> call = ClientUtils.productService.toggleFavorite(productId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                boolean isFavorite = !btnFavorite.isSelected();

                btnFavorite.setBackgroundResource(
                        isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite
                );
                btnFavorite.setSelected(isFavorite);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                ToastUtils.showCustomToast(getContext(),R.string.network_error + t.getMessage(), true);
            }
        });
    }
}
