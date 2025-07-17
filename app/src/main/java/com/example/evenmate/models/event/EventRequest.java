package com.example.evenmate.models.event;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.evenmate.models.Address;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRequest implements Parcelable {
    private Long id;
    private String name;
    private String description;
    private Integer maxAttendees;
    private Boolean isPrivate;
    private Address address;
    private Long typeId;
    private LocalDate date;
    private Long organizerId;
    private String photo;

    public EventRequest() {
    }

    public EventRequest(Long id, String name, String description, Boolean isPrivate, String photo, Integer maxAttendees, Address address, Long typeId, LocalDate date, Long organizerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
        this.photo = photo;
        this.maxAttendees = maxAttendees;
        this.address = address;
        this.typeId = typeId;
        this.date = date;
        this.organizerId = organizerId;
    }

    protected EventRequest(Parcel in) {
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

        if (in.readByte() == 0) {
            typeId = null;
        } else {
            typeId = in.readLong();
        }

        String dateString = in.readString();
        date = dateString != null ? LocalDate.parse(dateString) : null;

        if (in.readByte() == 0) {
            organizerId = null;
        } else {
            organizerId = in.readLong();
        }

        photo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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

        if (isPrivate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) (isPrivate ? 1 : 2));
        }

        dest.writeParcelable(address, flags);

        if (typeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(typeId);
        }

        dest.writeString(date != null ? date.toString() : null);

        if (organizerId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(organizerId);
        }

        dest.writeString(photo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventRequest> CREATOR = new Creator<EventRequest>() {
        @Override
        public EventRequest createFromParcel(Parcel in) {
            return new EventRequest(in);
        }

        @Override
        public EventRequest[] newArray(int size) {
            return new EventRequest[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "EventRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", maxAttendees=" + maxAttendees +
                ", isPrivate=" + isPrivate +
                ", address=" + address +
                ", typeId=" + typeId +
                ", date=" + date +
                ", organizerId=" + organizerId +
                ", photo='" + photo + '\'' +
                '}';
    }
}
