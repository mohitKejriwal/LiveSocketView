package com.india.android.websocketio;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by admin on 02-09-2018.
 */

public interface ApiService {
    @GET("historical")
    Call<ArrayList<String>> getHistoricStocks(@Query("interval") String interval);
}
