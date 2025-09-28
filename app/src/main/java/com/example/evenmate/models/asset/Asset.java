package com.example.evenmate.models.asset;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.user.User;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Asset implements Parcelable {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer discount;
    private Double priceAfterDiscount;
    private User provider;
    private List<String> images;
    private Category category;
    private Double averageReview;
    private AssetType type;
    private Boolean isVisible;
    private Boolean isAvailable;
    private Boolean isVisibleToUser;

    protected Asset(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        description = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        if (in.readByte() == 0) {
            discount = null;
        } else {
            discount = in.readInt();
        }
        if (in.readByte() == 0) {
            priceAfterDiscount = null;
        } else {
            priceAfterDiscount = in.readDouble();
        }
        provider = in.readParcelable(User.class.getClassLoader());
        images = in.createStringArrayList();
        category = in.readParcelable(Category.class.getClassLoader());
        if (in.readByte() == 0) {
            averageReview = null;
        } else {
            averageReview = in.readDouble();
        }
        type = AssetType.valueOf(in.readString());
        byte tmpIsVisible = in.readByte();
        isVisible = tmpIsVisible == 0 ? null : tmpIsVisible == 1;
        byte tmpIsAvailable = in.readByte();
        isAvailable = tmpIsAvailable == 0 ? null : tmpIsAvailable == 1;
        byte tmpIsVisibleToUser = in.readByte();
        isVisibleToUser = tmpIsVisibleToUser == 0 ? null : tmpIsVisibleToUser == 1;
    }

    public static final Creator<Asset> CREATOR = new Creator<>() {
        @Override
        public Asset createFromParcel(Parcel in) {
            return new Asset(in);
        }

        @Override
        public Asset[] newArray(int size) {
            return new Asset[size];
        }
    };

    public ProductRequest toRequest(){
        return new ProductRequest(
                id,
                name,
                description,
                price,
                discount,
                images,
                category.getId(),
                null,
                null,
                isVisible,
                isAvailable
            );
        }
        public Product toProduct(){
            return new Product(
                id,
                name,
                description,
                price,
                discount,
                priceAfterDiscount,
                provider,
                images,
                category,
                averageReview,
                type,
                isVisible,
                isAvailable,
                isVisibleToUser
            );
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(name);
        parcel.writeString(description);
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(price);
        }
        if (discount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(discount);
        }
        if (priceAfterDiscount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(priceAfterDiscount);
        }
        parcel.writeParcelable(provider, i);
        parcel.writeStringList(images);
        parcel.writeParcelable(category, i);
        if (averageReview == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(averageReview);
        }
        parcel.writeString(type.name());
        parcel.writeByte((byte) (isVisible == null ? 0 : isVisible ? 1 : 2));
        parcel.writeByte((byte) (isAvailable == null ? 0 : isAvailable ? 1 : 2));
        parcel.writeByte((byte) (isVisibleToUser == null ? 0 : isVisibleToUser ? 1 : 2));
    }
}