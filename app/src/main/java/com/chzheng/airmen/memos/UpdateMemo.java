package com.chzheng.airmen.memos;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateMemo implements Serializable {
    private ArrayList<String> data;
    public UpdateMemo(ArrayList<String> data) {
        this.data = data;
    }
    public ArrayList<String> getMessages() {
        return data;
    }
}
