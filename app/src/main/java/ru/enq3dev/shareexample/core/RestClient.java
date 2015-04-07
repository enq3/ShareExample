package ru.enq3dev.shareexample.core;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestClient {
    private static int CONNECTION_TIMEOUT = 15000;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(CONNECTION_TIMEOUT);
        client.get(url, params, responseHandler);
    }
}
