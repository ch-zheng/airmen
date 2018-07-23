package com.chzheng.airmen.memos;

import java.io.Serializable;

public class SignallerMemo implements Serializable {
    private String message;
    public SignallerMemo(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
