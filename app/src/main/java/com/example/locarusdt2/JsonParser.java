package com.example.locarusdt2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

    public void getUser (String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        String accessToken = jsonObject.getString("accessToken");
        int expires = jsonObject.getInt("expires");
        String refreshToken = jsonObject.getString("refreshToken");
        Log.d(Constants.TAG, "accessToken: " + accessToken +"\n"+ "expires: " + expires +"\n"+ "refreshToken: " + refreshToken );
    }
}
