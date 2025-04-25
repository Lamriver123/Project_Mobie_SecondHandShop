package com.example.marketplacesecondhand;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    public static Retrofit getRetrofit(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://196.169.3.176:8080/marketplace/")
                //    .baseUrl("http://192.168.1.14:8080/marketplace/")
                 //   .baseUrl("http://10.0.2.2:8080/marketplace/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
