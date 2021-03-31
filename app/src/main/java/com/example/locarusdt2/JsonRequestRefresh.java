package com.example.locarusdt2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonRequestRefresh {


    static JSONObject refresh (String refreshToken) {

        JSONObject postData = new JSONObject();
        JSONObject user = new JSONObject();
        try {
            postData.put("jsonrpc", "2.0");
            postData.put("method", "refresh");
            user.put("refreshToken", refreshToken);
            postData.put("params", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }
}
