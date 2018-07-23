package com.chzheng.airmen.networking;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chzheng.airmen.BombardierActivity;
import com.chzheng.airmen.LobbyActivity;
import com.chzheng.airmen.LobbyOwnerActivity;
import com.chzheng.airmen.NavigatorActivity;
import com.chzheng.airmen.PilotActivity;
import com.chzheng.airmen.SignallerActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

//Listens for updates from the server, then forwards them to the appropriate activities
public class ClientListener implements Runnable {
    private static final String TAG = "ClientListener";
    private Socket mClientSocket;

    public ClientListener(Socket clientSocket) {
        mClientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (ObjectInputStream fromServerStream = new ObjectInputStream(mClientSocket.getInputStream())) {
            while(!Thread.interrupted()) {
                final Object input = fromServerStream.readObject();
                if (input != null) sendToActivities(input);
            }
        } catch (IOException|ClassNotFoundException e) {
            Log.e(TAG, e.getMessage() != null ? e.getMessage() : "Unknown error", e);
            //TODO: Notify activities of server shutdown
        }
    }

    private void sendToActivities(Object input) {
        Handler[] activityHandlers = {
                LobbyActivity.sHandler,
                LobbyOwnerActivity.sHandler,
                PilotActivity.sHandler,
                BombardierActivity.sHandler,
                NavigatorActivity.sHandler,
                SignallerActivity.sHandler
        };
        for (Handler handler : activityHandlers) {
            Message message = new Message();
            message.obj = input;
            handler.sendMessage(message);
        }
    }
}
