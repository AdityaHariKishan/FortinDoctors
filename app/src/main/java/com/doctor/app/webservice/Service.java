package com.doctor.app.webservice;

import android.content.Context;
import android.os.Environment;

import com.doctor.app.helper.AppConst;
import com.doctor.app.helper.ConnectionDetector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {
    public Service() {

    }
    private static Context mContext;

    protected static Retrofit retrofit;
    public static <S> S createService(Context context, Class<S> serviceClass) {

        mContext= context;


        File httpCacheDirectory = new File(Environment.getExternalStorageDirectory(), "HttpCache");// Here to facilitate the file directly on the SD Kagan catalog HttpCache in ， Generally put in context.getCacheDir() in
        int cacheSize = 20 * 1024 * 1024;// Set cache file size 10M
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
         OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)// Set connection timeout
                .readTimeout(10, TimeUnit.SECONDS)// Read timeout
                .writeTimeout(10, TimeUnit.SECONDS)// Write timeout
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)// Add a custom cache interceptor （ Explain later ）， Note that it needs to be used here. .addNetworkInterceptor
                .cache(cache)// Add cache
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit=new Retrofit.Builder()
                .baseUrl(AppConst.MAIN)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(serviceClass);
    }

    static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            Response response = chain.proceed(request);
            ConnectionDetector connectionDetector=new ConnectionDetector(mContext);

            if (connectionDetector.isConnectingToInternet()) {
                // Get header information
                String cacheControl =request.cacheControl().toString();
                return response.newBuilder()
                        .removeHeader("Pragma")// Clear header information ， Because server if not supported ， Will return some interference information ， Does not clear the following can not be effective
                        .header("Cache-Control", cacheControl)
                        .build();
            }
            return response;
        }
    };
}
