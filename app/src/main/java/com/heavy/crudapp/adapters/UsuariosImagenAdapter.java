package com.heavy.crudapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heavy.crudapp.R;
import com.heavy.crudapp.entidades.Usuario;

import java.util.ArrayList;

public class UsuariosImagenAdapter extends RecyclerView.Adapter<UsuariosImagenAdapter.UsuariosViewHolder> {

    ArrayList<Usuario> listaUsuarios;

    public UsuariosImagenAdapter(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public UsuariosImagenAdapter.UsuariosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuarios_list_image, parent, false);
        return new UsuariosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosImagenAdapter.UsuariosViewHolder holder, int position) {
        holder.listaDocumento.setText(listaUsuarios.get(position).getDocumento().toString());
        holder.listaNombre.setText(listaUsuarios.get(position).getNombre().toString());
        holder.listaCorreo.setText(listaUsuarios.get(position).getCorreo().toString());
        holder.listaProfesion.setText(listaUsuarios.get(position).getProfesion().toString());

        if(listaUsuarios.get(position).getImagen() != null){
            holder.imagenLista.setImageBitmap(listaUsuarios.get(position).getImagen());
        } else {
            holder.imagenLista.setImageResource(R.drawable.img_base);
        }

    }

    @Override
    public int getItemCount() {
        return this.listaUsuarios.size();
    }

    public class UsuariosViewHolder extends RecyclerView.ViewHolder {
        TextView listaDocumento, listaNombre, listaCorreo, listaProfesion;
        ImageView imagenLista;
        public UsuariosViewHolder(@NonNull View itemView) {
            super(itemView);
            listaDocumento = itemView.findViewById(R.id.listaDocumento);
            listaNombre = itemView.findViewById(R.id.listaNombre);
            listaCorreo = itemView.findViewById(R.id.listaCorreo);
            listaProfesion = itemView.findViewById(R.id.listaProfesion);
            imagenLista = itemView.findViewById(R.id.imagenLista);
        }
    }
}
