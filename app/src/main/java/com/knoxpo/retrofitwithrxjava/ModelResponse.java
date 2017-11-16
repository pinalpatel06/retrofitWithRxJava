package com.knoxpo.retrofitwithrxjava;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knoxpo on 24/8/17.
 */

public class ModelResponse {
    @SerializedName("list")
    public List<Weather.WeatherData> mWeatherDataList = new ArrayList<>();
}
