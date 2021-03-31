package com.example.locarusdt2;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonRequestCurrentUser {

    static JSONObject currentUser () {

        JSONObject postData = new JSONObject();
        JSONObject user = new JSONObject();
        try {
            postData.put("jsonrpc", "2.0");
            postData.put("id", "12345");
            postData.put("method", "users.current");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }
}
