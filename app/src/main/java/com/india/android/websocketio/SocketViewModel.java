package com.india.android.websocketio;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.os.Looper;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 02-09-2018.
 */

public class SocketViewModel extends ViewModel {
    private MutableLiveData<ArrayList<String>> pastStockData;
    private MutableLiveData<String[]> liveStockData;

    private Socket mSocket;
    private Emitter.Listener onDataReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            liveStockData.postValue(args[0].toString().split(","));
            final Ack ack = (Ack) args[args.length - 1];
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ack.call(1);
                }
            }, 5000);                                           //Acknowledgement sent after 5 seconds to reduce the ratio of updates/time

        }
    };
    private Emitter.Listener onErrorReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.print(args[0]);
        }
    };

    public SocketViewModel() {
        pastStockData =new MutableLiveData<>();
        liveStockData = new MutableLiveData<>();
        loadPastData();
        {
            try {
                mSocket = IO.socket("http://kaboom.rksv.net/watch");          //Initialize connection
            } catch (URISyntaxException e) {
            }
        }
    }

    LiveData<ArrayList<String>> getPastStockData(){
        if (pastStockData !=null){
            return pastStockData;
        }
        pastStockData =new MutableLiveData<>();
        return pastStockData;
    }

    LiveData<String[]> getLiveStockData(){
        if (liveStockData !=null){
            return liveStockData;
        }
        liveStockData =new MutableLiveData<>();
        return liveStockData;
    }

    private void loadPastData(){
        //Historic data for Graph
        RestClient.apiService.getHistoricStocks("1").enqueue(new Callback< ArrayList<String>>(){
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                pastStockData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
            }
        });
    }

    void connectSocket(){
        if (mSocket!=null){
            if (!mSocket.connected()){
                mSocket.connect();
            }
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("state",true);
                mSocket.emit("sub",jsonObject);                              //Subscribe for data updates
                mSocket.on("data", onDataReceive);                           //Listener for data updates
                mSocket.on("error", onErrorReceive);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    void disconnectSocket(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("state",false);
        mSocket.emit("unsub",jsonObject);                                        //Un subscribe from server and remove listeners after that
        mSocket.off("data", onDataReceive);
        mSocket.off("error", onErrorReceive);
        mSocket.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}