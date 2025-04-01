package com.example.publictransportapp.api;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.Request;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    // 确认API地址
    private static final String BASE_URL = "https://data.etabus.gov.hk/";
    private static Retrofit retrofit;
    private static KmbApi api;

    public static KmbApi getApi() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                                .header("Accept", "application/json, text/plain, */*")
                                .header("Accept-Language", "en-US,en;q=0.9")
                                .header("Origin", "https://data.etabus.gov.hk")
                                .header("Referer", "https://data.etabus.gov.hk/")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api = retrofit.create(KmbApi.class);
        }
        return api;
    }

    // 如果主API失败，可以尝试使用备用API
    public static KmbApi getBackupApi() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("User-Agent", "ShortPath/1.0 Android App")
                            .header("Accept", "application/json")
                            .header("Referer", "https://rt.data.gov.hk/")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit backupRetrofit = new Retrofit.Builder()
//                .baseUrl(BACKUP_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return backupRetrofit.create(KmbApi.class);
    }
}
