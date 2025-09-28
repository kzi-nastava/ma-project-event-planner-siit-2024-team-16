package com.example.evenmate.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapUtils {

    public static GeoPoint getGeoPointFromAddress(Context context, String fullAddress) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(fullAddress, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new GeoPoint(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String buildFullAddress(com.example.evenmate.models.Address address) {
        return address.getStreetName() + " " + address.getStreetNumber() + ", " + address.getCity() + ", " + address.getCountry();
    }
}
