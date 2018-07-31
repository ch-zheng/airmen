package com.chzheng.airmen.game;

import java.io.Serializable;

public class SerialEntity implements Serializable {
    private String type;
    private Coordinates position;
    public SerialEntity(Game.Entity entity) {
        type = entity.getClass().getName();
        position = entity.getPosition();
    }
    public String getType() { return type; }
    public Coordinates getPosition() { return position; }
}
