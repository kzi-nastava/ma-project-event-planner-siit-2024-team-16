package com.example.evenmate.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Address implements Parcelable {
    private String city;
    private String streetName;
    private String streetNumber;
    private String country;

    public Address() {}

    public Address(String streetName, String streetNumber, String city, String country) {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.city = city;
        this.country = country;
    }

    protected Address(Parcel in) {
        city = in.readString();
        streetName = in.readString();
        streetNumber = in.readString();
        country = in.readString();
    }

    public static final Creator<Address> CREATOR = new Creator<>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(city);
        dest.writeString(streetName);
        dest.writeString(streetNumber);
        dest.writeString(country);
    }
}
