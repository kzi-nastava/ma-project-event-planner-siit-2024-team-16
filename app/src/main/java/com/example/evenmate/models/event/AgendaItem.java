package com.example.evenmate.models.event;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgendaItem implements Parcelable {
    private Long id;
    private String name;
    private String description;
    private String startTime;
    private String endTime;
    private String location;

    protected AgendaItem(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        description = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        location = in.readString();
    }

    public static final Creator<AgendaItem> CREATOR = new Creator<>() {
        @Override
        public AgendaItem createFromParcel(Parcel in) {
            return new AgendaItem(in);
        }

        @Override
        public AgendaItem[] newArray(int size) {
            return new AgendaItem[size];
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
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeString(location);
    }
}
