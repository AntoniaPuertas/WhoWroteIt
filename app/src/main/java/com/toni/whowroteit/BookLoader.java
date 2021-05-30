package com.toni.whowroteit;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class BookLoader extends AsyncTaskLoader<String> {

    private String consulta;

    public BookLoader(Context context, String consulta) {
        super(context);
        this.consulta = consulta;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return NetworkUtils.getBookInfo(consulta);
    }
}
