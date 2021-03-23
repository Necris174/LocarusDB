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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout textInputLogin;
    private TextInputLayout textInputPassword;
    private Button signUp;
    private RequestQueue requestQueue;
    private String login;
    private String password;
    String accessToken;
    String expires;
    String refreshToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputLogin = findViewById(R.id.textInputLogin);
        textInputPassword = findViewById(R.id.textInputPassword);
        signUp = findViewById(R.id.loginSignUpButton);


    }

    private boolean validateLogin() {
        login = textInputLogin.getEditText().getText().toString();
        if (login.isEmpty()) {
            textInputLogin.setError("Введите Логин");
            return false;
        } else {
            textInputLogin.setError("");
            return true;
        }
    }

    private boolean validatePassword() {
        password = textInputPassword.getEditText().getText().toString();
        if (password.isEmpty()) {
            textInputPassword.setError("Введите Пароль");
            return false;
        } else {
            textInputPassword.setError("");
            return true;
        }
    }

    public void loginSignUpUser(View view) {
        if (validateLogin() | validatePassword()) {
            Log.d(Constants.TAG, "Запрос");
            getJSon(JsonRequestSignIn.signIn(login, password));
        } else {
            return;
        }


    }

    private void getJSon(JSONObject postData) {
        String postUrl = "http://stage.local/api/";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.TAG, "Ответ от сервера " + response + "\n");

                try {
                    accessToken = response.getJSONObject("result").getString("accessToken");
                    expires = response.getJSONObject("result").getString("expires");
                    refreshToken = response.getJSONObject("result").getString("refreshToken");
                    Log.d(Constants.TAG, accessToken);
                } catch (JSONException e) {
                    Log.d(Constants.TAG, e + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(Constants.TAG, "Ответ от сервера " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("X-Client-Type", "drivertask");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);

    }

}