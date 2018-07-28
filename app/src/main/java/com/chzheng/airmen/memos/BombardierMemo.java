package com.chzheng.airmen.memos;

import java.io.Serializable;

public class BombardierMemo implements Serializable {
    public boolean arm, launch;
    public int turretDirection;

    public BombardierMemo(boolean arm, boolean launch, int turretDirection) {
        this.arm = arm;
        this.launch = launch;
        this.turretDirection = turretDirection;
    }
}
