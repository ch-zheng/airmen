package com.chzheng.airmen.networking;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

//Listens for updates from the UI, then forwards them to the Server
public class ClientSender extends HandlerThread {
    private static final String TAG = "Client sender";
    private Socket mClientSocket;
    private ObjectOutputStream toServerStream;
    public static Handler sHandler;

    public ClientSender(Socket clientSocket) {
        super(TAG);
        mClientSocket = clientSocket;
    }

    @Override
    protected void onLooperPrepared() {
        //Establish output stream to server
        try {toServerStream = new ObjectOutputStream(mClientSocket.getOutputStream());}
        catch (IOException e) {Log.e(TAG, e.getMessage(), e);}
        //Register Handler
        sHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //Forward message to the server
                if (msg.obj instanceof Serializable) {
                    try { toServerStream.writeObject(msg.obj); }
                    catch (IOException e) {Log.e(TAG, e.getMessage(), e);}
                }
                return false;
            }
        });
    }

    @Override
    public boolean quitSafely() {
        try { toServerStream.close(); }
        catch (IOException e) {Log.e(TAG, e.getMessage(), e);}
        return super.quitSafely();
    }
}
