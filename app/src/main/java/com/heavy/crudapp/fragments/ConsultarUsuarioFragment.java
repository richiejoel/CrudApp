package com.heavy.crudapp.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.heavy.crudapp.R;
import com.heavy.crudapp.entidades.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;


public class ConsultarUsuarioFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener, View.OnClickListener {

    EditText documentoConsulta;
    TextView nombreConsulta, correoConsulta, profesionConsulta;
    ImageView imagen;
    Button btnConsultar;
    ProgressDialog dialog;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public ConsultarUsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consultar_usuario, container, false);
        documentoConsulta = view.findViewById(R.id.documentoConsulta);
        nombreConsulta = view.findViewById(R.id.nombreConsulta);
        correoConsulta = view.findViewById(R.id.correoConsulta);
        profesionConsulta = view.findViewById(R.id.profesionConsulta);
        imagen = view.findViewById(R.id.imagen);
        btnConsultar = view.findViewById(R.id.btnConsultar);

        request = Volley.newRequestQueue(getContext());
        btnConsultar.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnConsultar:
                mConsultarUsuarios();
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        dialog.hide();
        Toast.makeText(getContext(), "Falló la consulta -> " + error.toString(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResponse(JSONObject response) {
        dialog.hide();
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            Usuario usuario = new Usuario();
            usuario.setNombre(jsonObject.optString("name"));
            usuario.setCorreo(jsonObject.optString("email"));
            usuario.setProfesion(jsonObject.optString("ocuppation"));
            usuario.setData(jsonObject.optString("imgbase64"));

            nombreConsulta.setText(usuario.getNombre());
            profesionConsulta.setText(usuario.getProfesion());
            correoConsulta.setText(usuario.getCorreo());
            if(usuario.getImagen() != null){
                imagen.setImageBitmap(usuario.getImagen());
            } else {
                imagen.setImageResource(R.drawable.img_base);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(getContext(), "La consulta se realizó correctamente", Toast.LENGTH_SHORT).show();
        Log.i("Response",response.toString());

    }

    private void mConsultarUsuarios(){
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.show();
        String url = "http://192.168.100.44:3000/consultarUsuario";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("document", documentoConsulta.getText().toString());

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(params),this, this);
        request.add(jsonObjectRequest);
    }
}
