package com.heavy.crudapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heavy.crudapp.R;
import com.heavy.crudapp.entidades.Usuario;

import java.util.ArrayList;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuariosViewHolder> {

    ArrayList<Usuario> listaUsuarios;

    public UsuariosAdapter(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public UsuariosAdapter.UsuariosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuarios_list, parent, false);
        return new UsuariosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosAdapter.UsuariosViewHolder holder, int position) {
        holder.listaDocumento.setText(listaUsuarios.get(position).getDocumento().toString());
        holder.listaNombre.setText(listaUsuarios.get(position).getNombre().toString());
        holder.listaCorreo.setText(listaUsuarios.get(position).getCorreo().toString());
        holder.listaProfesion.setText(listaUsuarios.get(position).getProfesion().toString());

    }

    @Override
    public int getItemCount() {
        return this.listaUsuarios.size();
    }

    public class UsuariosViewHolder extends RecyclerView.ViewHolder {
        TextView listaDocumento, listaNombre, listaCorreo, listaProfesion;
        public UsuariosViewHolder(@NonNull View itemView) {
            super(itemView);
            listaDocumento = itemView.findViewById(R.id.listaDocumento);
            listaNombre = itemView.findViewById(R.id.listaNombre);
            listaCorreo = itemView.findViewById(R.id.listaCorreo);
            listaProfesion = itemView.findViewById(R.id.listaProfesion);
        }
    }
}
