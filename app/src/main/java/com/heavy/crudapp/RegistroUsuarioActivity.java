package com.heavy.crudapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.heavy.crudapp.entidades.NetworkSingleton;
import com.heavy.crudapp.entidades.VolleySingleton;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RegistroUsuarioActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener, View.OnClickListener {

    EditText registroDocumento, registroNombre, registroProfesion, registroCorreo, registroPassword;
    Button btnRegistrar;
    ImageView registroImagen;
    ProgressDialog dialog;
    JsonObjectRequest jsonObjectRequest;
    String currentPhotoPath;
    Uri imageServer;

    boolean isGallery = false;
    boolean isCamera = false;

    //Codes permission
    private static final int REQUEST_PERMISSION_CODE_GALLERY = 100;
    private static final int REQUEST_IMAGE_GALLERY = 101;
    private static final int REQUEST_PERMISSION_CODE_CAMERA = 103;
    private static final int REQUEST_IMAGE_CAMERA = 102;

    //Carpertas global
    private static final String FOLDER_PRINCIPAL = "CrudAppFolder/";
    private static final String FOLDER_IMAGE = "CrudApp";
    private static final String DIRECTORIO_IMAGE = FOLDER_PRINCIPAL + FOLDER_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        registroDocumento = findViewById(R.id.documentoRegistroAvt);
        registroNombre = findViewById(R.id.usernameRegistroAvt);
        registroProfesion = findViewById(R.id.profesionRegistroAvt);
        registroCorreo = findViewById(R.id.correoRegistroAvt);
        registroPassword = findViewById(R.id.passwordRegistroAvt);
        registroImagen = findViewById(R.id.imagenRegistroAvt);
        btnRegistrar = findViewById(R.id.btnRegistroAvt);

        btnRegistrar.setOnClickListener(this);
        registroImagen.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegistroAvt:
                mConsumirRegistroUsuarios();
                break;
            case R.id.imagenRegistroAvt:
                mMostrarDialogoOpciones();
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        dialog.hide();
        Toast.makeText(this, "Falló el registro -> " + error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        dialog.hide();
        Toast.makeText(this, "El registro se realizó correctamente", Toast.LENGTH_SHORT).show();
        mLimpiarData();
        Intent registroExitoso = new Intent(RegistroUsuarioActivity.this, LoginActivity.class);
        startActivity(registroExitoso);
        finish();
    }

    private void mConsumirRegistroUsuarios() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        NetworkSingleton.getObInstanceNetwork(this);
        String url = NetworkSingleton.getProtocol()+"://"+NetworkSingleton.getIp()+":"+NetworkSingleton.getPort()+"/signup";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", registroCorreo.getText().toString());
        params.put("password", registroPassword.getText().toString());
        params.put("document", registroDocumento.getText().toString());
        params.put("name", registroNombre.getText().toString());
        params.put("occupation", registroProfesion.getText().toString());
        params.put("imgbase64",mConvertImageToString());

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(params),this, this);
        //request.add(jsonObjectRequest);
        VolleySingleton.getObInstanceVolley(this).addToRequestQueue(jsonObjectRequest);
    }

    private String mConvertImageToString(){
        try {
            Bitmap bitmap = null;
            if(this.isGallery){
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageServer);
            } else if(this.isCamera){
                File file = new File(this.currentPhotoPath);
                Uri uri = Uri.fromFile(file);
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                bitmap = getRezidBitmap(bitmap, 1024);
            }
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

    private void mMostrarDialogoOpciones(){
        this.isCamera = false;
        this.isGallery = false;
        final CharSequence[] opciones = {"Tomar Foto", "Cargar Imagen", "Cancelar"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(this);
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
        if(cameraIntent.resolveActivity(getPackageManager())!= null){
            //startActivityForResult(cameraI3ntent, REQUEST_IMAGE_CAMERA);
            File fotoFile = null;
            try {
                fotoFile = createFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(fotoFile != null){
                Uri photoUri = FileProvider.getUriForFile(
                        this,
                        getPackageName()+".provider",
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
        File storageFile = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
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
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
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
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
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
                this.isGallery = true;
            } else {
                Toast.makeText(this,"You didn't choose any picture",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == REQUEST_IMAGE_CAMERA){
            if(resultCode == Activity.RESULT_OK){
                registroImagen.setImageURI(Uri.parse(currentPhotoPath));
                imageServer = Uri.parse(currentPhotoPath);
                this.isCamera = true;
            }
        }
    }

    private void mSolicitarPermisosManual() {
        final CharSequence[] opciones={"Yes","No"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Yes")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
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
}
