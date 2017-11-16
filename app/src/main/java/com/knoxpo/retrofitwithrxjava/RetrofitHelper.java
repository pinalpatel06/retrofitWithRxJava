package com.knoxpo.retrofitwithrxjava;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by knoxpo on 24/8/17.
 */

public class RetrofitHelper {

    public RetrofitInterface getService() {
        final Retrofit retrofit = createRetrofit();
        return retrofit.create(RetrofitInterface.class);
    }

    public RetrofitInterface getWeatherService(){
        final Retrofit retrofit = createWeatherRetrofit();
        return retrofit.create(RetrofitInterface.class);
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // <- add this
                .client(new OkHttpClient())
                .build();
    }


    private Retrofit createWeatherRetrofit(){
        return new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/forecast/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient())
                .build();

    }
}
