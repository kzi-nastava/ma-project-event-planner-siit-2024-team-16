package com.example.evenmate.models.user;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.evenmate.models.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Parcelable {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Address address;
    private String phone;
    private Company company;
    private String photo;
    private String role;

    @Nullable
    private Double averageReview; // To avoid casting issues in subclasses

    public User(User user) {
        this.id = user.id;
        this.email = user.email;
        this.password = user.password;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.address = user.address;
        this.phone = user.phone;
        this.company = user.company;
        this.photo = user.photo;
        this.role = user.role;
    }

    protected User(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        email = in.readString();
        password = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        phone = in.readString();
        company = in.readParcelable(Company.class.getClassLoader());
        photo = in.readString();
        role = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
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
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeParcelable(address, flags);
        dest.writeString(phone);
        dest.writeParcelable(company, flags);
        dest.writeString(photo);
        dest.writeString(role);
    }
}