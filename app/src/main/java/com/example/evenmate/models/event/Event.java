package com.example.evenmate.models.event;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.evenmate.models.Address;
import com.example.evenmate.models.user.User;

import java.time.LocalDate;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Event implements Parcelable {
    private Long id;
    private String name;
    private String description;
    private Integer maxAttendees;
    private Boolean isPrivate;
    private Address address;
    private EventType type;
    private LocalDate date;
    private User organizer;
    private String photo;
    private Double rating;
    private Boolean isFavorite;
    private ArrayList<String> invited;
    public Event(){}

    protected Event(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }

        name = in.readString();
        description = in.readString();

        if (in.readByte() == 0) {
            maxAttendees = null;
        } else {
            maxAttendees = in.readInt();
        }

        byte tmpIsPrivate = in.readByte();
        isPrivate = tmpIsPrivate == 0 ? null : tmpIsPrivate == 1;

        address = in.readParcelable(Address.class.getClassLoader());

        type = in.readParcelable(EventType.class.getClassLoader());

        String dateString = in.readString();
        if (dateString != null) {
            date = LocalDate.parse(dateString);
        }

        organizer = in.readParcelable(User.class.getClassLoader());

        photo = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
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

        if (maxAttendees == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(maxAttendees);
        }

        dest.writeByte((byte) (isPrivate == null ? 0 : isPrivate ? 1 : 2));

        dest.writeParcelable(address, flags);

        dest.writeParcelable(type, flags);

        dest.writeString(date != null ? date.toString() : null);

        dest.writeParcelable(organizer, flags);

        dest.writeString(photo);
    }
    public EventRequest toRequest() {
        return new EventRequest(
                id,
                name,
                description,
                isPrivate,
                photo,
                maxAttendees,
                address,
                type != null ? type.getId() : null,
                date,
                organizer != null ? organizer.getId() : null
        );
    }

}