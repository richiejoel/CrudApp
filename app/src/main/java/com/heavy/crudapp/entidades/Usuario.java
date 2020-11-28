package com.heavy.crudapp.entidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String documento;
    private String nombre;
    private String profesion;
    private String correo;
    private String data;
    private Bitmap imagen;
    private String imageUrl;

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        try {
            byte[] byteCode = Base64.decode(data,Base64.DEFAULT);
            //this.imagen = BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
            int alto = 300; //alto pixeles
            int ancho = 300; //ancho pixeles
            Bitmap foto = BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
            this.imagen = Bitmap.createScaledBitmap(foto, alto, ancho, true);

        } catch (Exception ex){

        }

    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
