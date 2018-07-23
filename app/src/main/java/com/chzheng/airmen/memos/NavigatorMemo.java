package com.chzheng.airmen.memos;

import java.io.Serializable;

public class NavigatorMemo implements Serializable{
    private String message;
    public NavigatorMemo(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
