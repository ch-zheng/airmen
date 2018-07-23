package com.chzheng.airmen.memos;

import java.io.Serializable;

public class PilotMemo implements Serializable {
    private String message = null;
    public PilotMemo(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
