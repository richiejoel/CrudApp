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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.heavy.crudapp.R;
import com.heavy.crudapp.entidades.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModificarEliminarFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener, View.OnClickListener {

    EditText documentoUpdate;
    TextView nombreUpdate, correoUpdate, profesionUpdate;
    ImageView imagen;
    Button btnUpdate, btnDelete;
    ImageButton btnConsultarUp;
    ProgressDialog dialog;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    private static final String CONS_SERVER = "http://192.168.100.44:3000/";
    private String tipo_query = "";

    public ModificarEliminarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modificar_eliminar, container, false);
        documentoUpdate = view.findViewById(R.id.documentoUpdate);
        nombreUpdate = view.findViewById(R.id.nombreUpdate);
        correoUpdate = view.findViewById(R.id.correoUpdate);
        profesionUpdate = view.findViewById(R.id.profesionUpdate);
        imagen = view.findViewById(R.id.imagenUpdate);
        btnConsultarUp = view.findViewById(R.id.btnSearch);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);

        request = Volley.newRequestQueue(getContext());
        btnConsultarUp.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSearch:
                mConsultarUsuarios();
                break;
            case R.id.btnUpdate:
                mUpdateUsers();
                break;
            case R.id.btnDelete:
                mDeleteUser();
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
        if(tipo_query.equals( "CONSULTA")){
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                Usuario usuario = new Usuario();
                usuario.setNombre(jsonObject.optString("name"));
                usuario.setCorreo(jsonObject.optString("email"));
                usuario.setProfesion(jsonObject.optString("ocuppation"));
                usuario.setImageUrl(jsonObject.optString("imagePath"));

                nombreUpdate.setText(usuario.getNombre());
                profesionUpdate.setText(usuario.getProfesion());
                correoUpdate.setText(usuario.getCorreo());
                if(usuario.getImageUrl() != null){
                    Glide.with(getContext()).load(CONS_SERVER + usuario.getImageUrl()).into(imagen);
                } else {
                    imagen.setImageResource(R.drawable.img_base);
                }

                Toast.makeText(getContext(), "La consulta se realizó correctamente", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*Log.i("Response",response.toString());*/
        } else if(tipo_query.equals("UPDATE")){
            Toast.makeText(getContext(), "La actualización se realizó correctamente", Toast.LENGTH_SHORT).show();
        } else if(tipo_query.equals("DELETE")){
            Toast.makeText(getContext(), "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
            mLimpiarData();
        } else {
            Toast.makeText(getContext(), "Estimado cliente, el servicio no está disponible por el momento", Toast.LENGTH_SHORT).show();
        }
    }

    private void mConsultarUsuarios(){
        tipo_query = "CONSULTA";
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.show();
        String url = "http://192.168.100.44:3000/consultarUsuarioUrl";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("document", documentoUpdate.getText().toString());

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(params),this, this);
        request.add(jsonObjectRequest);
    }

    private void mUpdateUsers() {
        tipo_query = "UPDATE";
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.show();
        String url = "http://192.168.100.44:3000/updateUser";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("document", documentoUpdate.getText().toString());
        params.put("name", nombreUpdate.getText().toString());
        params.put("email", correoUpdate.getText().toString());
        params.put("occupation", profesionUpdate.getText().toString());

        jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,url,new JSONObject(params),this, this);
        request.add(jsonObjectRequest);
    }

    private void mDeleteUser(){
        tipo_query = "DELETE";
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.show();
        String url = "http://192.168.100.44:3000/deleteOneUser";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("document", documentoUpdate.getText().toString());

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(params),this, this);
        request.add(jsonObjectRequest);
    }

    private void mLimpiarData(){
        documentoUpdate.setText("");
        nombreUpdate.setText("");
        correoUpdate.setText("");
        profesionUpdate.setText("");
        imagen.setImageResource(R.drawable.img_base);
    }
}
