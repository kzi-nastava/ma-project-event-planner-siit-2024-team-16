package com.example.evenmate.models.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.evenmate.models.Address;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCompanyRequest implements Parcelable {
    private Long id;
    private String name;
    private Address address;
    private String phone;
    private String description;
    private List<String> photos;

    public UpdateCompanyRequest() {
        address = new Address();
    }

    protected UpdateCompanyRequest(Parcel in) {
        id = in.readLong();
        name = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        phone = in.readString();
        description = in.readString();
        photos = in.createStringArrayList();
    }

    public static final Creator<com.example.evenmate.models.user.UpdateCompanyRequest> CREATOR = new Creator<>() {
        @Override
        public com.example.evenmate.models.user.UpdateCompanyRequest createFromParcel(Parcel in) {
            return new com.example.evenmate.models.user.UpdateCompanyRequest(in);
        }

        @Override
        public com.example.evenmate.models.user.UpdateCompanyRequest[] newArray(int size) {
            return new com.example.evenmate.models.user.UpdateCompanyRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeParcelable(address, flags);
        dest.writeString(phone);
        dest.writeString(description);
        dest.writeStringList(photos);
    }
}

