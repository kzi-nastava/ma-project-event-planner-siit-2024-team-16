package com.example.evenmate.fragments.map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.evenmate.R;
import com.example.evenmate.utils.MapUtils;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapFragment extends Fragment {

    private String fullAddress;
    private String markerTitle = "Location";
    private MapView mapView;

    public MapFragment() {}

    public static MapFragment newInstance(String fullAddress, String markerTitle) {
        MapFragment fragment = new MapFragment();
        fragment.fullAddress = fullAddress;
        fragment.markerTitle = markerTitle;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        GeoPoint location = MapUtils.getGeoPointFromAddress(ctx, fullAddress);
        if (location != null) {
            IMapController mapController = mapView.getController();
            mapController.setZoom(15);
            mapController.setCenter(location);

            Marker marker = new Marker(mapView);
            marker.setPosition(location);
            marker.setTitle(markerTitle);
            mapView.getOverlays().add(marker);
        }else {
            android.widget.Toast.makeText(ctx, "Address not found", android.widget.Toast.LENGTH_SHORT).show();
            mapView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
