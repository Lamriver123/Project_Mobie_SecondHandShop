package com.example.marketplacesecondhand;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static String currentToken = "";
    private static final String BASE_URL_REAL_MACHINE = "http://196.169.6.1:8080/marketplace/";
    private static final String BASE_URL_VIRTUAL_MACHINE = "http://10.0.2.2:8080/marketplace/";
    public static Retrofit retrofit;
    public static Retrofit getRetrofit(){
        if(retrofit == null){
            if (currentToken.isEmpty()) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL_REAL_MACHINE)
                        .addConverterFactory(GsonConverterFactory.create())
                        //   .client(client)
                        .build();
            }
            else {
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(new AuthInterceptor())// Thêm Interceptor vào Retrofit
                        .connectTimeout(20, TimeUnit.SECONDS) // thời gian chờ kết nối
                        .readTimeout(20, TimeUnit.SECONDS)    // thời gian chờ đọc dữ liệu
                        .writeTimeout(20, TimeUnit.SECONDS)   // thời gian chờ ghi dữ liệu (nếu có)
                        .build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL_REAL_MACHINE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();
            }

        }
        return retrofit;
    }
}
