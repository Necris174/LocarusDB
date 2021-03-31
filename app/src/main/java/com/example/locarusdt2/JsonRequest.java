package com.example.locarusdt2;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonRequest extends Application {

    SharedPreferences tokens = getSharedPreferences("Token", MODE_PRIVATE);
    String accessToken = tokens.getString("accessToken","");
    String refreshToken = tokens.getString("refreshToken","");
    Long expires = tokens.getLong("expires",0);

    private void getJSon(JSONObject postData) {
        String postUrl = "http://stage.local/api/";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        Response.Listener listener = response -> {

        };
        Response.ErrorListener errorListener = error -> {

        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData,listener,errorListener);

        requestQueue.add(jsonObjectRequest);

    }
}
