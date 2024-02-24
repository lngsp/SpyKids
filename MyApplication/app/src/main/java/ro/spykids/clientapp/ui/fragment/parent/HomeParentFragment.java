package ro.spykids.clientapp.ui.fragment.parent;

import static ro.spykids.clientapp.util.Constants.NOTIFICATION_REQUEST;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.LocalDateTime;
import java.util.List;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.clientapi.BatteryAPI;
import ro.spykids.clientapp.clientapi.LocationAPI;
import ro.spykids.clientapp.pojo.Battery;
import ro.spykids.clientapp.pojo.Location;

public class HomeParentFragment extends Fragment implements OnMapReadyCallback {
    //Variables
    private String email, token;
    private String previousMessage = null;
    private MapView mapView;
    private GoogleMap gMap;
    private Handler handler = new Handler();
    private int delayMillis = 30000; //every 30 sec
    private LocationAPI locationAPI = new LocationAPI();
    private BatteryAPI batteryAPI = new BatteryAPI();
    private Context fragmentContext;

    //Constructor
    public HomeParentFragment(){}

    public HomeParentFragment(String email, String token){
        this.email = email;
        this.token = token;
    }

    //Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_parent, container, false);

        //remove the status bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
            token = args.getString("token");
        }

        mapView = view.findViewById(R.id.mapViewParent);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        checkPermissionNotification();

        getBattery();

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        handler.postDelayed(new Runnable(){
            public void run(){
                if (fragmentContext != null) {
                    //get last location
                    locationAPI.getLastLocation(getContext(), email, token, new LocationAPI.LocationListCallback() {

                        @Override
                        public void onSuccess(List<Location> locationList) {
                            //no error

                            if (locationList == null) {
                                Toast.makeText(getActivity(), "No location found yet!", Toast.LENGTH_SHORT).show();
                            }

                            for (Location l : locationList) {
                                System.out.println(">>>>> Location is :" + l.getLongitude() + " " + l.getLongitude());
                                if (Double.isNaN(l.getLatitude()) || Double.isNaN(l.getLongitude())) {
                                    sendNotification(l.getEmail(), "The GPS is closed or the internet is not working!");
                                    Toast.makeText(getActivity(), "The GPS is closed or the internet is not working!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                LatLng location = new LatLng(l.getLatitude(), l.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                //put the title = message from server
                                markerOptions.title(l.getMessage());

                                //check the message type
                                if (l.getMessage().contains("undefined")) {
                                    //if contains undefined it means that the message from server is „The user childEmail is in an undefined area!”
                                    //make the marker orange and send notification
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                    sendNotification(l.getEmail(), l.getMessage());
                                } else if (l.getMessage().contains("restricted")) {
                                    //if contains restricted it means that the message from server
                                    //is „The user childEmail is in a restricted area with the name areaName!”
                                    //make the marker red and send notification
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    sendNotification(l.getEmail(), l.getMessage());
                                } else if (l.getMessage().contains("safe")) {
                                    //if contains safe it means that the message from server
                                    //is „The user childEmail is in a safe area with the name areaName!”
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                }

                                markerOptions.position(location);
                                googleMap.addMarker(markerOptions);
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                            }
                        }

                        @Override
                        public void onError(String message) {
                            // Handle error here
                            Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });

                    handler.postDelayed(this, delayMillis); // Repeat every 30 sec
                }
            }
        }, 10);
    }

    //get battery from the server
    private void getBattery(){
        batteryAPI.getLastBattery(getContext(), email, token, new BatteryAPI.BatteryCallback() {
            @Override
            public void onSuccess(List<Battery> list) {
                for(Battery b: list){
                    String mess = b.getMessage();
                    String childEmail = "Child email: " + b.getChildEmail();
                    Integer percent = b.getPercent();
                    LocalDateTime time = b.getTime();

                    if(mess.equals("The battery percentage is low!")){
                        String newMess = "The battery percent (" + percent + "%) is low. Time: " + time + ".";
                        sendNotification(childEmail, newMess);
                    }
                    else if(mess.equals("The battery is almost discharged!")){
                        String newMess = "The battery percent (" + percent + "%) is almost discharged. Time: " + time + ".";
                        sendNotification(childEmail, newMess);
                    }
                    else if(mess.equals("The battery percentage is low. The phone will close soon!")){
                        String newMess = "The battery percent (" + percent + "%) is low. The phone will close soon! Time: " + time + ".";
                        sendNotification(childEmail, newMess);
                    }
                    else {
                        Toast.makeText(getActivity(), "The battery percentage is very good!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //NOTIFICATION
    private void createNotificationChannel(){
        checkPermissionNotification();

        String CHANNEL_ID = getString(R.string.default_notification_channel_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title, String message) {
        checkPermissionNotification();

        if (previousMessage != null && previousMessage.equals(message)) {
            // The message is identical to the previous message, so do not send notification
            return;
        }

        createNotificationChannel();
        String CHANNEL_ID = getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        notificationManager.notify(200, builder.build());

    }

    private void checkPermissionNotification(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, NOTIFICATION_REQUEST);
            return;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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