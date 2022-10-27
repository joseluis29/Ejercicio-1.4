package com.example.ejercicio_14;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ejercicio_14.config.Datos;

import java.io.ByteArrayInputStream;

public class ActivityVista extends AppCompatActivity {
    EditText nombre, desc;
    ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista);
        init();
        Bundle data = getIntent().getExtras();
        Datos datos = null;
        if(data != null){
            datos = (Datos) data.getSerializable("datos");

            nombre.setText(datos.getNombre());
            desc.setText(datos.getDescripcion());
            showPhoto(datos.getImagen());
            Bitmap bmImagen = BitmapFactory.decodeFile(datos.getPath());
            foto.setImageBitmap(bmImagen);
        }
    }

    private void init(){
        nombre = findViewById(R.id.txtVNombre);
        desc = findViewById(R.id.txtVDesc);
        foto = findViewById(R.id.imgVFoto);
    }

    private void showPhoto(byte[] img){
        Bitmap bitmap = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(img);
        bitmap = BitmapFactory.decodeStream(bais);
        foto.setImageBitmap(bitmap);
    }
}