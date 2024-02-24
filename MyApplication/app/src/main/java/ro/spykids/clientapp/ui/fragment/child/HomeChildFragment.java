package ro.spykids.clientapp.ui.fragment.child;

import static ro.spykids.clientapp.util.Constants.LOCATION_REQUEST;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.clientapi.BatteryAPI;
import ro.spykids.clientapp.clientapi.LocationAPI;
import ro.spykids.clientapp.util.TokenManager;

public class HomeChildFragment extends Fragment implements OnMapReadyCallback {
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationAPI locationAPI = new LocationAPI();
    private BatteryAPI batteryAPI = new BatteryAPI();
    private Handler handler = new Handler();
    private MapView mapView;
    private GoogleMap googleMap;
    private LatLng latLng;
    private String email, token;
    private int delayMillis = 30000; //every 30 sec
    private Context fragmentContext;

    public HomeChildFragment(){}

    public HomeChildFragment(String email, String token) {
        this.email = email;
        this.token = token;
    }

    //Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_child, container, false);

        mapView = view.findViewById(R.id.mapViewChild);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        //remove the status bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
            token = args.getString("token");
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }

    private void sendCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Context context = getContext() != null ? getContext() : getActivity();

                    if (context == null) {
                        // Nu există context valid, nu puteți continua
                        return;
                    }
                    System.out.println(">>>> Your location is " + location.getLongitude()+ " " + location.getLatitude());
                    locationAPI.sendCurrentLocation(context, email, latLng.latitude, latLng.longitude, token);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .title("Your are here!")
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    googleMap.addMarker(markerOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }else {
                    Toast.makeText(getContext(), "Enable your GPS or check you network connection.", Toast.LENGTH_SHORT).show();
                    locationAPI.sendCurrentLocation(getContext(), email, Double.NaN, Double.NaN, token);
                    System.out.println(">>>>>>>>>>> Enable your GPS or check you network connection. Location is " + Double.NaN);
                }
            }
        });

    }

    private void sendCurrentBattery(){
        BatteryManager batteryManager = (BatteryManager) getActivity().getSystemService(getContext().BATTERY_SERVICE);

        //current level of battery
        int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        int batteryStatus = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
        boolean isCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                batteryStatus == BatteryManager.BATTERY_STATUS_FULL;

//        System.out.println("BATERRY LEVEL IS : " + batteryLevel + " batteryStatus " + batteryStatus + " isCharging " + isCharging);
        batteryAPI.sendCurrentPercentBattery(getContext(), email, batteryLevel, token);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;


        handler.postDelayed(new Runnable() {
            public void run() {
                if (fragmentContext != null) {
                    sendCurrentLocation();

                    if (isAdded() && !isDetached() && !isRemoving()) {
                        sendCurrentBattery();
                    }
                    handler.postDelayed(this, delayMillis); // Repeat every 30 sec
                }
            }
        }, 10);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentContext = null;
    }

}
