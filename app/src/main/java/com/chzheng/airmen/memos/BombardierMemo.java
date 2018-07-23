package com.chzheng.airmen.memos;

import java.io.Serializable;

public class BombardierMemo implements Serializable {
    private String message;
    public BombardierMemo(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
