package com.example.locarusdt2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SensorsActivity extends AppCompatActivity {

    SharedPreferences tokens;
    SharedPreferences.Editor editor;
    String userUUID;
    String accessToken;
    String deviceID;
    private ArrayList<Sensors> sensorsArrayList = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    RequestQueue requestQueue;
    ArrayList<SensorCard> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        tokens = getSharedPreferences("Token", MODE_PRIVATE);
        editor = tokens.edit();
        accessToken = tokens.getString("accessToken","");
        Log.d(Constants.TAG, "Запуск окна датчиков");
        arrayList = new ArrayList<>();

        getJSon(JsonRequestCurrentUser.currentUser(),"userUUID");


//        Thread devicesListThread = new Thread(() -> getJSon(JsonRequestDevicesList.devicesList(userUUID),"deviceID"));
//        Thread tailGetThread = new Thread(() -> getJSon(JsonRequestTailGet.tailGet(deviceID),"tail"));
//        Thread arrayListThread = new Thread(() -> {
//            for (Sensors sensors : sensorsArrayList) {
//                arrayList.add(new SensorCard(R.drawable.ic_baseline_speed_24, sensors.getValue(),sensors.getName()));
//            }
//            runOnUiThread(() -> adapter.notifyDataSetChanged());
//        });

        //getJSon1(JsonRequestCurrentUser.currentUser(),"userUUID");
//        getJSon1(JsonRequestDevicesList.devicesList(userUUID),"deviceID");
//        getJSon1(JsonRequestTailGet.tailGet(deviceID),"tail");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter(arrayList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sensor_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                editor.remove("accessToken");
                editor.apply();
                startActivity(new Intent(this,MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed () {
        moveTaskToBack(true);
    }

    private void getJSon(JSONObject postData, String flag) {
        String postUrl = "http://stage.local/api/";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.TAG, "Ответ от сервера " + response.toString() + "\n");
                try {
                    switch (flag) {
                        case "userUUID":
                            userUUID = response.getJSONObject("result").getString("userID");
                            getJSon(JsonRequestDevicesList.devicesList(userUUID),"deviceID");
                            Log.d(Constants.TAG, userUUID);
                            break;
                        case "deviceID":
                            JSONArray array = response.getJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                deviceID = array.getJSONObject(i).getString("deviceID");
                                getJSon(JsonRequestTailGet.tailGet(deviceID),"tail");
                            }
                            Log.d(Constants.TAG, deviceID);
                            break;
                        case "tail":
                            Log.d(Constants.TAG, "Хвосты");
                            JSONArray sensors = null;
                            JSONArray array1 = response.getJSONArray("result");
                            for (int i = 0; i < array1.length(); i++) {
                                JSONObject jsonObject = array1.getJSONObject(i);
                                sensors = jsonObject.getJSONArray("sensors");
                            }
                            for (int i = 0; i < sensors.length(); i++) {
                                String name = sensors.getJSONObject(i).getString("name");
                                String value = sensors.getJSONObject(i).getString("value");
                                String units = sensors.getJSONObject(i).getString("units");
                                String varName = sensors.getJSONObject(i).getString("varName");
                                Integer ooType = sensors.getJSONObject(i).getInt("ooType");
                                Long time = sensors.getJSONObject(i).getLong("time");
                                Sensors sensor = new Sensors(name, value, units, varName, ooType, time);
                                sensorsArrayList.add(sensor);
                            }
                            for (Sensors sensors1 : sensorsArrayList) {
                                arrayList.add(new SensorCard(R.drawable.ic_baseline_speed_24, sensors1.getValue(),sensors1.getName()));
                            }
                            runOnUiThread(() -> adapter.notifyDataSetChanged());
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
            Toast toast = Toast.makeText(getApplicationContext(), error.toString(),Toast.LENGTH_SHORT);
            toast.show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("X-Client-Type", "drivertask");
                headers.put("Authorization", "Bearer "+ accessToken);

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

    private void getJSon1(JSONObject postData, String flag){
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        String postUrl = "http://stage.local/api/";
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        Log.d(Constants.TAG,postData.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, postData, future, future){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("X-Client-Type", "drivertask");
                headers.put("Authorization", "Bearer "+ accessToken);
                return headers;
            }
        };
        future.setRequest(requestQueue.add(request));
        requestQueue.add(request);
        JSONObject response = null;

        try {
            response = future.get(10,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        // do something with response

        Log.d(Constants.TAG, response + "");
        }
    }
