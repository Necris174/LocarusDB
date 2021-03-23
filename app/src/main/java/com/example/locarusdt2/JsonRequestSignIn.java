package com.example.locarusdt2;

import android.app.Application;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public  class JsonRequestSignIn {

    static JSONObject signIn (String login, String password) {

        JSONObject postData = new JSONObject();
        JSONObject user = new JSONObject();
        try {
            postData.put("jsonrpc", "2.0");
            postData.put("method", "signin");
            user.put("login", login);
            user.put("password", password);
            postData.put("params", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

}
