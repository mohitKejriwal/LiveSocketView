package com.india.android.websocketio;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by admin on 02-09-2018.
 */

public class RestClient {
    private static RestClient restClient=new RestClient();
    public static ApiService apiService;

    private RestClient(){
        Gson gson=new Gson();
        OkHttpClient.Builder okHttpBuilder=new OkHttpClient.Builder();
        long TIME_OUT = 45L;
        okHttpBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS).readTimeout(TIME_OUT, TimeUnit.SECONDS).writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        String BASE_URL=" http://kaboom.rksv.net/api/";

        OkHttpClient okHttpClient=okHttpBuilder.addInterceptor(interceptor).addInterceptor(new HeaderInterceptor()).build();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build();

        apiService=retrofit.create(ApiService.class);
    }

    class HeaderInterceptor implements Interceptor{

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request=chain.request();
            request.newBuilder().addHeader("Accept-Encoding","application/gzip").build();
            return chain.proceed(request);
        }
    }
}
