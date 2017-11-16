package com.knoxpo.retrofitwithrxjava;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by knoxpo on 24/8/17.
 */

public interface RetrofitInterface {
    @GET("posts/{post_id}")
    Single<Model> queryModel(@Path("post_id") String id);

    @GET("posts")
    Single<List<Model>> listAllModel();

    @GET("daily?q=surat&mode=JSON&units=matrics&cnt=7&APPID=c1c67bcc13f19d7471eef2c9487b6993")
    Single<ModelResponse> listWeatherData();

    @GET("daily?q=surat&mode=JSON&units=matrics&cnt=7")
    Observable<Weather> getWeather(@Query("APPID") String id);
}
