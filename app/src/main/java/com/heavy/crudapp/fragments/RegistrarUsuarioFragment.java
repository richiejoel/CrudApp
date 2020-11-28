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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.heavy.crudapp.MainActivity;
import com.heavy.crudapp.R;

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
public class RegistrarUsuarioFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener, View.OnClickListener {


    EditText registroDocumento, registroNombre, registroProfesion, registroCorreo, registroPassword;
    Button btnRegistrar, btnFoto;
    ImageView registroImagen;
    ProgressDialog dialog;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    String currentPhotoPath;
    Uri imageServer;

    //Codes permission
    private static final int REQUEST_PERMISSION_CODE_GALLERY = 100;
    private static final int REQUEST_IMAGE_GALLERY = 101;
    private static final int REQUEST_PERMISSION_CODE_CAMERA = 103;
    private static final int REQUEST_IMAGE_CAMERA = 102;

    //Carpertas global
    private static final String FOLDER_PRINCIPAL = "CrudAppFolder/";
    private static final String FOLDER_IMAGE = "CrudApp";
    private static final String DIRECTORIO_IMAGE = FOLDER_PRINCIPAL + FOLDER_IMAGE;


    public RegistrarUsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_usuario, container, false);
        registroDocumento = view.findViewById(R.id.registroDocumento);
        registroNombre = view.findViewById(R.id.registroNombre);
        registroProfesion = view.findViewById(R.id.registroProfesion);
        registroCorreo = view.findViewById(R.id.registroCorreo);
        registroPassword = view.findViewById(R.id.registroPassword);
        registroImagen = view.findViewById(R.id.registroImagen);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        btnFoto = view.findViewById(R.id.btnFoto);

        request = Volley.newRequestQueue(getContext());
        btnRegistrar.setOnClickListener(this);
        btnFoto.setOnClickListener(this);

        return view;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        dialog.hide();
        Toast.makeText(getContext(), "Falló el registro -> " + error.toString(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResponse(JSONObject response) {
        dialog.hide();
        Toast.makeText(getContext(), "El registro se realizó correctamente", Toast.LENGTH_SHORT).show();
        mLimpiarData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegistrar:
                mConsumirRegistroUsuarios();
                break;
            case R.id.btnFoto:
                mMostrarDialogoOpciones();
                break;
        }
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
                registroImagen.setImageURI(photo);
                imageServer = photo;
            } else {
                Toast.makeText(getContext(),"You didn't choose any picture",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == REQUEST_IMAGE_CAMERA){
            if(resultCode == Activity.RESULT_OK){
                registroImagen.setImageURI(Uri.parse(currentPhotoPath));
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

    private void mLimpiarData(){
        registroCorreo.setText("");
        registroPassword.setText("");
        registroDocumento.setText("");
        registroNombre.setText("");
        registroProfesion.setText("");
    }

    private void mConsumirRegistroUsuarios() {
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.show();
        String url = "http://192.168.100.44:3000/signup";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", registroCorreo.getText().toString());
        params.put("password", registroPassword.getText().toString());
        params.put("document", registroDocumento.getText().toString());
        params.put("name", registroNombre.getText().toString());
        params.put("occupation", registroProfesion.getText().toString());
        params.put("imgbase64",mConvertImageToString());

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(params),this, this);
        request.add(jsonObjectRequest);
    }

    private String mConvertImageToString(){
        try {
            Bitmap bitmap = null;
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),imageServer);

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
}
