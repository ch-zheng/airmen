package com.chzheng.airmen.networking;

import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.chzheng.airmen.ClientActivity;
import com.chzheng.airmen.OwnerActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Client implements Runnable {
    private static final String TAG = "Client";
    private InetAddress mAddress;
    private int mPort;

    public Client(InetAddress address, int port) {
        mAddress = address;
        mPort = port;
    }

    @Override
    public void run() {
        Log.i(TAG, "New client created");
        try (Socket clientSocket = new Socket(mAddress, mPort)) {
            //Start client threads
            final Thread clientListener = new Thread(new ClientListener(clientSocket));
            final HandlerThread clientSender = new ClientSender(clientSocket);
            clientListener.start();
            clientSender.start();
            //Wait for threads to close
            while (clientListener.isAlive() && clientSender.isAlive()) sleep(2000);
            //Make sure both threads close
            clientListener.interrupt();
            clientSender.quitSafely();
        } catch (IOException|InterruptedException e) {
            Log.e(TAG, e.getMessage() != null ? e.getMessage() : "Unknown error", e);
            Message message = Message.obtain();
            message.obj = e;
            OwnerActivity.sHandler.sendMessage(message);
            Message message2 = Message.obtain();
            message2.obj = e;
            ClientActivity.sHandler.sendMessage(message2);
        } finally {
            Log.i(TAG, "Shutdown");
        }
    }
}
