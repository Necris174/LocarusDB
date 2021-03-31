package com.example.locarusdt2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonRequestDevicesList {

    static JSONObject devicesList (String userID) {

        JSONObject postData = new JSONObject();
        JSONObject user = new JSONObject();
        try {
            postData.put("jsonrpc", "2.0");
            postData.put("method", "devices.list");
            user.put("userID", userID);
            postData.put("params", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(Constants.TAG, postData.toString());
        return postData;
    }
}
