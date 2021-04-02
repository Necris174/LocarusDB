package com.example.locarusdt2;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonRequestTailGet {
    static JSONObject tailGet (String deviceID) {

        JSONObject postData = new JSONObject();
        JSONObject user = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            postData.put("jsonrpc", "2.0");
            postData.put("method", "tail.get");
            postData.put("id", "12345");
            user.put("deviceID", deviceID);
            user.put("lastTime",System.currentTimeMillis());
            postData.put("params", jsonArray);
            jsonArray.put(user);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(Constants.TAG, postData.toString());
        return postData;
    }
}
