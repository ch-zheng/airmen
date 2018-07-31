package com.chzheng.airmen.memos;

import java.io.Serializable;

public class BombardierMemo implements Serializable {
    public boolean launch;
    public int turretAim;

    public BombardierMemo(boolean launch, int turretAim) {
        this.launch = launch;
        this.turretAim = turretAim;
    }
}
