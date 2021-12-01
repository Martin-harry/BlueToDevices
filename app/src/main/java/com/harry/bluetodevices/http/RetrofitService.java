package com.harry.bluetodevices.http;

import com.harry.bluetodevices.bean.JsonData;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @author Martin-harry
 * @date 2021/11/29
 * @address
 * @Desc retrofitService
 */
public interface RetrofitService {
    //动态血糖上传数据
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(MyApi.BLOOD_DATE)
    Observable<JsonData> getSugar(@Body RequestBody info);
}
