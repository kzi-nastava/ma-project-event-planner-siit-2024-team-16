package com.example.evenmate.models.user;

import android.os.Parcel;
import android.os.Parcelable;
import com.example.evenmate.models.Address;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Company implements Parcelable {
    private String email;
    private String name;
    private Address address;
    private String phone;
    private String description;
    private List<String> photos;

    public Company() {
        address = new Address();
    }

    protected Company(Parcel in) {
        email = in.readString();
        name = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        phone = in.readString();
        description = in.readString();
        photos = in.createStringArrayList();
    }

    public static final Creator<Company> CREATOR = new Creator<>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(name);
        dest.writeParcelable(address, flags);
        dest.writeString(phone);
        dest.writeString(description);
        dest.writeStringList(photos);
    }
}