package com.nhomIOT.outputIotApp;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainViewModel extends ViewModel {

    private WebSocket webSocket;
    private final OkHttpClient client = new OkHttpClient();
    private final MutableLiveData<String> message = new MutableLiveData<>("");
    private final MutableLiveData<String> status = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>(false);

    public MainViewModel() {
        initWebSocket();
    }

    public void reconnect() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
        initWebSocket();
    }

    private void initWebSocket() {
        Request request = new Request.Builder()
                .url("ws://192.168.4.1:80")
                .build();

        webSocket = client.newWebSocket(request, webSocketListener);
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<String> getStatus() {
        return status;
    }

    public LiveData<Boolean> isConnected() {
        return isConnected;
    }


    private final WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            // Xử lý khi có kết nối WebSocket mới
            Log.d("WebSocket", "Kết nối thành công");
            status.postValue("Kết nối thành công");
            isConnected.postValue(true);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            // Xử lý khi nhận được tin nhắn dạng văn bản từ ESP8266 server
            Log.d("WebSocket", "Received message string: " + text);
            if (!message.getValue().equals(text)) {
                message.postValue(text);
            }


        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            // Xử lý khi nhận được dữ liệu nhị phân từ ESP8266 server
            Log.d("WebSocket", "Received message byte: " + bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            // Xử lý khi kết nối WebSocket đang đóng
            Log.d("WebSocket", "Xử lý khi kết nối WebSocket đang đóng" );
            status.postValue("WebSocket đang đóng");
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            // Xử lý khi kết nối WebSocket đã đóng
            Log.d("WebSocket", "Xử lý khi kết nối WebSocket đã đóng" );
            status.postValue("WebSocket đã đóng");
            isConnected.postValue(false);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            // Xử lý khi có lỗi xảy ra
            Log.d("WebSocket",t.getMessage() );
            status.postValue("Lỗi kết nối WebSocket");
            isConnected.postValue(false);


        }
    };

    public void closeSocket() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }


}
