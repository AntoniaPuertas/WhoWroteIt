package com.toni.whowroteit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    EditText inputLibro;
    TextView txtLibro;
    TextView txtAutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if(getSupportLoaderManager().getLoader(0)!=null){
//            getSupportLoaderManager().initLoader(0,null,this);
//        }

        if(getLoaderManager().getLoader(0) != null){
            LoaderManager.getInstance(this).initLoader(0, null, this);
        }


        inputLibro = findViewById(R.id.inputLibro);
        txtLibro = findViewById(R.id.txtLibro);
        txtAutor = findViewById(R.id.txtAutor);

        if(savedInstanceState != null){
            inputLibro.setText(savedInstanceState.getString("input"));
            txtLibro.setText(savedInstanceState.getString("libro"));
            txtAutor.setText(savedInstanceState.getString("autor"));
        }
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
            //new FetchBook(txtLibro, txtAutor).execute(cadenaBusqueda);
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", cadenaBusqueda);
            //getSupportLoaderManager().restartLoader(0, queryBundle, this);
            LoaderManager.getInstance(this).initLoader(0, queryBundle, this);
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


    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id,  Bundle args) {
        String queryString = "";

        if (args != null) {
            queryString = args.getString("queryString");
        }

        return new BookLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            //convierte el string en un objeto json
            JSONObject jsonObject = new JSONObject(data);
            //convierte el objeto json en un array
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            int i = 0;
            String titulo = null;
            String autor = null;
            //busca en el array las entradas con titulo y autor
            while (i < itemsArray.length() && autor == null && titulo == null){
                JSONObject libro = itemsArray.getJSONObject(i);
                JSONObject volumenInfo = libro.getJSONObject("volumeInfo");
                JSONArray autores = volumenInfo.getJSONArray("authors");
                //comprueba si existe titulo y autor en este item
                try {
                    titulo = volumenInfo.getString("title");
                    autor = autores.getString(0);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //actualiza el contador al siguiente item
                i++;
            }
            if(titulo != null && autor != null){
                txtLibro.setText(titulo);
                txtAutor.setText(autor);
            }else{
                txtLibro.setText(R.string.sin_resultados);
                txtAutor.setText("");
            }

        }catch(JSONException e){
            txtLibro.setText(R.string.sin_resultados);
            txtAutor.setText("");
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("input", inputLibro.getText().toString());
        bundle.putString("libro", txtLibro.getText().toString());
        bundle.putString("autor", txtAutor.getText().toString());

    }
}