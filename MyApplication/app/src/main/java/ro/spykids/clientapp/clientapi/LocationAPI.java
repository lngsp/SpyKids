package ro.spykids.clientapp.clientapi;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.pojo.Location;

public class LocationAPI {
    public interface LocationListCallback {
        void onSuccess(List<Location> locationList);
        void onError(String message);
    }

    public void sendCurrentLocation(Context context, String email,
                                    double latitudine, double longitude, String token) {

        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "locations/";

        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Location successfully added!")) {
//                            Toast.makeText(context, "Location added!", Toast.LENGTH_LONG).show();
                        } else {
                            errorSound.start();
                            Log.e("LocationAPI", "Error in sendCurrentLocation.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorSound.start();
                if(error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();
                Log.e("LocationAPI", "Error in sendCurrentLocation: " + error.getMessage());

            }
        }) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", email);
                    jsonObject.put("latitude", latitudine);
                    jsonObject.put("longitude", longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("LocationAPI", "JSONException in sendCurrentLocation: " + e.getMessage());

                }
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

        };

        queue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    // ultima locatie a copilului
    public void getLastLocation(Context context, String email, String token, LocationListCallback callback) {

        List<Location> locationList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "locations/last/?parentEmail=" + email ;

        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    // loop through each JSON object in the response array
                    for (int i = 0; i < response.length(); i++) {
                        // get the JSON object at the current index
                        JSONObject locationResponse = response.getJSONObject(i);


                        // extract the values you need from the JSON object
                        String childEmail = locationResponse.getString("childEmail");
                        double latitude = locationResponse.getDouble("latitude");
                        double longitude = locationResponse.getDouble("longitude");
                        String arrivalTimeStr = locationResponse.getString("arrivalTime");
                        String departureTimeStr = locationResponse.optString("departureTime"); // use optString() to handle null values
                        String messages = locationResponse.getString("messages");

                        LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr);
                        LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr);

                        Location location = new Location();

                        location.setEmail(childEmail);
                        location.setLatitude(latitude);
                        location.setLongitude(longitude);
                        location.setArrivalTime(arrivalTime);
                        location.setDepartureTime(departureTime);
                        location.setMessage(messages);

                        locationList.add(location);
                    }
                    callback.onSuccess(locationList);

                } catch (JSONException e) {
                    errorSound.start();
                    e.printStackTrace();
                    Log.e("LocationAPI", "JSONException in getLastLocation: " + e.getMessage());

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorSound.start();
                if(error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();
                Log.e("LocationAPI", "Error in getLastLocation: " + error.getMessage());

            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // add the request to the Volley request queue
        queue.add(jsonArrayRequest);

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    // istoricul tuturor locatiilor
    public void getHistoryLocations(Context context, String email, String token, LocationListCallback callback) {

        List<Location> locationList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "locations/?parentEmail=" + email;

        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // loop through each JSON object in the response array
                            for (int i = 0; i < response.length(); i++) {
                                // get the JSON object at the current index
                                JSONObject locationResponse = response.getJSONObject(i);


                                // extract the values you need from the JSON object
                                String childEmail = locationResponse.getString("childEmail");
                                double latitude = locationResponse.getDouble("latitude");
                                double longitude = locationResponse.getDouble("longitude");
                                String messages = locationResponse.getString("messages");
                                String arrivalTimeStr = locationResponse.getString("arrivalTime");
                                String departureTimeStr = locationResponse.optString("departureTime"); // use optString() to handle null values

                                LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr);
                                LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr);

                                Location location = new Location();

                                location.setEmail(childEmail);
                                location.setLatitude(latitude);
                                location.setLongitude(longitude);
                                location.setArrivalTime(arrivalTime);
                                location.setDepartureTime(departureTime);
                                location.setMessage(messages);

                                locationList.add(location);
                            }
                            callback.onSuccess(locationList);

                        } catch (JSONException e) {
                            errorSound.start();
                            e.printStackTrace();
                            Log.e("LocationAPI", "JSONException in getHistoryLocations: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorSound.start();
                if(error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("LocationAPI", "Error in getHistoryLocations: " + errorMessage);
                } else {
                    Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // add the request to the Volley request queue
        queue.add(jsonArrayRequest);

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    // get all locations from a date
    public void getLocationsFromDate(Context context, String email, String token, Date date, LocationListCallback callback) {
        List<Location> locationList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "locations/date/?parentEmail=" + email + "&date=" + date;

        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // loop through each JSON object in the response array
                            for (int i = 0; i < response.length(); i++) {
                                // get the JSON object at the current index
                                JSONObject locationResponse = response.getJSONObject(i);


                                // extract the values you need from the JSON object
                                String childEmail = locationResponse.getString("childEmail");
                                double latitude = locationResponse.getDouble("latitude");
                                double longitude = locationResponse.getDouble("longitude");
                                String messages = locationResponse.getString("messages");
                                String arrivalTimeStr = locationResponse.getString("arrivalTime");
                                String departureTimeStr = locationResponse.optString("departureTime"); // use optString() to handle null values

                                LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr);
                                LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr);

                                Location location = new Location();

                                location.setEmail(childEmail);
                                location.setLatitude(latitude);
                                location.setLongitude(longitude);
                                location.setArrivalTime(arrivalTime);
                                location.setDepartureTime(departureTime);
                                location.setMessage(messages);

                                locationList.add(location);
                            }
                            callback.onSuccess(locationList);

                        } catch (JSONException e) {
                            errorSound.start();
                            e.printStackTrace();
                            Log.e("LocationAPI", "JSONException in getLocationsFromDate: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorSound.start();
                        if(error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                            Log.e("LocationAPI", "Error in getHistoryLocations: " + errorMessage);

                        } else {
                            Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
                        }
                        error.printStackTrace();

                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    // get all locations since ... and now
    public void getLocationsSinceDate(Context context, String email, String token, Date date, LocationListCallback callback) {
        List<Location> locationList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "locations/since/?parentEmail=" + email + "&date=" + date;

        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // loop through each JSON object in the response array
                            for (int i = 0; i < response.length(); i++) {
                                // get the JSON object at the current index
                                JSONObject locationResponse = response.getJSONObject(i);


                                // extract the values you need from the JSON object
                                String childEmail = locationResponse.getString("childEmail");
                                double latitude = locationResponse.getDouble("latitude");
                                double longitude = locationResponse.getDouble("longitude");
                                String messages = locationResponse.getString("messages");
                                String arrivalTimeStr = locationResponse.getString("arrivalTime");
                                String departureTimeStr = locationResponse.optString("departureTime"); // use optString() to handle null values

                                LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr);
                                LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr);

                                Location location = new Location();

                                location.setEmail(childEmail);
                                location.setLatitude(latitude);
                                location.setLongitude(longitude);
                                location.setArrivalTime(arrivalTime);
                                location.setDepartureTime(departureTime);
                                location.setMessage(messages);

                                locationList.add(location);
                            }
                            callback.onSuccess(locationList);

                        } catch (JSONException e) {
                            errorSound.start();
                            e.printStackTrace();
                            Log.e("LocationAPI", "JSONException in getLocationsSinceAndNow: " + e.getMessage());

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorSound.start();
                        if(error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                            Log.e("LocationAPI", "Error in getHistoryLocations: " + errorMessage);

                        } else {
                            Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
                        }
                        error.printStackTrace();

                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
}
