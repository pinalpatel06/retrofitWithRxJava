package com.knoxpo.retrofitwithrxjava;

/**
 * Created by knoxpo on 24/8/17.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knoxpo on 23/8/17.
 */

public class Weather {

    @SerializedName("city")
    private City mData;

    public class City {
        public String getId() {
            return mId;
        }

        public String getCityName() {
            return mCityName;
        }

        public String getCountry() {
            return mCountry;
        }

        public String getPopulation() {
            return mPopulation;
        }

        @SerializedName("id")
        private String mId;

        @SerializedName("name")
        private String mCityName;

        @SerializedName("coord")
        private Coordinate mCoord;

        @SerializedName("country")
        private String mCountry;

        @SerializedName("population")
        private String mPopulation;

        class Coordinate {
            @SerializedName("lat")
            private double mLat;
            @SerializedName("lon")
            private double mLng;
        }
    }

    class WeatherData {
        @SerializedName("dt")
        private long mDate;

        public long getDate() {
            return mDate;
        }

        public Temp getTemp() {
            return temp;
        }

        @SerializedName("temp")
        private Temp temp;

        class Temp {
            public float getMin() {
                return mMin;
            }

            public float getMax() {
                return mMax;
            }

            @SerializedName("min")
            private float mMin;

            @SerializedName("max")
            private float mMax;


        }

        public String toString() {
            return "Max : " + temp.mMax + " Min : " + temp.mMin;
        }
    }

    public String toString() {
        return mData.getCityName() + " " + mData.getCountry();
    }
}
