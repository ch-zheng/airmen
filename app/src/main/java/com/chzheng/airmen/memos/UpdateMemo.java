package com.chzheng.airmen.memos;

import com.chzheng.airmen.GameModel;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateMemo implements Serializable {
    //Pilot
    public int airspeed, altitude, direction;
    //Navigator
    public float latitude, longitude;
    public int[][] elevationTable;
    public int elevation;
    //Bombardier
    //Signaller
    public ArrayList<String> messages;

    public UpdateMemo(GameModel.Bomber bomber, GameModel.Map map) {
        airspeed = (int) bomber.airspeed;
        altitude = (int) bomber.altitude;
        direction = (int) bomber.direction;
        latitude = (float) bomber.latitude;
        longitude = (float) bomber.longitude;
        elevationTable = map.getElevationTable();
        elevation = map.getElevation((int) latitude, (int) longitude);
    }
}
