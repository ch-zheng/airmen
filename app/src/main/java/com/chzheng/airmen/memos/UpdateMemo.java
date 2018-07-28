package com.chzheng.airmen.memos;

import com.chzheng.airmen.game.Map;
import com.chzheng.airmen.game.Player;

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

    public UpdateMemo(Player player, Map map, ArrayList<String> messages) {
        airspeed = (int) player.getAirspeed();
        altitude = (int) player.getAltitude();
        direction = (int) player.getDirection();
        latitude = (float) player.getCoordinates().getLatitude();
        longitude = (float) player.getCoordinates().getLongitude();
        elevationTable = map.getElevationTable();
        elevation = map.getElevation(player.getCoordinates());
        this.messages = messages;
    }
}
