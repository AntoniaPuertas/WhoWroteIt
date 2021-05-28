package com.toni.whowroteit;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM = "q";
    private static final String MAX_RESULTS = "maxResults";
    private static final String PRINT_TYPE = "printType";

    public static String getBookInfo(String cadenaBusqueda){
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJonsonString = null;

        try {
            //crea la uri de la solicitud
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, cadenaBusqueda)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();
            //crea la url a partir de la uri
            URL requestURL = new URL(builtURI.toString());

            //realiza la conexion
            urlConnection = (HttpsURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //respuesta
            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            //lee la respuesta linea a linea
            String linea;
            while((linea = reader.readLine()) != null){
                builder.append(linea);
                builder.append("\n");
            }

            //comprueba si hay respuesta
            if(builder.length() == 0){
                return null;
            }

            //convierte el stringBuilder en string
            bookJonsonString = builder.toString();

        }catch (IOException e){
            e.printStackTrace();
        }finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(LOG_TAG, bookJonsonString);
        return bookJonsonString;
    }
}
