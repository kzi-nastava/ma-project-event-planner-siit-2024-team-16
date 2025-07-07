package com.example.evenmate.models.user;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.example.evenmate.models.Address;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User implements Parcelable {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Address address;
    private String phone;
    private Company company;
    private String photo;
    private String role;

    public User() {
        this.address = new Address();
    }

    public User(String email, String password, String firstName, Address address, String lastName, String phone, Company company, String photo, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.address = address;
        this.lastName = lastName;
        this.phone = phone;
        this.company = company;
        this.photo = photo;
        this.role = role;
    }

    protected User(Parcel in) {
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