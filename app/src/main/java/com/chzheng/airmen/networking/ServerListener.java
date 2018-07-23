package com.chzheng.airmen.networking;

import android.os.Message;
import android.util.Log;

import com.chzheng.airmen.memos.ServerMemo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

//Listens for client updates, then forwards them to the main Server thread
public class ServerListener implements Runnable {
    private static final String TAG = "Server listener";
    private Socket mClientSocket;

    public ServerListener(Socket clientSocket) {
        this.mClientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (ObjectInputStream fromClientStream = new ObjectInputStream(mClientSocket.getInputStream())) {
            while (!Thread.interrupted()) {
                Object input = fromClientStream.readObject();
                if (input != null) {
                    Message message = new Message();
                    message.obj = input;
                    Server.sHandler.sendMessage(message);
                }
            }
        } catch (IOException|ClassNotFoundException e) {
            Log.e(TAG, e.getMessage() != null ? e.getMessage() : "Unknown error", e);
            Message message = new Message();
            message.obj = new ServerMemo(ServerMemo.Action.DISCONNECT, mClientSocket.getInetAddress());
            Server.sHandler.sendMessage(message);
        }
    }
}
