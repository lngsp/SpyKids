package ro.spykids.clientapp.ui.activity.parent;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import ro.spykids.clientapp.R;

public class ViewAreaOnMapActivity extends AppCompatActivity {
    private MapView mapView;
    private GoogleMap googleMap;
    private Circle circle;
    String data, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_area_on_map);

        data = getIntent().getStringExtra("data");
        type = getIntent().getStringExtra("type");

        Log.d("ViewAreaOnMapActivity", "DATA AICI ESTE : " + data + "TYPEUL : " + type);

        mapView = this.findViewById(R.id.mapViewArea);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                if (type.equals("CIRCLE")) {
                    try {
                        if(circle != null){
                            circle.remove();
                        }
                        JSONObject jsonObject = new JSONObject(data);
                        JSONObject center = jsonObject.getJSONObject("center");
                        double latitude = center.getDouble("latitude");
                        double longitude = center.getDouble("longitude");
                        int radius = jsonObject.getInt("radius");

                        Log.d("ViewAreaOnMapActivity: ", "latitude: " + latitude);
                        Log.d("ViewAreaOnMapActivity: ", "longitude: " + longitude);
                        Log.d("ViewAreaOnMapActivity: ", "radius: " + radius);

                        LatLng markerLatLng = new LatLng(latitude, longitude);

                        CircleOptions circleOptions = new CircleOptions()
                                .center(markerLatLng)
                                .radius(radius)
                                .strokeWidth(3f)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 255, 0, 0));

                        circle = googleMap.addCircle(circleOptions);

                        LatLng circleCenter = circle.getCenter();

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(circleCenter, 15);

                        googleMap.moveCamera(cameraUpdate);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type.equals("POLYGON")) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        //get all the keys from json
                        Iterator<String> keys = jsonObject.keys();

                        //points from data
                        ArrayList<LatLng> points = new ArrayList<>();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONObject point = jsonObject.getJSONObject(key);
                            double latitude = point.getDouble("latitude");
                            double longitude = point.getDouble("longitude");
                            LatLng latLng = new LatLng(latitude, longitude);
                            points.add(latLng);
                        }

                        // Verificați dacă există suficiente puncte pentru a forma un poligon
                        if (points.size() >= 3) {
                            PolygonOptions polygonOptions = new PolygonOptions()
                                    .addAll(points)
                                    .strokeWidth(3f)
                                    .strokeColor(Color.BLUE)
                                    .fillColor(Color.argb(50, 255, 0, 0));

                            Polygon polygon = googleMap.addPolygon(polygonOptions);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(polygon.getPoints().get(1), 10);

                            googleMap.moveCamera(cameraUpdate);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}