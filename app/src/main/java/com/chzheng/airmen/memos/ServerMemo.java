package com.chzheng.airmen.memos;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class ServerMemo implements Serializable {
    public enum Action { NOTHING, CLIENT_LIST, ROLE_ASSIGNMENT, ROLE, DISCONNECT, SHUTDOWN }
    private Action mAction;
    private Serializable mData = null;
    public ServerMemo (Action action) { mAction = action; }
    public ServerMemo(Action action, @Nullable Serializable data) {
        mAction = action;
        mData = data;
    }
    public Action getAction() { return mAction; }
    public Serializable getData() { return mData; }
}
