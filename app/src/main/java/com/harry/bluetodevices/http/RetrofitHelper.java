package com.harry.bluetodevices.http;

import android.util.Log;

import com.harry.bluetodevices.util.AppUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Martin-harry
 * @date 2021/11/29
 * @address
 * @Desc RetrofitHelper
 */
public class RetrofitHelper {
    private String TAG = "RetrofitHelper";

    private OkHttpClient interceptor;
    GsonConverterFactory factory = GsonConverterFactory.create();
    private static RetrofitHelper instance = null;
    private Retrofit mRetrofit = null;

    //单例模式
    public static RetrofitHelper getInstance() {
        if (instance == null) {
            instance = new RetrofitHelper();
        }
        return instance;
    }

    private RetrofitHelper() {
        //实例化OKhttpClient
        interceptor = new OkHttpClient.Builder()
                .addInterceptor(intercept)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        //初始化retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(MyApi.baseUrl)
                .client(interceptor)
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public RetrofitService getServer() {
        return mRetrofit.create(RetrofitService.class);
    }

    /**
     * 请求访问quest
     * response拦截器
     */
    private Interceptor intercept = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Log.e(TAG, "----------Request Start----------------");
            Log.e(TAG, "| " + request.toString() + request.headers().toString());
            long startTime = System.currentTimeMillis();
            Response response = chain.proceed(chain.request());
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            Log.e(TAG, "| Response:" + AppUtils.unicodeToUTF_8(content));
            Log.e(TAG, "----------Request End:" + duration + "毫秒----------");
            return response.newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build();
        }
    };
}
