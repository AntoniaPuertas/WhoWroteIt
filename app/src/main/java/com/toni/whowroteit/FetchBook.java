package com.toni.whowroteit;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;

public class FetchBook extends AsyncTask<String, Void, String> {

private WeakReference<TextView> txtTitulo;
private WeakReference<TextView> txtAutor;

    public FetchBook(TextView txtTitulo, TextView txtAutor) {
        this.txtTitulo = new WeakReference<>(txtTitulo);
        this.txtAutor = new WeakReference<>(txtAutor);
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            //convierte el string en un objeto json
            JSONObject jsonObject = new JSONObject(s);
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
                txtTitulo.get().setText(titulo);
                txtAutor.get().setText(autor);
            }else{
                txtTitulo.get().setText(R.string.sin_resultados);
                txtAutor.get().setText("");
            }

        }catch(JSONException e){
            txtTitulo.get().setText(R.string.sin_resultados);
            txtAutor.get().setText("");
            e.printStackTrace();
        }
    }
}
