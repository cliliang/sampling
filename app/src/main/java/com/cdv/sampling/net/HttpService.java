package com.cdv.sampling.net;

import android.text.TextUtils;

import com.cdv.sampling.BuildConfig;
import com.cdv.sampling.constants.StorageConstants;
import com.cdv.sampling.repository.UserRepository;
import com.cdv.sampling.storage.SamplingStorage;
import com.cdv.sampling.utils.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by apple on 16/3/17.
 */
public class HttpService {

    public static String DEFAULT_HOST_URL = "http://60.205.151.145:8088/";

    public static int DEFAULT_CACHE_MAX_SECONDS = 14 * 24 * 60 * 60;
    private SamplingHttpApi samplingCommonApi;
    private Gson forceChangeToString = null;

    private HttpService() {
        DEFAULT_HOST_URL = SamplingStorage.getInstance().getStringValue(StorageConstants.KEY_IP_CONFIG, DEFAULT_HOST_URL);
        forceChangeToString = new GsonBuilder().registerTypeAdapter(String.class, new JsonDeserializer<String>() {

            @Override
            public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json.isJsonArray() || json.isJsonObject()) {
                    return json.toString();
                } else {
                    return json.getAsString();
                }
            }
        }).registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                String date = json.getAsString();
                if (TextUtils.isEmpty(date)){
                    return null;
                }
                return TimeUtils.parseDate(date, TimeUtils.DEFAULT_DATE_FORMAT);
            }
        }).create();
        Retrofit.Builder builder = new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(forceChangeToString))
                .baseUrl(DEFAULT_HOST_URL);
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = SamplingHttpClient.addCommonHeaderToRequest(chain.request());
                Response response = chain.proceed(request);
                if (TextUtils.isEmpty(UserRepository.getInstance().getToken())){
                    String token = response.header("token");
                    UserRepository.getInstance().setToken(token);
                }
                return response;
            }
        });

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(loggingInterceptor);
            DEFAULT_CACHE_MAX_SECONDS = 60;
        }

        OkHttpClient client = okHttpBuilder.build();
        builder.client(client);
        samplingCommonApi = builder.build().create(SamplingHttpApi.class);
    }

    public static HttpService getInstance() {
        return EPocketHttpServiceHandler.INSTANCE;
    }

    private static class EPocketHttpServiceHandler {
        public static final HttpService INSTANCE = new HttpService();
    }

    public static SamplingHttpApi getApi() {
        return HttpService.getInstance().samplingCommonApi;
    }
}