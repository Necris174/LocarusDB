package com.example.locarusdt2;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SharedPreferencesReceive extends Application {

     public static ArrayList<Sensors> getSensors(String js){
         ArrayList<Sensors> allSensors = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(js);
        for (int i = 0; i < jsonArray.length(); i++) {
            String name = null;
            String value = null;

            value = jsonArray.getJSONObject(i).getString("value");
            name = jsonArray.getJSONObject(i).getString("name");

            allSensors.add(new Sensors(name,value));
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allSensors;
        }

    public static ArrayList<Sensors> getActiveSensors(String js){
        ArrayList<Sensors> allSensors = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(js);
            for (int i = 0; i < jsonArray.length(); i++) {
                String name = null;
                Boolean active = null;
                name = jsonArray.getJSONObject(i).getString("name");
                active = jsonArray.getJSONObject(i).getBoolean("active");
                allSensors.add(new Sensors(name,active));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allSensors;
    }
    public static ArrayList<String> getActiveSensorsSt(String js){
        ArrayList<String> allSensors = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(js);
            for (int i = 0; i < jsonArray.length(); i++) {
                String name = null;
                Boolean active = null;
                name = jsonArray.getJSONObject(i).getString("name");
                active = jsonArray.getJSONObject(i).getBoolean("active");
                if (active){
                    allSensors.add(name);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allSensors;
    }

    }

