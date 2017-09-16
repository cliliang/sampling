package com.cdv.sampling.net;

import android.text.TextUtils;

import com.cdv.sampling.BuildConfig;
import com.cdv.sampling.repository.UserRepository;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by apple on 16/3/18.
 */
public class SamplingHttpClient {

    private OkHttpClient defaultOkHttpClient;

    private HttpLoggingInterceptor debugLoggingInterceptor;

    private SamplingHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS);
        debugLoggingInterceptor = new HttpLoggingInterceptor();
        debugLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (BuildConfig.DEBUG){
            builder.addInterceptor(debugLoggingInterceptor);
        }
        defaultOkHttpClient = builder.build();
    }

    public static SamplingHttpClient getInstance() {
        return CdvHttpClientHandler.INSTANCE;
    }

    private static class CdvHttpClientHandler {
        public static final SamplingHttpClient INSTANCE = new SamplingHttpClient();
    }

    public OkHttpClient getDefaultOkHttpClient() {
        return defaultOkHttpClient;
    }

    public Observable<Response> sendGetRequest(final String url){
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    Request request = new Request.Builder().url(url).get().build();
                    Call call = defaultOkHttpClient.newCall(request);
                    Response response = call.execute();
                    subscriber.onNext(response);
                } catch (IOException e) {
                    subscriber.onError(e);
                }finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    public Observable<Response> sendGetRequest(final String url, final Headers headers){
        if (headers == null){
            return sendGetRequest(url);
        }
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    Request request = new Request.Builder().url(url).headers(headers).get().build();
                    Call call = defaultOkHttpClient.newCall(request);
                    Response response = call.execute();
                    subscriber.onNext(response);
                } catch (IOException e) {
                    subscriber.onError(e);
                }finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static Request addCommonHeaderToRequest(Request request){
        Request.Builder builder = request.newBuilder();
        String token = UserRepository.getInstance().getToken();
        if (!TextUtils.isEmpty(token)){
            builder.addHeader("token", token);
        }
        return builder.build();
    }
}
