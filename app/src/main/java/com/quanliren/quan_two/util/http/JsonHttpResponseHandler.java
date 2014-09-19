package com.quanliren.quan_two.util.http;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by BingBing on 2014/9/17.
 */
public class JsonHttpResponseHandler extends com.loopj.android.http.JsonHttpResponseHandler {

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        onSuccess(response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        onFailure();
    }

    public void onSuccess(JSONObject response) {

    }

    public void onFailure() {

    }
}
