package com.example.evenmate.models.asset;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest implements Parcelable {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer discount;
    private List<String> images;
    private Long categoryId;
    private String newCategoryName;
    private String newCategoryDescription;
    private Boolean isAvailable;
    private Boolean isVisible;

    protected ProductRequest(Parcel in) {
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
        images = in.createStringArrayList();
        if (in.readByte() == 0) {
            categoryId = null;
        } else {
            categoryId = in.readLong();
        }
        newCategoryName = in.readString();
        newCategoryDescription = in.readString();
        byte tmpIsAvailable = in.readByte();
        isAvailable = tmpIsAvailable == 0 ? null : tmpIsAvailable == 1;
        byte tmpIsVisible = in.readByte();
        isVisible = tmpIsVisible == 0 ? null : tmpIsVisible == 1;
    }

    public static final Creator<ProductRequest> CREATOR = new Creator<>() {
        @Override
        public ProductRequest createFromParcel(Parcel in) {
            return new ProductRequest(in);
        }

        @Override
        public ProductRequest[] newArray(int size) {
            return new ProductRequest[size];
        }
    };

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
        parcel.writeStringList(images);
        if (categoryId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(categoryId);
        }
        parcel.writeString(newCategoryName);
        parcel.writeString(newCategoryDescription);
        parcel.writeByte((byte) (isAvailable == null ? 0 : isAvailable ? 1 : 2));
        parcel.writeByte((byte) (isVisible == null ? 0 : isVisible ? 1 : 2));
    }
}