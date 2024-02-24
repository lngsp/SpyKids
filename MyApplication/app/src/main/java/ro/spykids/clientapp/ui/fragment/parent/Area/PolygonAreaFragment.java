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

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.clientapi.AreaAPI;
import ro.spykids.clientapp.pojo.Point;

public class PolygonAreaFragment extends Fragment implements GoogleMap.OnMapClickListener, OnMapReadyCallback {
    private String email, token;
    private MapView mapView;
    private GoogleMap gMap;
    private List<Point> points = new ArrayList<>();
    private AreaAPI areaAPI = new AreaAPI();
    private Button btnDefine;
    private EditText etxtAreaName;
    private CheckBox checkBoxSafeArea;
    private boolean safe = true;

    public PolygonAreaFragment(){}

    //Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_area_polygon, container, false);

        //remove the status bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
            token = args.getString("token");
        }

        mapView = view.findViewById(R.id.mapViewPolygon);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        etxtAreaName = view.findViewById(R.id.etxtAreaName);
        checkBoxSafeArea = view.findViewById(R.id.checkBoxSafeArea);
        btnDefine = view.findViewById(R.id.btnDefineArea);

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
                //draw a line between first point and last point
                if (points.size() >= 2) {
                    Point firstPoint = points.get(0);
                    Point lastPoint = points.get(points.size() - 1);

                    LatLng firstLatLng = new LatLng(firstPoint.getX(), firstPoint.getY());
                    LatLng lastLatLng = new LatLng(lastPoint.getX(), lastPoint.getY());

                    // create the line
                    PolylineOptions closingLineOptions = new PolylineOptions()
                            .add(lastLatLng, firstLatLng)
                            .color(Color.BLUE)
                            .width(5);
                    gMap.addPolyline(closingLineOptions);
                }

                areaAPI.defineAreaPolygon(getContext(), email, token, etxtAreaName.getText().toString(),
                        points, safe);
            }
        });


        return view;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Add marker to map
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
        gMap.addMarker(markerOptions);
        // Add point to list
        Point p = new Point(latLng.latitude, latLng.longitude);
        points.add(p);

        if (points.size() >= 2) {
            Point lastPoint = points.get(points.size() - 2);
            LatLng lastLatLng = new LatLng(lastPoint.getX(), lastPoint.getY());

            //create polyline
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(lastLatLng, latLng)
                    .color(Color.BLUE)
                    .width(5);
            gMap.addPolyline(polylineOptions);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // Set map click listener
        gMap.setOnMapClickListener(this);

    }


}
