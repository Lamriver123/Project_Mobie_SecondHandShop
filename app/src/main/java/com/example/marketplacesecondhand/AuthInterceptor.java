package com.example.marketplacesecondhand;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor{

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request newRequest = original.newBuilder()
                .header("Authorization", "Bearer " + RetrofitClient.currentToken)
                .build();
        return chain.proceed(newRequest);
    }
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//        Request originalRequest = chain.request();
//
//        // Chỉ thêm Authorization nếu token hợp lệ (không null, không rỗng)
//        if (RetrofitClient.currentToken != null && !RetrofitClient.currentToken.isEmpty()) {
//            Request newRequest = originalRequest.newBuilder()
//                    .header("Authorization", "Bearer " + RetrofitClient.currentToken)
//                    .build();
//            return chain.proceed(newRequest);
//        }
//
//        // Nếu chưa có token, dùng request gốc
//        return chain.proceed(originalRequest);
//    }


}
