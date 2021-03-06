package com.heavy.crudapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.heavy.crudapp.R;
import com.heavy.crudapp.adapters.UsuariosAdapter;
import com.heavy.crudapp.entidades.NetworkSingleton;
import com.heavy.crudapp.entidades.Usuario;
import com.heavy.crudapp.entidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConsultarListaUsuariosFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    RecyclerView recyclerViewUsuarios;
    ArrayList<Usuario> listaUsuarios;
    ProgressDialog dialog;
    //RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ImageView noNetworkImage;


    public ConsultarListaUsuariosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consultar_lista_usuarios, container, false);
        recyclerViewUsuarios = view.findViewById(R.id.recycler);
        noNetworkImage = view.findViewById(R.id.imageNoNetwork);
        listaUsuarios = new ArrayList<>();
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        noNetworkImage.setVisibility(View.INVISIBLE);
        //recyclerViewUsuarios.setHasFixedSize(true);
        //request = Volley.newRequestQueue(getContext());

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            noNetworkImage.setVisibility(View.INVISIBLE);
            mConsultarAllUsuarios();
        } else {
            noNetworkImage.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(),"Revise su conexión a Internet", Toast.LENGTH_SHORT).show();
        }


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
        NetworkSingleton.getObInstanceNetwork(getContext());
        String url = NetworkSingleton.getProtocol()+"://"+NetworkSingleton.getIp()+":"+NetworkSingleton.getPort()+"/consultarUsuariosAllTxt";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this, this);
        //request.add(jsonObjectRequest);
        VolleySingleton.getObInstanceVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
