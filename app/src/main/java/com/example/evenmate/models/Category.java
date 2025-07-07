package com.example.evenmate.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.example.evenmate.models.event.EventType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Category implements Parcelable {
    private Long id;
    private String name;
    private String description;
    private List<EventType> types;

    public Category(Long id, String name, String description, List<EventType> types) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.types = types;
    }

    public Category(Long id, String name, String description) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.types = new ArrayList<>();
    }

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    // Parcelable implementation
    protected Category(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        description = in.readString();
        types = in.createTypedArrayList(EventType.CREATOR);
    }

    public static final Creator<Category> CREATOR = new Creator<>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(name);
        dest.writeString(description);
        dest.writeTypedList(types);
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(name, category.name) && Objects.equals(description, category.description) && Objects.equals(types, category.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, types);
    }
}