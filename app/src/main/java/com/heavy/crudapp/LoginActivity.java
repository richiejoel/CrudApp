package com.heavy.crudapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.heavy.crudapp.entidades.NetworkSingleton;
import com.heavy.crudapp.entidades.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener, View.OnClickListener {

    Button btnLogin;
    TextView txtRegistrarse;
    EditText edtCorreo, edtPassword;

    ProgressDialog dialog;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.botonLogin);
        txtRegistrarse = findViewById(R.id.registerHere);
        edtCorreo = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);

        btnLogin.setOnClickListener(this);
        txtRegistrarse.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.botonLogin:
                mIniciarSesion();
                break;
            case R.id.registerHere:
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        dialog.hide();
        Toast.makeText(this, "Falló el login -> " + error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        dialog.hide();
        Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();
        mLimpiarData();
        Intent loginExitoso = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(loginExitoso);
        finish();
    }


    private void mIniciarSesion(){

        if(mValidarIngresoDatos()){
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading...");
            dialog.show();

            NetworkSingleton.getObInstanceNetwork(getApplicationContext());
            String url = NetworkSingleton.getProtocol()+"://"+NetworkSingleton.getIp()+":"+NetworkSingleton.getPort()+"/signin";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("email", edtCorreo.getText().toString());
            params.put("password", edtPassword.getText().toString());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(params),this, this);
            VolleySingleton.getObInstanceVolley(this).addToRequestQueue(jsonObjectRequest);
        } else {
            Toast.makeText(this, "Ingrese el correo y la contraseña", Toast.LENGTH_SHORT).show();
        }

    }

    private void mLimpiarData(){
        edtCorreo.setText("");
        edtPassword.setText("");
    }

    private boolean mValidarIngresoDatos(){
        if(edtCorreo.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()){
            return false;
        } else{
            return true;
        }
    }
}
