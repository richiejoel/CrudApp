package com.heavy.crudapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.heavy.crudapp.R;
import com.heavy.crudapp.entidades.NetworkSingleton;
import com.heavy.crudapp.entidades.Usuario;

import java.util.ArrayList;

public class UsuariosImagenUrlAdapter extends RecyclerView.Adapter<UsuariosImagenUrlAdapter.UsuariosUrlViewHolder> {

    ArrayList<Usuario> listaUsuarios;
    private Context ctx;
    private String CONS_SERVER = "";

    public UsuariosImagenUrlAdapter(ArrayList<Usuario> listaUsuarios, Context ctx) {
        this.listaUsuarios = listaUsuarios;
        this.ctx = ctx;
    }
    @NonNull
    @Override
    public UsuariosImagenUrlAdapter.UsuariosUrlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuarios_list_image, parent, false);
        return new UsuariosImagenUrlAdapter.UsuariosUrlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosImagenUrlAdapter.UsuariosUrlViewHolder holder, int position) {
        holder.listaDocumento.setText(listaUsuarios.get(position).getDocumento().toString());
        holder.listaNombre.setText(listaUsuarios.get(position).getNombre().toString());
        holder.listaCorreo.setText(listaUsuarios.get(position).getCorreo().toString());
        holder.listaProfesion.setText(listaUsuarios.get(position).getProfesion().toString());

        if(listaUsuarios.get(position).getImageUrl() != null){
            NetworkSingleton.getObInstanceNetwork(this.ctx);
            CONS_SERVER = NetworkSingleton.getProtocol()+"://"+NetworkSingleton.getIp()+":"+NetworkSingleton.getPort()+"/";
            Glide.with(ctx).load(CONS_SERVER + listaUsuarios.get(position).getImageUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.imagenLista);
            System.out.println("URL IMAGEN -> "+CONS_SERVER + listaUsuarios.get(position).getImageUrl());
        } else {
            holder.imagenLista.setImageResource(R.drawable.img_base);
        }
    }


    @Override
    public int getItemCount() {
        return this.listaUsuarios.size();
    }

    public class UsuariosUrlViewHolder extends RecyclerView.ViewHolder {
        TextView listaDocumento, listaNombre, listaCorreo, listaProfesion;
        ImageView imagenLista;
        public UsuariosUrlViewHolder(@NonNull View itemView) {
            super(itemView);
            listaDocumento = itemView.findViewById(R.id.listaDocumento);
            listaNombre = itemView.findViewById(R.id.listaNombre);
            listaCorreo = itemView.findViewById(R.id.listaCorreo);
            listaProfesion = itemView.findViewById(R.id.listaProfesion);
            imagenLista = itemView.findViewById(R.id.imagenLista);
        }
    }
}
