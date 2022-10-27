package com.example.ejercicio_14;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ejercicio_14.config.Datos;
import com.example.ejercicio_14.config.SQLiteConexion;
import com.example.ejercicio_14.config.Transacciones;

import java.util.ArrayList;

public class ActivityLista extends AppCompatActivity {
    ListView lista;
    Button btnAtras;

    SQLiteConexion conexion;
    ArrayList<Datos> listaDatos;
    ArrayList<String> argDatos;
    Datos data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        init();
        cargarLista();
        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, argDatos);
        lista.setAdapter(adp);
        lista.setOnItemClickListener(this::onClickLista);
        btnAtras.setOnClickListener(this::onClickAtras);
    }

    private void onClickAtras(View view) {
        Intent atras = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(atras);
    }

    private void onClickLista(AdapterView<?> adapterView, View view, int i, long l) {
        showPhoto(i);
    }

    private void init(){
        lista = findViewById(R.id.lista);
        btnAtras = findViewById(R.id.btnAtras);
    }

    protected void cargarLista(){
        try {
            conexion = new SQLiteConexion(getApplicationContext(), Transacciones.NameDatabase, null, 2);
            SQLiteDatabase db = conexion.getReadableDatabase();
            listaDatos = new ArrayList<Datos>();
            Cursor cursor = db.rawQuery(Transacciones.consultDatos, null);
            while(cursor.moveToNext()) {
                data = new Datos();
                data.setId(cursor.getInt(0));
                data.setNombre(cursor.getString(1));
                data.setDescripcion(cursor.getString(2));
                data.setPath(cursor.getString(3));
                data.setImagen(cursor.getBlob(4));
                listaDatos.add(data);
            }
            cursor.close();
            fillList();
        }catch (SQLiteException ex){
            message(ex.getMessage());
        }
    }

    private void fillList() {
        argDatos = new ArrayList<String>();
        for(int i = 0; i<listaDatos.size(); i ++) {
            argDatos.add(listaDatos.get(i).getId()  + " | " + listaDatos.get(i).getNombre() + " | "+ listaDatos.get(i).getDescripcion());
        }
    }

    private void showPhoto(int i){
        try {
            Datos datos = listaDatos.get(i);
            Bundle bundle = new Bundle();
            bundle.putSerializable("datos", datos);
            Intent intent = new Intent(getApplicationContext(), com.example.ejercicio_14.ActivityVista.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }catch (Exception ex){
            message(ex.getMessage());
        }
    }

    public void message(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}