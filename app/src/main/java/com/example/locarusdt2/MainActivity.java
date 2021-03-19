package com.example.locarusdt2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.VoiceInteractor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

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
    }

    private void getJSon (){
        String url = "http://stage.local/api/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

    }
}