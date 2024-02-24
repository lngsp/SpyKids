package ro.spykids.clientapp.ui.activity.parent;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ro.spykids.clientapp.R;

public class ViewLocOnMapActivity extends AppCompatActivity {
    private MapView mapView;
    private GoogleMap googleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_loc_on_map);

        mapView = findViewById(R.id.mapViewLoc);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                String address = getIntent().getStringExtra("address");
                Double longitude = getIntent().getDoubleExtra("longitude", 0);
                Double latitude = getIntent().getDoubleExtra("latitude", 0);
                System.out.println(">>>>>>>>>>>>>>  ViewLocOnMapActivity" + latitude + " " + longitude);
//                double[] coordinates = getLatLngFromAddress(ViewLocOnMapActivity.this, address);
//
//                if (coordinates != null) {
//                    double latitude = coordinates[0];
//                    double longitude = coordinates[1];

                    LatLng latLng = new LatLng(latitude, longitude);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(address);
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    googleMap.addMarker(markerOptions);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

//                } else {
//                    System.out.println("No results were found for the specified address.");
//                }
            }
        });
    }

    public static double[] getLatLngFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && addresses.size() > 0) {
                Address location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                return new double[]{latitude, longitude};
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}