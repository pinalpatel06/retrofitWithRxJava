package com.knoxpo.retrofitwithrxjava;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.HttpException;
import retrofit2.Response;


/**
 * Created by knoxpo on 24/8/17.
 */

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private RetrofitInterface mRetrofitInterface;
    private static final int UNCHECKED_ERROR_TYPE_CODE = -100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mRetrofitInterface = new RetrofitHelper().getService();
        mRetrofitInterface = new RetrofitHelper().getWeatherService();

        //request();

        //listAll();

        listAllTitles();

        getWeather();

    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    private void request() {
        mCompositeDisposable.add(mRetrofitInterface.queryModel("1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Model>() {
                    @Override
                    public void accept(Model model) throws Exception {
                        Log.d(TAG, "Request fetched " + model.getTitle());
                    }
                }));
    }

    private void listAll() {
        mCompositeDisposable.add(mRetrofitInterface.listAllModel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Model>>() {
                    @Override
                    public void accept(List<Model> models) throws Exception {
                        Log.d(TAG, "Request " + models.size());
                    }
                })
        );
    }

    private int mCounter = 0;
    private static final int ATTEMPTS = 3;

    private void listAllTitles() {
        mCompositeDisposable.add(mRetrofitInterface.listWeatherData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .map(new Function<ModelResponse, List<Weather.WeatherData>>() {
                    @Override
                    public List<Weather.WeatherData> apply(ModelResponse modelResponse) throws Exception {
                        return modelResponse.mWeatherDataList;
                    }
                })
                .filter(new Predicate<List<Weather.WeatherData>>() {
                    @Override
                    public boolean test(@NonNull List<Weather.WeatherData> weatherDatas) throws Exception {
                        Calendar c = Calendar.getInstance();
                        return weatherDatas.get(0).getDate() > c.getTimeInMillis();
                    }
                })
                .subscribe(new Consumer<List<Weather.WeatherData>>() {
                    @Override
                    public void accept(List<Weather.WeatherData> weatherDatas) throws Exception {
                        Log.d(TAG, weatherDatas.size() + " ");
                        for (int i = 0; i < weatherDatas.size(); i++) {
                            Log.d(TAG, weatherDatas.get(i).toString() + " ");
                        }
                    }
                })
        );
    }

    private void getWeather() {

       /*// Single<Weather> single = mRetrofitInterface.getWeather("c1c67bcc13f19d74");
        mCompositeDisposable.add(single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new Function<Flowable<Throwable>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(@NonNull Flowable<Throwable> throwableFlowable) throws Exception {
                        return exponentialBackoffForExceptions(throwableFlowable, 2, 3, TimeUnit.SECONDS, HttpException.class);
                    }
                })
                .subscribe(new Consumer<Weather>() {
                               @Override
                               public void accept(@NonNull Weather weather) throws Exception {
                                   Log.d(TAG, weather.toString());
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Log.d(TAG,throwable.getMessage());
                            }
                        }
                )
        );*/


        //Single<Weather> single = mRetrofitInterface.getWeather("c1c67bcc13f19d74");
        mCompositeDisposable.add(mRetrofitInterface.getWeather("c1c67bcc13f19d74")
        .retryWhen(
                new Function<Observable<Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> apply(@NonNull Observable<Throwable> throwableFlowable) throws Exception {
                        return exponentialBackoffForExceptions(throwableFlowable, 2, 3, TimeUnit.SECONDS, HttpException.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Weather>() {
                    @Override
                    public void accept(@NonNull Weather weather) throws Exception {
                        Log.d(TAG, weather.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.d(TAG,throwable.getMessage());
                    }
                })
        );
    }

    public static Observable<?> exponentialBackoffForExceptions(Observable<Throwable> errors, final long delay, final int retries, final TimeUnit timeUnit, final Class<? extends Throwable>... errorTypes) {
        if (delay <= 0) {
            throw new IllegalArgumentException("delay must be greater than 0");
        }

        if (retries <= 0) {
            throw new IllegalArgumentException("retries must be greater than 0");
        }

        Observable<Pair<Throwable, Integer>> flowable = errors.zipWith(Observable.range(1, retries + 1), new BiFunction<Throwable, Integer, Pair<Throwable, Integer>>() {
            @Override
            public Pair<Throwable, Integer> apply(@NonNull Throwable error, @NonNull Integer integer) {
                try {
                    Log.d(TAG, "Retry count: " + integer);

                    Pair<Throwable, Integer> errorPair = null;
                    if (integer == retries + 1) {
                        errorPair = new Pair<>(error, UNCHECKED_ERROR_TYPE_CODE);
                    }

                    if (errorTypes != null && errorPair == null) {
                        for (Class<? extends Throwable> clazz : errorTypes) {
                            if (clazz.isInstance(error)) {
                                // Mark as error type found
                                errorPair = new Pair<>(error, integer);
                                break;
                            }
                        }
                    }

                    if (errorPair == null) {
                        errorPair = new Pair<>(error, UNCHECKED_ERROR_TYPE_CODE);
                    }

                    return errorPair;
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                    return null;
                }
            }
        });

        Observable flatFlowable = flowable.flatMap(new Function<Pair<Throwable, Integer>, Observable<?>>() {
            @Override
            public Observable<?> apply(@NonNull Pair<Throwable, Integer> errorRetryCountTuple) throws Exception {
                int retryAttempt = errorRetryCountTuple.second;

                // If not a known error type, pass the error through.
                if (retryAttempt == UNCHECKED_ERROR_TYPE_CODE) {
                    return Observable.error(errorRetryCountTuple.first);
                }

                long d = (long) Math.pow(delay, retryAttempt);

                Log.d(TAG, "Retry after: " + d);

                // Else, exponential backoff for the passed in error types.
                return Observable.timer(d, timeUnit);
            }
        });

        return flatFlowable;
    }


    /*public static Flowable<?> exponentialBackoffForExceptions(Flowable<Throwable> errors, final long delay, final int retries, final TimeUnit timeUnit, final Class<? extends Throwable>... errorTypes) {
        if (delay <= 0) {
            throw new IllegalArgumentException("delay must be greater than 0");
        }

        if (retries <= 0) {
            throw new IllegalArgumentException("retries must be greater than 0");
        }

        Flowable<Pair<Throwable, Integer>> flowable = errors.zipWith(Flowable.range(1, retries + 1), new BiFunction<Throwable, Integer, Pair<Throwable, Integer>>() {
            @Override
            public Pair<Throwable, Integer> apply(@NonNull Throwable error, @NonNull Integer integer) throws Exception {

                Log.d(TAG, "Retry count: " + integer);

                Pair<Throwable, Integer> errorPair = null;
                if (integer == retries + 1) {
                    errorPair = new Pair<>(error, UNCHECKED_ERROR_TYPE_CODE);
                }

                if (errorTypes != null && errorPair == null) {
                    for (Class<? extends Throwable> clazz : errorTypes) {
                        if (clazz.isInstance(error)) {
                            // Mark as error type found
                            errorPair = new Pair<>(error, integer);
                            break;
                        }
                    }
                }

                if (errorPair == null) {
                    errorPair = new Pair<>(error, UNCHECKED_ERROR_TYPE_CODE);
                }

                return errorPair;
            }
        });

        Flowable flatFlowable = flowable.flatMap(new Function<Pair<Throwable, Integer>, Publisher<?>>() {
            @Override
            public Publisher<?> apply(@NonNull Pair<Throwable, Integer> errorRetryCountTuple) throws Exception {
                int retryAttempt = errorRetryCountTuple.second;

                // If not a known error type, pass the error through.
                if (retryAttempt == UNCHECKED_ERROR_TYPE_CODE) {
                    return Flowable.error(errorRetryCountTuple.first);
                }

                long d = (long) Math.pow(delay, retryAttempt);

                Log.d(TAG, "Retry after: " + d);

                // Else, exponential backoff for the passed in error types.
                return Flowable.timer(d, timeUnit);
            }
        });

        return flatFlowable;
    }*/
}
