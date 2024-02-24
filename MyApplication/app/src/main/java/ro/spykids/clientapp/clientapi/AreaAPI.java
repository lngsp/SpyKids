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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.pojo.Area;
import ro.spykids.clientapp.pojo.AreaType;
import ro.spykids.clientapp.pojo.Point;

public class AreaAPI {
    public interface AreaListCallback {
        void onSuccess(List<Area> areaList);
        void onError(String message);
    }

    public void defineAreaPolygon(Context context, String parentEmail, String token, String areaName, List<Point> points, Boolean safeType){
        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "areas/";

        final MediaPlayer successSound = MediaPlayer.create(context, R.raw.button_pressed);
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Area successfully added!")) {
                            successSound.start();
                            Toast.makeText(context, "Area added!", Toast.LENGTH_LONG).show();
                        } else {
                            errorSound.start();
                            Log.e("RestrictedAreaAPI", "Error in defineArea.");
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
                Log.e("RestrictedAreaAPI", "Error in defineArea: " + error.getMessage());
            }
        }) {
            @Override
            public byte[] getBody() {
                JSONObject area = new JSONObject();
                try {
                    JSONObject data = new JSONObject();

                    // iterate through the list of points
                    for (int i = 0; i < points.size(); i++) {
                        // create a new JSONObject for each point
                        JSONObject point = new JSONObject();
                        point.put("latitude", points.get(i).getX());
                        point.put("longitude", points.get(i).getY());

                        // add the point object to the data object with the appropriate name
                        data.put("point" + (i+1), point);
                    }


                    area.put("name", areaName);
                    area.put("areaType", "POLYGON");
                    area.put("enable", true);
                    area.put("definedBy", parentEmail);
                    area.put("safe", safeType);
                    area.put("data", data.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("RestrictedAreaAPI", "JSONException in defineArea: " + e.getMessage());

                }
                return area.toString().getBytes();
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

    public void defineAreaCircle(Context context, String parentEmail, String token, String areaName, List<Point> points,
                                 Float radius, Boolean safeType){
        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "areas/";

        final MediaPlayer successSound = MediaPlayer.create(context, R.raw.button_pressed);
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Area successfully added!")) {
                            successSound.start();
                            Toast.makeText(context, "Area added!", Toast.LENGTH_LONG).show();
                        } else {
                            errorSound.start();
                            Log.e("RestrictedAreaAPI", "Error in defineArea.");
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
                Log.e("RestrictedAreaAPI", "Error in defineArea: " + error.getMessage());

            }
        }) {
            @Override
            public byte[] getBody() {
                JSONObject area = new JSONObject();
                try {
                    JSONObject data = new JSONObject();

                    // iterate through the list of points
                    for (int i = 0; i < points.size(); i++) {
                        // create a new JSONObject for each point
                        JSONObject point = new JSONObject();
                        point.put("latitude", points.get(i).getX());
                        point.put("longitude", points.get(i).getY());

                        // add the point object to the data object with the appropriate name
                        data.put("center", point);
                    }

                    data.put("radius", radius);

                    area.put("name", areaName);
                    area.put("areaType", "CIRCLE");
                    area.put("enable", true);
                    area.put("definedBy", parentEmail);
                    area.put("safe", safeType);
                    area.put("data", data.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("RestrictedAreaAPI", "JSONException in defineArea: " + e.getMessage());

                }
                return area.toString().getBytes();
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


    public void addChild(Context context, String parentEmail, String token, String childEmail, String areaName){
        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "areas/children";

        final MediaPlayer successSound = MediaPlayer.create(context, R.raw.button_pressed);
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Child successfully added!")) {
                            successSound.start();
                            Toast.makeText(context, "Child added to area!", Toast.LENGTH_LONG).show();
                        } else {
                            errorSound.start();
                            Log.e("RestrictedAreaAPI", "Error in addChild.");
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
                Log.e("RestrictedAreaAPI", "Error in addChild: " + error.getMessage());

            }
        }) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("parentEmail", parentEmail);
                    jsonObject.put("childEmail", childEmail);
                    jsonObject.put("areaName", areaName);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("RestrictedAreaAPI", "JSONException in addChild: " + e.getMessage());

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

    public void getParentAreas(Context context, String token, String parentEmail, AreaListCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "areas/parent?parentEmail=" + parentEmail;

        final MediaPlayer successSound = MediaPlayer.create(context, R.raw.button_pressed);
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        List<Area> areaList = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // loop through each JSON object in the response array
                            for (int i = 0; i < response.length(); i++) {
                                // get the JSON object at the current index
                                JSONObject areaResponse = response.getJSONObject(i);


                                // extract the values you need from the JSON object
                                String name = areaResponse.getString("name");
                                String data = areaResponse.getString("data");
                                AreaType areaType = AreaType.valueOf(areaResponse.getString("areaType"));
                                Boolean enable = areaResponse.getBoolean("enable");
                                Boolean safe = areaResponse.getBoolean("safe");

                                Area area = new Area();

                                area.setName(name);
                                area.setData(data);
                                area.setAreaType(areaType);
                                area.setEnable(enable);
                                area.setSafe(safe);

                                areaList.add(area);
                            }
                            callback.onSuccess(areaList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("AreaAPI", "JSONException in getChildAreas: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorSound.start();
                if(error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("AreaAPI", "Error in getChildAreas: " + errorMessage);
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

    public void modifyAreaSafety(Context context, String parentEmail, String token, String areaName, Boolean safe){
        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "areas/safe?definedBy=" + parentEmail + "&name=" + areaName +
                "&safe=" + safe.toString();

        final MediaPlayer successSound = MediaPlayer.create(context, R.raw.button_pressed);
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Area safety has been updated successfully!")) {
                            successSound.start();
//                            Toast.makeText(context, "Area safety has been updated successfully!!", Toast.LENGTH_LONG).show();
                        } else {
                            errorSound.start();
                            Log.e("AreaAPI", "Error in modifyAreaSafety.");
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
                Log.e("AreaAPI", "Error in modifyAreaSafety: " + error.getMessage());

            }
        }) {
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

    public void modifyAreaEnable(Context context, String parentEmail, String token, String areaName, Boolean enable){
        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "areas/enable?definedBy=" + parentEmail + "&name=" + areaName +
                "&enable=" + enable.toString();

        final MediaPlayer successSound = MediaPlayer.create(context, R.raw.button_pressed);
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Area has been updated successfully, is now disabled!") ||
                            response.equals("Area has been updated successfully, is now enabled!"))
                        {
                            successSound.start();
                            // Toast.makeText(context, "Area enable has been updated successfully!!", Toast.LENGTH_LONG).show();
                        } else {
                            errorSound.start();
                            Log.e("AreaAPI", "Error in modifyAreaEnable.");
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
                Log.e("AreaAPI", "Error in modifyAreaEnable: " + error.getMessage());

            }
        }) {
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
}

