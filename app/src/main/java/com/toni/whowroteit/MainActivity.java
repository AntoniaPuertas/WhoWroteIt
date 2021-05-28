package com.toni.whowroteit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText inputLibro;
    TextView txtLibro;
    TextView txtAutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputLibro = findViewById(R.id.inputLibro);
        txtLibro = findViewById(R.id.txtLibro);
        txtAutor = findViewById(R.id.txtAutor);


    }

    public void buscarLibro(View view){
        String cadenaBusqueda = inputLibro.getText().toString();

        //ocultar el teclado cuando se toca el boton
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager != null){
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        //comprobar si hay conexion de red
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(connectivityManager != null){
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        //se realiza la peticion si hay conexion y la cadena de búsqueda no está vacía
        if(networkInfo != null && networkInfo.isConnected() && cadenaBusqueda.length() != 0){
            new FetchBook(txtLibro, txtAutor).execute(cadenaBusqueda);
            txtAutor.setText("");
            txtLibro.setText(R.string.cargando);
        }else{
            if(cadenaBusqueda.length() == 0){
                txtAutor.setText("");
                txtLibro.setText(R.string.sin_texto);
            }else{
                txtAutor.setText("");
                txtLibro.setText(R.string.sin_conexion);
            }
        }

    }


}