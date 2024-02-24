package ro.spykids.clientapp.clientapi;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.pojo.User;

public class UserAPI {

    public interface UserResponseListener {
        //used to receive the response from the server when requesting information about a specific user
        //one user
        void onSuccess(User user);
        void onError(String message);
    }

    public interface UserListCallback {
        //used to receive the response from the server when requesting the list of children associated with a parent user.
        //many users
        void onSuccess(List<User> userList);
        void onError(String message);
    }

    public void getUserByEmail(Context context, String email, String token, UserResponseListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getResources().getString(R.string.base_url) + "users/?email=" + email;

        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String firstName = response.getString("firstName");
                            String lastName = response.getString("lastName");
                            String password = response.getString("password");
                            String phone = response.getString("phone");
                            int age = response.getInt("age");
                            String type = response.getString("type");

                            User user = new User(firstName, lastName, email, password, phone, age, type, token);
                            listener.onSuccess(user);

                        }
                        catch (JSONException e) {
                            errorSound.start();
                            e.printStackTrace();
                            listener.onError("Error parsing user info.");
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
                Log.e("UserAPI", "Error in getUserByEmail: " + error.getMessage());
                errorSound.release();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                String authHeader = "Bearer " + token;

                params.put("Authorization", authHeader);

                return params;
            }
        };

        queue.add(jsonObjectRequest);
    }


    public void addChild(Context context, String emailChild, String parentEmail, String token) {
        final MediaPlayer successSound = MediaPlayer.create(context, R.raw.success);
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getResources().getString(R.string.base_url) + "users/children?parentEmail=" + parentEmail
                + "&childEmail=" + emailChild;

        //String Request Object:
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equalsIgnoreCase("Child successfully added!")){
                    successSound.start();
                    Toast.makeText(context, "Child add successfully", Toast.LENGTH_LONG).show();
                }else {
                    errorSound.start();
                    Log.e("UserAPI", "Error in addChild.");
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
                Log.e("UserAPI", "Error in addChild: " + error.getMessage());
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String authHeader = "Bearer " + token;
                headers.put("Authorization", authHeader);
                return headers;
            }
        };
        //End of String Request Object:
        requestQueue.add(stringRequest);
    }

    public void getParentChildrens(Context context, String email, String token, UserListCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getResources().getString(R.string.base_url) + "user/children?parentEmail=" + email;

       
        List<User> kidsList = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    // loop through each JSON object in the response array
                    for (int i = 0; i < response.length(); i++) {
                        // get the JSON object at the current index
                        JSONObject kidsResponse = response.getJSONObject(i);


                        // extract the values you need from the JSON object
                        String firstName = kidsResponse.getString("firstName");
                        String lastName = kidsResponse.getString("lastName");
                        String email = kidsResponse.getString("email");
                        String phone = kidsResponse.getString("phone");
                        String age = kidsResponse.getString("age");
                        String type = kidsResponse.getString("type");
                        String role = kidsResponse.getString("role");

                        User child = new User();

                        child.setEmail(email);
                        child.setFirstName(firstName);
                        child.setLastName(lastName);
                        child.setPhone(phone);
                        child.setAge(Integer.parseInt(age));
                        child.setType(type);

                        kidsList.add(child);
                    }
                    callback.onSuccess(kidsList);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("UserAPI", "JSONException in getParentKids: " + e.getMessage());

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();
                Log.e("UserAPI", "Error in getParentKids: " + error.getMessage());

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


