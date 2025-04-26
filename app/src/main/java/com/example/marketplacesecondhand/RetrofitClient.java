package com.example.marketplacesecondhand;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL_REAL_MACHINE = "http://192.168.1.20:8080/marketplace/";
    private static final String BASE_URL_VIRTUAL_MACHINE = "http://10.0.2.2:8080/marketplace/";
    private static Retrofit retrofit;
    public static Retrofit getRetrofit(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_VIRTUAL_MACHINE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
