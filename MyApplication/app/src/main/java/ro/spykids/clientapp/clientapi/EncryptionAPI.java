package ro.spykids.clientapp.clientapi;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import ro.spykids.clientapp.R;

public class EncryptionAPI {
    public void decrypt(Context context, String message, final DecryptCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getResources().getString(R.string.base_url) + "encryption/decrypt?decrypt=" + message;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Apelarea metodei de callback pentru a transmite rezultatul criptării
                        callback.onDecryptSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Apelarea metodei de callback pentru a trata eroarea
                        callback.onDecryptError(error);
                    }
                }) {
        };
        queue.add(stringRequest);
    }

    // Definirea unei interfețe pentru callback
    public interface DecryptCallback {
        void onDecryptSuccess(String decryptedText);
        void onDecryptError(VolleyError error);
    }

}
