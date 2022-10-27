package com.example.ejercicio_14;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.ejercicio_14.config.SQLiteConexion;
import com.example.ejercicio_14.config.Transacciones;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    EditText nombre, desc;
    Button btnGuardar, btnFoto, btnLista;
    ImageView imagen;

    static final int REQUESTCODECAMARA = 100;
    static final int REQUESTTAKEPHOTO = 101;
    Bitmap bmImagen;
    String path;
    SQLiteConexion conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        btnGuardar.setOnClickListener(this::onClickGuardar);
        btnFoto.setOnClickListener(this::onClickTakePhoto);
        btnLista.setOnClickListener(this::onClickListActivity);
    }

    private void onClickListActivity(View view) {
        Intent lista = new Intent(getApplicationContext(), com.example.ejercicio_14.ActivityLista.class);
        startActivity(lista);
    }

    private void onClickTakePhoto(View view) {
        assignPermissions();
    }

    private void onClickGuardar(View view) {
        if(emptyFields(nombre)){
            if(emptyFields(desc)){
                if(bmImagen.getByteCount() > 0){
                    guardar();
                }else message("Debe tomar una foto");
            }else message("Debe ingresar su descipción");
        }else message("Debe ingresar su nombre");
    }

    private void guardar(){
        try {
            conexion = new SQLiteConexion(getApplicationContext(), Transacciones.NameDatabase, null, 2);
            SQLiteDatabase db = conexion.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Transacciones.nombre, nombre.getText().toString());
            values.put(Transacciones.descripcion, desc.getText().toString());
            values.put(Transacciones.pathImage, path);

            ByteArrayOutputStream baos = new ByteArrayOutputStream(10480);
            bmImagen.compress(Bitmap.CompressFormat.JPEG, 0 , baos);
            byte[] blob = baos.toByteArray();
            values.put(Transacciones.image, blob);

            Long result = db.insert(Transacciones.tablaDatos, Transacciones.id, values);
            db.close();
            message("Datos ingresados exitosamente");
            cleanFields();
        }catch (SQLiteException ex){
            message(ex.getMessage());
        }
    }

    private void assignPermissions() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA }, REQUESTCODECAMARA);
        }else takePhoto();
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.example.ejercicio_14.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUESTTAKEPHOTO);
                }
            } catch (IOException ex) {
                message(ex.getMessage());
            }catch (Exception e){
                message(e.getMessage());
            }
        }
    }

    private void init(){
        nombre = findViewById(R.id.txtVNombre);
        desc = findViewById(R.id.txtVDesc);
        btnGuardar = findViewById(R.id.btnSQL);
        btnFoto = findViewById(R.id.btnTomarFoto);
        btnLista = findViewById(R.id.btnVerLista);
        imagen = findViewById(R.id.imgVFoto);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpeg", storageDir);

        path = image.getAbsolutePath();
        return image;
    }

    public boolean emptyFields(EditText field){
        return field.getText().toString().length() > 1 ? true : false;
    }

    public void message(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void cleanFields(){
        nombre.setText("");
        desc.setText("");
        imagen.setImageBitmap(null);
        bmImagen = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUESTCODECAMARA) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) takePhoto();
            else message("Permiso denegado");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUESTTAKEPHOTO && resultCode == RESULT_OK) {
            Bitmap image = BitmapFactory.decodeFile(path);
            bmImagen = image;
            imagen.setImageBitmap(image);
            message("La imagen se guardo exitosamente");
        }
    }
}