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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.pojo.Battery;

public class BatteryAPI {
    public interface BatteryCallback {
        void onSuccess(List<Battery> list);
        void onError(String message);
    }

    public void sendCurrentPercentBattery(Context context, String childEmail, Integer percent, String token) {

        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "batteries/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Battery successfully added!")) {
                            //Toast.makeText(context, "Battery added!", Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("BatteryAPI", "Error in sendCurrentPercentBattery.");
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
                Log.e("BatteryAPI", "Error in sendCurrentPercentBattery: " + error.getMessage());
                errorSound.release();
            }
        }) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("childEmail", childEmail);
                    jsonObject.put("percent", percent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("BatteryAPI", "JSONException in sendCurrentPercentBattery: " + e.getMessage());

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

    public void getLastBattery(Context context, String email, String token, BatteryCallback callback){
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "batteries/?parentEmail=" + email;

        List<Battery> batteryList = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            // loop through each JSON object in the response array
                            for (int i = 0; i < response.length(); i++) {
                                // get the JSON object at the current index
                                JSONObject batteryResponse = response.getJSONObject(i);


                                // extract the values you need from the JSON object
                                String childEmail = batteryResponse.getString("childEmail");
                                Integer percent = batteryResponse.getInt("percent");
                                String timeStr = batteryResponse.getString("time");
                                String message = batteryResponse.getString("message");

                                //System.out.println(">>>> " + childEmail + " " + percent + " time " + timeStr + message);

                                LocalDateTime time = LocalDateTime.parse(timeStr);

                                Battery battery = new Battery();

                                battery.setPercent(percent);
                                battery.setTime(time);
                                battery.setChildEmail(childEmail);
                                battery.setMessage(message);

                                batteryList.add(battery);
                            }
                            callback.onSuccess(batteryList);

                        } catch (JSONException e) {
                            errorSound.start();
                            e.printStackTrace();
                            Log.e("BatteryAPI", "JSONException in getLastBattery: " + e.getMessage());
                            errorSound.release();
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
                        Log.e("BatteryAPI", "Error in getLastBattery: " + error.getMessage());
                        errorSound.release();
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
}
