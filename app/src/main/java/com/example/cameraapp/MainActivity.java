package com.example.cameraapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnCamara;
    ImageView imgFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCamara = findViewById(R.id.btnCamara);
        imgFoto = findViewById(R.id.imgFoto);

        btnCamara.setOnClickListener(view -> inicializarCamara());
    }

    private void inicializarCamara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        alternativaStartForResult.launch(intent);
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
                            Bundle extras = data.getExtras();
                            Bitmap imgBitmap = (Bitmap) extras.get("data");
                            imgFoto.setImageBitmap(imgBitmap);
                        case MainActivity.RESULT_CANCELED:
                            Toast.makeText(MainActivity.this, "Proceso denegado", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

}


