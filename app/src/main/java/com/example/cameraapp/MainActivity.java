package com.example.cameraapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btnCamara;
    ImageView imgFoto;
    String rutaImagen;
    private final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static String[] solicitudPermisosCamara = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permisoStorage();
        //permisosCamara();
        btnCamara = findViewById(R.id.btnCamara);
        imgFoto = findViewById(R.id.imgFoto);

        btnCamara.setOnClickListener(view -> {
            try {
                inicializarCamara();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void inicializarCamara() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  //ACTION_IMAGE_CAPTURE
        File imagenArchivo = null;
        try {
            imagenArchivo = crearImagen();
        }catch (IOException ex){
            Toast.makeText(MainActivity.this, "Proceso incompleto", Toast.LENGTH_SHORT).show();
        }
        if(imagenArchivo != null){
            Uri fotoUri = FileProvider.getUriForFile(this, "com.example.cameraapp.fileprovider", imagenArchivo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            alternativaStartForResult.launch(intent);
        }
    }

    private final ActivityResultLauncher<Intent> alternativaStartForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    switch (result.getResultCode()) {
                        case MainActivity.RESULT_OK:
                            int requestCode = 0, resultCode = 0;
                            Intent data = result.getData();
                            MainActivity.super.onActivityResult(requestCode, resultCode, data);
                            Bitmap imgBitmap = BitmapFactory.decodeFile(rutaImagen);
                            imgBitmap = corrigiendoorientacion(imgBitmap);
                            imgFoto.setImageBitmap(imgBitmap);
                        case MainActivity.RESULT_CANCELED:
                            Toast.makeText(MainActivity.this, "Proceso completado", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

    private File crearImagen () throws IOException {
        String nombreImagen = "fotoCapturada_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);
        rutaImagen = imagen.getAbsolutePath();
        return imagen;
    }

    public Bitmap corrigiendoorientacion(Bitmap imagen){
        if(imagen.getWidth() > imagen.getHeight()){
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            imagen = Bitmap.createBitmap(imagen, 0, 0, imagen.getWidth(), imagen.getHeight(), matrix, true);
        }
        return imagen;
    }

    private void solicitandoPermisosCamara() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //Log.i("Mensaje", "No se tiene permiso para la Camara.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 225);
            //if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){}


            //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
           // Log.i("Mensaje", "Se tiene permiso para usar la camara!");
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String avisoPopUp = "";
        toast = Toast.makeText(this, avisoPopUp, Toast.LENGTH_LONG);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    avisoPopUp = "Camara permitida";
                } else {
                    avisoPopUp = "No ha activado la camara!";
                }
                toast.show();
                return;
        }

    }

    private void permisoStorage(){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){ //No tiene el permiso
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        solicitudPermisosCamara,
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }
/*
    private void permisosCamara(){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){ //No tiene el permiso
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        solicitudPermisosCamara,
                        100);
            }
        }
    }*/

}


