package com.example.evenmate.models.user;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.evenmate.models.Address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest implements Parcelable {
    private Long id;
    private String newPassword;
    private String oldPassword;
    private String firstName;
    private String lastName;
    private Address address;
    private String phone;
    private UpdateCompanyRequest company;
    private String photo;

    public UpdateUserRequest(com.example.evenmate.models.user.UpdateUserRequest user) {
        this.id = user.id;
        this.newPassword = user.newPassword;
        this.oldPassword = user.oldPassword;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.address = user.address;
        this.phone = user.phone;
        this.company = user.company;
        this.photo = user.photo;
    }

    public UpdateUserRequest() {
        this.address = new Address();
    }

    public UpdateUserRequest(Long id, String newPassword, String oldPassword, String firstName, Address address, String lastName, String phone, UpdateCompanyRequest company, String photo) {
        this.id = id;
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
        this.firstName = firstName;
        this.address = address;
        this.lastName = lastName;
        this.phone = phone;
        this.company = company;
        this.photo = photo;
    }

    protected UpdateUserRequest(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        newPassword = in.readString();
        oldPassword = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        phone = in.readString();
        company = in.readParcelable(Company.class.getClassLoader());
        photo = in.readString();
    }

    public static final Creator<com.example.evenmate.models.user.UpdateUserRequest> CREATOR = new Creator<>() {
        @Override
        public com.example.evenmate.models.user.UpdateUserRequest createFromParcel(Parcel in) {
            return new com.example.evenmate.models.user.UpdateUserRequest(in);
        }

        @Override
        public com.example.evenmate.models.user.UpdateUserRequest[] newArray(int size) {
            return new com.example.evenmate.models.user.UpdateUserRequest[size];
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
        dest.writeString(newPassword);
        dest.writeString(oldPassword);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeParcelable(address, flags);
        dest.writeString(phone);
        dest.writeParcelable(company, flags);
        dest.writeString(photo);
    }
}
