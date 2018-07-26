package com.chzheng.airmen.memos;

import java.io.Serializable;

public class PilotMemo implements Serializable {
    public int airspeed, altitude, direction;
    public boolean enginesOn, landingGearDeployed;
    public PilotMemo(
            int airspeed,
            int altitude,
            int direction,
            boolean enginesOn,
            boolean landingGearDeployed
    ) {
        this.airspeed = airspeed;
        this.altitude = altitude;
        this.direction = direction;
        this.enginesOn = enginesOn;
        this.landingGearDeployed = landingGearDeployed;
    }
}
