package com.knoxpo.retrofitwithrxjava;

import com.google.gson.annotations.SerializedName;

/**
 * Created by knoxpo on 24/8/17.
 */

public class Model {

    @SerializedName("userId")
    private long mUserId;
    @SerializedName("id")
    private long mId;
    @SerializedName("title")
    private String mTitle;

    public long getUserId() {
        return mUserId;
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return body;
    }

    @SerializedName("body")
    private String body;
}
