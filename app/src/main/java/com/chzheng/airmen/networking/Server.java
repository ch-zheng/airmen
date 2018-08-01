package com.chzheng.airmen.networking;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.chzheng.airmen.game.Game;
import com.chzheng.airmen.game.Player;
import com.chzheng.airmen.memos.BombardierMemo;
import com.chzheng.airmen.memos.PilotMemo;
import com.chzheng.airmen.memos.ServerMemo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Server implements Runnable {
    private static final String TAG = "Server";
    public static Handler sHandler;
    private HandlerThread mHandlerThread = new HandlerThread(TAG);
    private int mServerPort;
    private final LinkedHashMap<Socket, ObjectOutputStream> mClients = new LinkedHashMap<>();
    private final Game mGameState = new Game();

    public Server(int port) {
        mServerPort = port;
        mHandlerThread.start();
        //Register Handler
        sHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj instanceof PilotMemo) {
                    final PilotMemo memo = (PilotMemo) msg.obj;
                    final Player player = mGameState.getPlayer();
                    player.setAirspeed(memo.airspeed);
                    player.setAltitude(memo.altitude);
                    player.setBearing(memo.direction);
                    player.setEngines(memo.enginesOn);
                    player.setLandingGear(memo.landingGearDeployed);
                } else if (msg.obj instanceof BombardierMemo) {
                    final BombardierMemo memo = (BombardierMemo) msg.obj;
                    if (memo.launch) mGameState.getPlayer().launch();
                    mGameState.getPlayer().setTurretAim(memo.turretAim);
                //} else if (msg.obj instanceof NavigatorMemo) {
                //} else if (msg.obj instanceof SignallerMemo) {
                } else if (msg.obj instanceof ServerMemo) {
                    final ServerMemo memo = (ServerMemo) msg.obj;
                    switch (memo.getAction()) {
                        case ROLE_ASSIGNMENT:
                            final LinkedHashMap<InetAddress, CharSequence> roleAssignments = (LinkedHashMap<InetAddress, CharSequence>) memo.getData();
                            for (InetAddress address : roleAssignments.keySet()) {
                                for (Socket socket : mClients.keySet()) {
                                    if (socket.getInetAddress().equals(address)) {
                                        final String role = roleAssignments.get(address).toString();
                                        synchronized (mClients) {
                                            try { mClients.get(socket).writeObject(new ServerMemo(ServerMemo.Action.ROLE, role)); }
                                            catch (IOException e) { Log.e(TAG, e.getMessage(), e); }
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        case DISCONNECT:
                            //Remove client from client list
                            final InetAddress address = (InetAddress) memo.getData();
                            Iterator<Socket> socketIterator = mClients.keySet().iterator();
                            while (socketIterator.hasNext()) {
                                final Socket socket = socketIterator.next();
                                if (socket.getInetAddress().equals(address)) socketIterator.remove();
                            }
                            for (Socket socket : mClients.keySet()) {
                                if (socket.getInetAddress().equals(address)) mClients.remove(socket);
                            }
                            //Send updated list to clients
                            final LinkedHashSet<InetAddress> addresses = new LinkedHashSet<>();
                            for (Socket s : mClients.keySet()) {
                                addresses.add(s.getInetAddress());
                            }
                            sendToClients(new ServerMemo(ServerMemo.Action.CLIENT_LIST, addresses));
                            break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void run() {
        Log.i(TAG, "Server started");
        try (ServerSocket serverSocket = new ServerSocket(mServerPort)) {
            //Client connections
            while(mClients.size() < 2 && !Thread.interrupted()) { //DEBUGGING: Lobby size modification
                final Socket clientSocket = serverSocket.accept();
                new Thread(new ServerListener(clientSocket)).start();
                mClients.put(clientSocket, new ObjectOutputStream(clientSocket.getOutputStream()));
                //Send client list to clients
                final LinkedHashSet<InetAddress> addresses = new LinkedHashSet<>();
                for (Socket s : mClients.keySet()) {
                    addresses.add(s.getInetAddress());
                }
                sendToClients(new ServerMemo(ServerMemo.Action.CLIENT_LIST, addresses));
                Log.i(TAG, "New client socket received");
            }
            //Game loop
            try {
                long lastUpdateTime = System.currentTimeMillis();
                while(
                        mGameState.update((double) (System.currentTimeMillis() - lastUpdateTime) / 1000L) &&
                                !Thread.interrupted()
                        ) {
                    lastUpdateTime = System.currentTimeMillis();
                    sendToClients(mGameState.getMemo());
                    Thread.sleep(63L);
                }
            } catch (InterruptedException e) { Log.e(TAG, e.getMessage(), e); }
            sendToClients(new ServerMemo(ServerMemo.Action.SHUTDOWN));
        } catch(IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            Log.i(TAG, "Shutdown");
            mHandlerThread.quitSafely();
            try {
                for (ObjectOutputStream stream : mClients.values()) {
                    stream.close();
                }
            } catch (IOException e) { Log.e(TAG, e.getMessage(), e); }
        }
    }

    private void sendToClients(Object object) {
        synchronized (mClients) {
            for (ObjectOutputStream stream : mClients.values()) {
                try {
                    stream.writeObject(object);
                    stream.reset();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }
}