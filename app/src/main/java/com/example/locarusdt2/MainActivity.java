package com.example.locarusdt2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.VoiceInteractor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout textInputLogin;
    private TextInputLayout textInputPassword;
    private Button signUp;
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputLogin = findViewById(R.id.textInputLogin);
        textInputPassword = findViewById(R.id.textInputPassword);
        signUp = findViewById(R.id.loginSignUpButton);

    }
    private  boolean validateLogin() {
        String login = textInputLogin.getEditText().getText().toString();
        if (login.isEmpty()) {
            textInputLogin.setError("Введите Логин");
            return false;
        } else {
            textInputLogin.setError("");
            return true;
        }
    }
    private  boolean validatePassword() {
        String login = textInputPassword.getEditText().getText().toString();
        if (login.isEmpty()) {
            textInputPassword.setError("Введите Пароль");
            return false;
        } else {
            textInputPassword.setError("");
            return true;
        }
    }

    public void loginSignUpUser(View view) {
        if (validateLogin() | validatePassword()) {
            return;
        }
        getJSon();
        Log.d("MyLogTag", "Ответ от сервера ");
    }

    private void getJSon (){
        String postUrl = "http://stage.local/api/";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        JSONObject login = new JSONObject();
        try {
            postData.put("jsonrpc", "2.0");
            postData.put("method", "signin");
            login.put("login", "admin");
            login.put("password", "123456");
            postData.put("params", login);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("MyLogTag", "Ответ от сервера " + e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("MyLogTag", "Ответ от сервера " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MyLogTag", "Ответ от сервера " + error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);

    }
}