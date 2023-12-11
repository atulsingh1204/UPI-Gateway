package com.upigateway.payment;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAPI {
    @POST("create_order")
    Call<ResponseData> createOrder(@Body DataModel dataModel);
}
