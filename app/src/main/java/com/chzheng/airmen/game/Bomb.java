package com.chzheng.airmen.game;

import android.util.Log;

public class Bomb implements Game.Entity {
    private static final String TAG = "Bomb";
    private static final int VELOCITY = 50, RADIUS = 2;
    private Game game;
    private Coordinates position;
    private double airspeed, direction, altitude;

    public Bomb(Game game, Coordinates position, double airspeed, double direction, double altitude) {
        Log.d(TAG, "Bomb created");
        this.game = game;
        this.position = position;
        this.airspeed = airspeed;
        this.direction = direction;
        this.altitude = altitude;
    }

    @Override
    public boolean update(double delta, double previousDelta) {
        altitude -= VELOCITY * delta;
        position.setLatitude(position.getLatitude() + Math.sin(Math.toRadians(-1 * direction + 90)) * (airspeed / 3600) * delta);
        position.setLongitude(position.getLongitude() + Math.cos(Math.toRadians(-1 * direction + 90)) * (airspeed / 3600) * delta);
        if (altitude > game.getMap().getElevation(position)) return true;
        else {
            game.getMessages().add("Bomb explosion at " + position.toString());
            Log.d(TAG, game.getMessages().toString());
            for (Game.Entity entity : game.getEntities()) {
                if (entity instanceof City && Coordinates.distanceBetween(position, entity.getPosition()) < RADIUS) ((City) entity).destroy();
            }
            return false;
        }
    }

    @Override
    public Coordinates getPosition() {
        return position;
    }
}
