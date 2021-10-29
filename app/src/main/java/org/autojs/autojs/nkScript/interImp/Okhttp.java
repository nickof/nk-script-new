package org.autojs.autojs.nkScript.interImp;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Okhttp {

    private static final String TAG = "nkScritp-Okhttp";
    private volatile static OkHttpClient okHttpClient;
    private volatile static Request request;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient==null){
            okHttpClient=new OkHttpClient.Builder().build();
        }
        return okHttpClient;
    }

    public static String get(String url)   {

        getOkHttpClient();
        request=new Request.Builder().url(url).build();
        Response response = null;

        try {
            response = okHttpClient.newCall( request).execute();
            String ret=response.body().string();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "get: exception="+e.toString()  );
            return null;
        }

    }

}
