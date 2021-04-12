package com.example.locarusdt2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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
    private ProgressBar progressBar;
    private RequestQueue requestQueue;
    private String login;
    private String password;
    private String accessToken;
    private Long expires;
    private String refreshToken;

    private boolean isLoginAvailable;
    Constants constants = new Constants();
    SharedPreferences tokens;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textInputLogin = findViewById(R.id.textInputLogin);
        textInputPassword = findViewById(R.id.textInputPassword);
        signUp = findViewById(R.id.loginSignUpButton);
        progressBar = findViewById(R.id.progressBar);

        tokens = getSharedPreferences("Token", MODE_PRIVATE);
        editor = tokens.edit();
        // Проверка accessToken

        if (tokens.contains("accessToken")){
            if (tokens.getLong("expires",0)<System.currentTimeMillis() / 1000L){
                Log.d(Constants.TAG,"");
                getJSon(JsonRequestRefresh.refresh(tokens.getString("refreshToken","")));
            } else {
                Intent intent = new Intent(this,SensorsActivity.class);
                startActivity(intent);
                finish();
            }
        }   else {
            Log.d(Constants.TAG, "Запускаем окно авторизации");
        }
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
            signUp.setEnabled(false);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            getJSon(JsonRequestSignIn.signIn(login, password));

        } else {
            return;
        }
    }

    private void getJSon(JSONObject postData) {
        String postUrl = Constants.SERVER;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, (JSONObject response) -> {
            Log.d(Constants.TAG, "Ответ от сервера " + response + "\n");
            try {
                accessToken = response.getJSONObject("result").getString("accessToken");
                expires = response.getJSONObject("result").getLong("expires");
                refreshToken = response.getJSONObject("result").getString("refreshToken");
                editor.putString("accessToken", accessToken);
                editor.putLong("expires", expires);
                editor.putString("refreshToken", refreshToken);
                editor.apply();
                Log.d(Constants.TAG, accessToken);
                Intent intent = new Intent(MainActivity.this,SensorsActivity.class);
                startActivity(intent);
                finish();
            } catch (JSONException e) {
                Log.d(Constants.TAG, e + "");
                signUp.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG);
                toast.show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(Constants.TAG, "Ответ от сервера " + error);
                isLoginAvailable = false;
                Toast toast = Toast.makeText(MainActivity.this.getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                toast.show();
                signUp.setEnabled(true);
                progressBar.setVisibility(View.GONE);
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