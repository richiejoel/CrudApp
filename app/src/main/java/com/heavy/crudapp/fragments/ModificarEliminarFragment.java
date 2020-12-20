package com.heavy.crudapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.heavy.crudapp.R;
import com.heavy.crudapp.entidades.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
    String currentPhotoPath;
    Uri imageServer;

    private static final String CONS_SERVER = "http://192.168.100.44:3000/";
    private String tipo_query = "";

    //Codes permission
    private static final int REQUEST_PERMISSION_CODE_GALLERY = 100;
    private static final int REQUEST_IMAGE_GALLERY = 101;
    private static final int REQUEST_PERMISSION_CODE_CAMERA = 103;
    private static final int REQUEST_IMAGE_CAMERA = 102;

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
        imagen.setOnClickListener(this);
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
            case R.id.imagenUpdate:
                mMostrarDialogoOpciones();
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
                    Glide.with(getContext()).load(CONS_SERVER + usuario.getImageUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imagen);
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
        params.put("imgbase64",mConvertImageToString());

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

    private void mMostrarDialogoOpciones(){
        final CharSequence[] opciones = {"Tomar Foto", "Cargar Imagen", "Cancelar"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getContext());
        alertOpciones.setTitle("Seleccione una opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(opciones[which].equals("Tomar Foto")){
                    mValidatePermissionsCameraWriteStorage();
                } else if(opciones[which].equals("Cargar Imagen")){
                    mValidatePermissionsReadStorage();
                } else {
                    dialog.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void mOpenGallery(){
        Intent intentGallery = new Intent(Intent.ACTION_PICK);
        intentGallery.setType("image/");
        startActivityForResult(intentGallery.createChooser(intentGallery,"Seleccione la aplicación"),REQUEST_IMAGE_GALLERY);
    }

    private void goToCamera()  {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getActivity().getPackageManager())!= null){
            //startActivityForResult(cameraI3ntent, REQUEST_IMAGE_CAMERA);
            File fotoFile = null;
            try {
                fotoFile = createFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(fotoFile != null){
                Uri photoUri = FileProvider.getUriForFile(
                        getContext(),
                        getActivity().getPackageName()+".provider",
                        fotoFile
                );
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(cameraIntent,REQUEST_IMAGE_CAMERA);
            }
        }
    }

    private File createFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss", Locale.getDefault()).format(new Date());
        String imgFileName = "IMG_" + timeStamp + "_";
        File storageFile = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imgFileName,
                ".png",
                storageFile
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void mValidatePermissionsReadStorage(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                mOpenGallery();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_GALLERY);
            }
        } else {
            mOpenGallery();
        }
    }

    private void mValidatePermissionsCameraWriteStorage(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                goToCamera();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE_CAMERA);
            }
        } else {
            goToCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_CODE_GALLERY){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mOpenGallery();
            } else {
                mSolicitarPermisosManual();
            }
        }

        if(requestCode == REQUEST_PERMISSION_CODE_CAMERA){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                goToCamera();
            } else {
                mSolicitarPermisosManual();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_GALLERY){
            if(resultCode == Activity.RESULT_OK && data != null){
                Uri photo = data.getData();
                imagen.setImageURI(photo);
                imageServer = photo;
            } else {
                Toast.makeText(getContext(),"You didn't choose any picture",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == REQUEST_IMAGE_CAMERA){
            if(resultCode == Activity.RESULT_OK){
                imagen.setImageURI(Uri.parse(currentPhotoPath));
                imageServer = Uri.parse(currentPhotoPath);
            }
        }
    }

    private void mSolicitarPermisosManual() {
        final CharSequence[] opciones={"Yes","No"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getContext());
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Yes")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getActivity().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }

            }
        });
        alertOpciones.show();
    }

    private String mConvertImageToString(){
        try {
            Bitmap bitmap = null;
            File file = new File(this.currentPhotoPath);
            Uri uri = Uri.fromFile(file);
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            bitmap = getRezidBitmap(bitmap, 1024);
            ByteArrayOutputStream array=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,array);
            byte[] imagenByte=array.toByteArray();
            String imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);
            return imagenString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void mLimpiarData(){
        documentoUpdate.setText("");
        nombreUpdate.setText("");
        correoUpdate.setText("");
        profesionUpdate.setText("");
        imagen.setImageResource(R.drawable.img_base);
    }

    private Bitmap getRezidBitmap (Bitmap bitmap, int maxSize){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(width <= maxSize && width <= maxSize){
            return bitmap;
        }

        float bitmapRatio = (float) width / (float) height;
        if(bitmapRatio > 1){
            width = maxSize;
            height = (int) (width/bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap,width, height, true);

    }
}
