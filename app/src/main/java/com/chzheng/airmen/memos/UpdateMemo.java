package com.chzheng.airmen.memos;

import com.chzheng.airmen.game.Coordinates;
import com.chzheng.airmen.game.Map;
import com.chzheng.airmen.game.Player;
import com.chzheng.airmen.game.SerialEntity;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateMemo implements Serializable {
    //Pilot
    public int airspeed, altitude, bearing, integrity;
    //Navigator
    public Coordinates coordinates;
    public float latitude, longitude; //Necessary for data-binding views
    public int[][] elevationTable;
    public int elevation;
    //Signaller
    public ArrayList<SerialEntity> entities;
    public ArrayList<String> messages;

    public UpdateMemo(Player player, Map map, ArrayList<SerialEntity> entities, ArrayList<String> messages) {
        airspeed = (int) player.getAirspeed();
        altitude = (int) player.getAltitude();
        bearing = (int) player.getBearing();
        integrity = (int) player.getIntegrity();
        coordinates = player.getPosition();
        latitude = (float) coordinates.getLatitude();
        longitude = (float) coordinates.getLongitude();
        elevationTable = map.getElevationTable();
        elevation = map.getElevation(player.getPosition());
        this.entities = entities;
        this.messages = messages;
    }
}
