package com.example.cameraapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imagenArchivo = null;
        try {
            imagenArchivo = crearImagen();
        }catch (IOException ex){
            Toast.makeText(MainActivity.this, "Proceso incompleto", Toast.LENGTH_SHORT).show();
        }
        if(imagenArchivo != null){
            Uri fotou = FileProvider.getUriForFile(this, "com.example.cameraapp.fileprovider", imagenArchivo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotou);
            alternativaStartForResult.launch(intent);
        }

        //alternativaStartForResult.launch(intent);
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
       //File directorio = getExternalFilesDir(Environment.getExternalStorageState(Environment.DIRECTORY_PICTURES), "MyCameraApp");

        //File directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");

        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);

        //File imagen = new File(directorio + "Mary" + nombreImagen + ".jpg");
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

/*
    ExifInterface ei = new ExifInterface(photoPath);
    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    Bitmap rotatedBitmap = null;
    switch(orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90: rotatedBitmap = rotateImage(bitmap, 90);
        break; case ExifInterface.ORIENTATION_ROTATE_180: rotatedBitmap = rotateImage(bitmap, 180);
        break; case ExifInterface.ORIENTATION_ROTATE_270: rotatedBitmap = rotateImage(bitmap, 270); break;
        case ExifInterface.ORIENTATION_NORMAL: default: rotatedBitmap = bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix(); matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }*/
}


