package com.example.evenmate.models.event;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.example.evenmate.models.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventType implements Parcelable {
    private Long id;
    private String name;
    private String description;
    private List<Category> recommendedCategories;
    private boolean isActive;

    public EventType() {}

    public EventType(Long id, String name, String description,
                     List<Category> recommendedCategories, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.recommendedCategories = recommendedCategories;
        this.isActive = isActive;
    }

    protected EventType(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        description = in.readString();

        // Read List<Category>
        recommendedCategories = new ArrayList<>();
        in.readList(recommendedCategories, Category.class.getClassLoader());

        isActive = in.readByte() != 0;
    }

    public static final Creator<EventType> CREATOR = new Creator<>() {
        @Override
        public EventType createFromParcel(Parcel in) {
            return new EventType(in);
        }

        @Override
        public EventType[] newArray(int size) {
            return new EventType[size];
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

        // Write List<Category>
        dest.writeList(recommendedCategories);

        dest.writeByte((byte) (isActive ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventType eventType = (EventType) o;
        return isActive == eventType.isActive &&
                Objects.equals(id, eventType.id) &&
                Objects.equals(name, eventType.name) &&
                Objects.equals(description, eventType.description) &&
                Objects.equals(recommendedCategories, eventType.recommendedCategories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, recommendedCategories, isActive);
    }

    public EventTypeRequest toRequest() {
        return new EventTypeRequest(
                id,
                name,
                description,
                recommendedCategories != null ?
                        recommendedCategories.stream()
                                .filter(Objects::nonNull)
                                .map(Category::getId)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()) : null,
                isActive
        );
    }

    @Override
    public String toString() {
        return "EventType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", recommendedCategories=" + recommendedCategories +
                ", isActive=" + isActive +
                '}';
    }
}