package com.heavy.crudapp.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.heavy.crudapp.R;
import com.heavy.crudapp.adapters.UsuariosAdapter;
import com.heavy.crudapp.entidades.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConsultarListaUsuariosFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    RecyclerView recyclerViewUsuarios;
    ArrayList<Usuario> listaUsuarios;
    ProgressDialog dialog;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    public ConsultarListaUsuariosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consultar_lista_usuarios, container, false);
        recyclerViewUsuarios = view.findViewById(R.id.recycler);
        listaUsuarios = new ArrayList<>();
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerViewUsuarios.setHasFixedSize(true);
        request = Volley.newRequestQueue(getContext());
        mConsultarAllUsuarios();

        return view;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        dialog.hide();
        Toast.makeText(getContext(), "Falló la consulta -> " + error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Usuario usuario = null;
        JSONArray jsonArray = response.optJSONArray("usuarios");
        try{
            for (int i=0; i<jsonArray.length(); i++){
                usuario = new Usuario();
                JSONObject jsonObject = null;
                jsonObject = jsonArray.getJSONObject(i);
                usuario.setDocumento(jsonObject.optString("document"));
                usuario.setProfesion(jsonObject.optString("occupation"));
                usuario.setCorreo(jsonObject.optString("email"));
                usuario.setNombre(jsonObject.optString("name"));
                listaUsuarios.add(usuario);
            }
            dialog.hide();
            Toast.makeText(getContext(), "La consulta se realizó correctamente", Toast.LENGTH_SHORT).show();
            UsuariosAdapter adapter = new UsuariosAdapter(listaUsuarios);
            recyclerViewUsuarios.setAdapter(adapter);
        } catch (JSONException ex){
            Toast.makeText(getContext(), "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
            dialog.hide();
        }

    }

    private void mConsultarAllUsuarios(){
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.show();
        String url = "http://192.168.100.44:3000/consultarUsuariosAllTxt";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this, this);
        request.add(jsonObjectRequest);
    }
}
