package com.example.locarusdt2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class SensorList extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<Sensors> allSensors;
    private ArrayAdapter<Sensors> arrayAdapter;
    private ArrayList<Sensors> activeSensors;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);
        listView = findViewById(R.id.listView);
        sharedPreferences = getSharedPreferences("Token", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        allSensors = SharedPreferencesReceive.getSensors(sharedPreferences.getString("arraySensors", ""));
        Log.d(Constants.TAG1,"Настройки датчиков (все): " + allSensors.toString());

        if (sharedPreferences.contains("active")) {
            activeSensors = SharedPreferencesReceive.getActiveSensors(sharedPreferences.getString("active",""));
            Log.d(Constants.TAG1,"НАстройки датчиков (активные) " + activeSensors);
        } else {
            activeSensors = allSensors;
        }

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_checked, allSensors);
        listView.setAdapter(arrayAdapter);


        for (int i = 0; i < activeSensors.size(); i++) {
            listView.setItemChecked(i, activeSensors.get(i).isActive());
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(Constants.TAG, "onItemClick: " + position);

            }
        });


    }

    @Override
    protected void onPause() {
        Log.d(Constants.TAG1, "Уничтожаем окно и сохраняем настройки");
        super.onPause();
        JSONArray jsonArray = new JSONArray();
        SparseBooleanArray sp = listView.getCheckedItemPositions();
        if (sp != null) {
            for (int i = 0; i < sp.size(); i++) {
                Sensors sensors = (Sensors) listView.getItemAtPosition(i);
                activeSensors.add(sensors);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", sensors.getName());
                    jsonObject.put("active", sp.valueAt(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }
        }
        editor.putString("active", jsonArray.toString());
        editor.apply();
    }
}