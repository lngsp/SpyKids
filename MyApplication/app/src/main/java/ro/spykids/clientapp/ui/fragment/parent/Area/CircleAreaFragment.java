package ro.spykids.clientapp.ui.fragment.parent.Area;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.clientapi.AreaAPI;
import ro.spykids.clientapp.pojo.Point;

public class CircleAreaFragment extends Fragment  implements GoogleMap.OnMapClickListener, OnMapReadyCallback{
    //XML
    private MapView mapView;
    private GoogleMap gMap;
    private Button btnDefine;
    private EditText etxtAreaName;
    private CheckBox checkBoxSafeArea;
    private SeekBar seekBar;

    //VARIABLES
    private Marker previousMarker;
    private Circle circle;

    private String email, token;
    private List<Point> points = new ArrayList<>();
    private AreaAPI areaAPI = new AreaAPI();
    private boolean safe = true;
    private float radius = 1000;



    public CircleAreaFragment(){}

    //Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_area_circle, container, false);

        //remove the status bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
            token = args.getString("token");
        }

        mapView = view.findViewById(R.id.mapViewCircle);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        seekBar = view.findViewById(R.id.seekBar);

        etxtAreaName = view.findViewById(R.id.etxtCircleAreaName);
        checkBoxSafeArea = view.findViewById(R.id.checkBoxSafeCircleArea);
        btnDefine = view.findViewById(R.id.btnDefineCircleArea);

        checkBoxSafeArea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    safe = true;
                else safe = false;
            }
        });


        btnDefine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areaAPI.defineAreaCircle(getContext(), email, token, etxtAreaName.getText().toString(),
                        points, radius, safe);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Actualizează raza cercului în funcție de valoarea selectată de utilizator
                radius = progress; // Poți adăuga și o scalare corespunzătoare, dacă este necesar
                circle.setRadius(radius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Acțiuni înainte de începerea tragerii seek bar-ului
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Acțiuni după terminarea tragerii seek bar-ului
            }
        });

        return view;
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if(previousMarker!=null){
            previousMarker.remove();
            points.clear();
        }
        // Add marker to map
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
        previousMarker = gMap.addMarker(markerOptions);

        // Add point to list
        Point p = new Point(latLng.latitude, latLng.longitude);
        points.add(p);

        if(circle != null) {
            circle.remove();
        }

        //Adaugarea unui cerc la locul de pe harta unde utilizatorul a facut click
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius) //schimba acest numar cu raza ta dorita
                .strokeWidth(3f)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(50, 255, 0, 0));
        circle = gMap.addCircle(circleOptions);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        // Set map click listener
        gMap.setOnMapClickListener(this);
    }


}
