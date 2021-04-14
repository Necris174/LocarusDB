package com.example.locarusdt2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SensorsActivity extends AppCompatActivity {

    private SharedPreferences tokens;
    private SharedPreferences.Editor editor;
    private String userUUID;
    private String accessToken;
    private String deviceID;
    private Long expires;
    private String refreshToken;
    private ArrayList<Sensors> sensorsArrayList;
    private ArrayList<String> activeSensors;
    private Thread thread;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<SensorCard> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        tokens = getSharedPreferences("Token", MODE_PRIVATE);
        editor = tokens.edit();
        Log.d(Constants.TAG1, "Запуск окна датчиков");
        if (tokens.contains("active")) {
            activeSensors = SharedPreferencesReceive.getActiveSensorsSt(tokens.getString("active", ""));
            Log.d(Constants.TAG1, "Окно датчиков (активные) " + activeSensors.toString());
        }

        accessToken = tokens.getString("accessToken", "");

        arrayList = new ArrayList<>();
        sensorsArrayList = new ArrayList<>();

        // Проверяем savedInstanceState.
        if (savedInstanceState == null || !savedInstanceState.containsKey("array")) {
            Log.d(Constants.TAG, "Первый запуск окна");
            getJSon(JsonRequestCurrentUser.currentUser(), "userUUID");
        } else {
            arrayList = savedInstanceState.getParcelableArrayList("array");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter(arrayList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

         thread = new Thread(() -> {
            Log.d(Constants.TAG, "Запукаем поток получения хвостов");
            while (true) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Date date = new java.util.Date(tokens.getLong("expires",0)*1000L);
                SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+5"));
                String formattedDate = sdf.format(date);
                Log.d(Constants.TAG, "expires протух" + formattedDate);
                if (tokens.getLong("expires", 0) - 30 < System.currentTimeMillis() / 1000L) {
                    getJSon(JsonRequestRefresh.refresh(tokens.getString("refreshToken", "")), "refresh");
                }
                getJSon(JsonRequestTailGet.tailGet(deviceID), "tail");
            }
        });
            thread.start();
    }

    @Override
    protected void onRestart() {
        arrayList.clear();
        super.onRestart();
        if (tokens.contains("active")) {
            activeSensors = SharedPreferencesReceive.getActiveSensorsSt(tokens.getString("active", ""));
            Log.d(Constants.TAG1, "Окно датчиков (активные) " + activeSensors.toString());
        }
        sensorsArrayList = SharedPreferencesReceive.getSensors(tokens.getString("arraySensors", ""));
        for (Sensors sensors1 : sensorsArrayList) {
            if (activeSensors!=null&&activeSensors.contains(sensors1.getName())){
                arrayList.add(new SensorCard(R.drawable.ic_baseline_speed_24, sensors1.getValue(), sensors1.getName()));
            }
        }
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("array", arrayList);
    }

    // добавляем раздел Меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sensor_menu, menu);
        return true;
    }

    // Меню настроек.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                editor.remove("accessToken");
                editor.remove("active");
                editor.apply();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            case R.id.settings:
              startActivity(new Intent(this,SensorList.class));
              return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();
    }

    // Действие при нажатии на кнопку Back в Android.
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // Метод отвечающий за отправку и обработку Json запросов.
    private void getJSon(JSONObject postData, String flag) {
        String postUrl = Constants.SERVER;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    switch (flag) {
                        case "userUUID":
                            Log.d(Constants.TAG, "Ответ на userUUID " + response.toString() + "\n");
                            userUUID = response.getJSONObject("result").getString("userID");
                            getJSon(JsonRequestDevicesList.devicesList(userUUID), "deviceID");
                            break;
                        case "deviceID":
                            Log.d(Constants.TAG, "Ответ на deviceID " + response.toString() + "\n");
                            JSONArray array = response.getJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                deviceID = array.getJSONObject(i).getString("deviceID");
                            }
                            getJSon(JsonRequestTailGet.tailGet(deviceID), "tail");
                            break;
                        case "tail":
                            Log.d(Constants.TAG, "Ответ на Хвосты " + response.toString() + "\n");
                            JSONArray sensors = null;
                            JSONArray array1 = response.getJSONArray("result");

                            for (int i = 0; i < array1.length(); i++) {
                                JSONObject jsonObject = array1.getJSONObject(i);
                                sensors = jsonObject.getJSONArray("sensors");
                            }
                            if (sensors!=null) {
                                editor.putString("arraySensors", sensors.toString());
                                editor.apply();
                                if (sensors.length() != 0) {
                                    arrayList.clear();
                                    sensorsArrayList.clear();
                                    for (int i = 0; i < sensors.length(); i++) {
                                        String name = sensors.getJSONObject(i).getString("name");
                                        String value = sensors.getJSONObject(i).getString("value");
                                        String units = sensors.getJSONObject(i).getString("units");
                                        String varName = sensors.getJSONObject(i).getString("varName");
                                        Integer ooType = sensors.getJSONObject(i).getInt("ooType");
                                        Long time = sensors.getJSONObject(i).getLong("time");
                                        Sensors sensor = new Sensors(name, value, units, varName, ooType, time);

                                        if (activeSensors==null) {
                                            sensorsArrayList.add(sensor);
                                        }else if (activeSensors.contains(name)){
                                            sensorsArrayList.add(sensor);
                                        }
                                    }
                                    Log.d(Constants.TAG1, "Карточки для отображения: " + sensorsArrayList.toString());
                                    for (Sensors sensors1 : sensorsArrayList) {
                                        arrayList.add(new SensorCard(R.drawable.ic_baseline_speed_24, sensors1.getValue(), sensors1.getName()));
                                    }

                                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                                }
                            }
                            break;
                        case "refresh":
                            Log.d(Constants.TAG, "Ответ на refresh " + response.toString() + "\n");
                            accessToken = response.getJSONObject("result").getString("accessToken");
                            expires = response.getJSONObject("result").getLong("expires");
                            refreshToken = response.getJSONObject("result").getString("refreshToken");
                            editor.putString("accessToken", accessToken);
                            editor.putLong("expires", expires);
                            editor.putString("refreshToken", refreshToken);
                            editor.apply();
                            break;
                    }
                } catch (JSONException e) {
                    Log.d(Constants.TAG, e + "");
                    Toast toast = Toast.makeText(SensorsActivity.this.getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
                    toast.show();
                }


            }
        }, error -> {
            Log.d(Constants.TAG, "Ответ от сервера " + error);
            Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("X-Client-Type", "drivertask");
                headers.put("Authorization", "Bearer " + accessToken);

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}
