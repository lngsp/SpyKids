package ro.spykids.clientapp.clientapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ro.spykids.clientapp.R;
import ro.spykids.clientapp.pojo.User;
import ro.spykids.clientapp.ui.activity.common.BondActivity;
import ro.spykids.clientapp.ui.activity.child.ChildMainActivity;
import ro.spykids.clientapp.ui.activity.parent.ParentMainActivity;
import ro.spykids.clientapp.util.TokenManager;

public class AuthAPI {
    private UserAPI userAPI = new UserAPI();



    public void login(Context context, String email, String password){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        final MediaPlayer successSound = MediaPlayer.create(context, R.raw.success);
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.error);

        RequestQueue queue = Volley.newRequestQueue(context);
        //The URL Posting TO:
        String url = context.getResources().getString(R.string.base_url) + "auth/authenticate";

        // make the json Object with email & password for the AuthRequest
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        // Set Request Object:
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("LoginActivity", "Response: " + response.toString());

                        try {
                            // Get token value
                            String token = response.getString("token");

                            JWT jwt = new JWT(token);

                            String userEmail = jwt.getClaim("sub").asString(); // get the subject of JWT
                            Date expiresAt = jwt.getExpiresAt();    // expiration date

                            if (expiresAt != null && expiresAt.before(new Date()))
                            {
                                errorSound.start();
                                Log.e("JWT", "Token has expired");
                                Toast.makeText(context, "Your session has expired. Please log in again to continue using the app.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            else
                            {
                                Log.i("JWT", "Token is valid!");

//                                Toast.makeText(context, "Login succesfully", Toast.LENGTH_LONG).show();
                                TokenManager.getInstance(context).saveToken(token);


                                // Create the user with the email & token
                                userAPI.getUserByEmail(context, email, token, new UserAPI.UserResponseListener() {
                                    @Override
                                    public void onSuccess(User user) {
                                        // Check if it is parent/child and display the Activity
                                        successSound.start();
                                        if(user.getType().matches("CHILD"))
                                        {
                                            Intent intent = new Intent(context, ChildMainActivity.class);
                                            intent.putExtra("email", user.getEmail());
                                            intent.putExtra("token", token);
                                            context.startActivity(intent);
                                        }else {
                                            Intent intent = new Intent(context, ParentMainActivity.class);
                                            intent.putExtra("email", user.getEmail());
                                            intent.putExtra("token", token);
                                            context.startActivity(intent);
                                        }
                                        successSound.release();
                                    }

                                    @Override
                                    public void onError(String message) {
                                        errorSound.start();
                                        System.out.println("An error : " + message);
                                        errorSound.release();
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            errorSound.start();
                            e.printStackTrace();
                            Log.e("LoginActivity", "Response: " + e.getMessage());
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();

                            errorSound.release();
                        }
                        // End of Try-Catch Block
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
                Log.e("AuthAPI", "Error in login: " + error.getMessage());
                errorSound.release();
            }
        }
        ){
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }

    //////REGISTER
    public void register(Context context, String email, String password, String firstName,
                       String lastName, String phone, Integer age, String type){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getResources().getString(R.string.base_url) + "auth/register";

        final MediaPlayer successSound = MediaPlayer.create(context, R.raw.button_pressed);
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.button_pressed);

        JSONObject userJson = new JSONObject();
        try {
            userJson.put("email", email);
            userJson.put("password", password);
            userJson.put("firstName", firstName);
            userJson.put("lastName", lastName);
            userJson.put("age", age);
            userJson.put("phone", phone);
            userJson.put("role", "USER");
            userJson.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                userJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("token");
//                    Log.d("RegisterActivity", "Token: " + token);

                    JWT jwt = new JWT(token);

                    String userEmail = jwt.getClaim("sub").asString(); // get the subject of JWT
                    Date expiresAt = jwt.getExpiresAt();    // expiration date

                    if (expiresAt != null && expiresAt.before(new Date()))
                    {
                        errorSound.start();
                        Log.e("JWT", "Token has expired");
                        Toast.makeText(context, "Your session has expired. Please log in again to continue using the app.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else {
                        successSound.start();
                        Log.i("JWT", "Token is valid!");

                        TokenManager.getInstance(context).saveToken(token);

                        User user = new User(firstName, lastName,email, password, phone, age, type, token);

                        if(type == "PARENT"){

                            Intent intent = new Intent(context, BondActivity.class);
                            intent.putExtra("User", (Serializable) user);
                            intent.putExtra("token", token);
                            context.startActivity(intent);
                        }
                        if(type == "CHILD"){
                            Intent intent = new Intent(context, BondActivity.class);
                            intent.putExtra("User", (Serializable) user);
                            intent.putExtra("token", token);
                            context.startActivity(intent);
                        }
                        successSound.release();

                    }



                } catch (JSONException e) {
                    errorSound.start();
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
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
                Log.e("AuthAPI", "Error in register: " + error.getMessage());
                errorSound.release();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }

    ////LOGOUT
    public void logout(Context context, String email) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getResources().getString(R.string.base_url) + "auth/logout";

        final MediaPlayer successSound = MediaPlayer.create(context, R.raw.button_pressed);
        final MediaPlayer errorSound = MediaPlayer.create(context, R.raw.button_pressed);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Logout successfully")) {
                            TokenManager.getInstance(context).clearToken();
                            successSound.start();
                        } else {
                            errorSound.start();
                            Log.e("AuthAPI", "Error in logout.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorSound.start();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
                        }
                        error.printStackTrace();
                        Log.e("AuthAPI", "Error in logout: " + error.getMessage());
                        errorSound.release();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // add email to request param
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
