package com.chzheng.airmen.game;

import android.util.Log;

public class Interceptor implements Game.Entity {
    private static final String TAG = "Interceptor";
    private static final int MAX_SPEED = 400, ACCELERATION = 50, RATE_OF_TURN = 12, FIREPOWER = 1, RANGE = 2;
    private Player target;
    private Coordinates position;
    private double airspeed = 0, bearing = 0, integrity = 100;

    public Interceptor(Game game, Coordinates coordinates) {
        this.target = game.getPlayer();
        this.position = coordinates;
        game.getMessages().add("Enemy interceptor deployed");
    }

    @Override
    public boolean update(double delta, double previousDelta) {
        final double distanceToTarget = Coordinates.distanceBetween(position, target.getPosition());
        final double setBearing;
        if (distanceToTarget > RANGE) {
            //Catch target
            setBearing = Coordinates.bearingTo(position, target.getPosition());
            if (airspeed < MAX_SPEED) airspeed += ACCELERATION * delta;
        } else {
            //Follow alongside target
            setBearing = target.getBearing();
            if (airspeed < target.getAirspeed()) airspeed += ACCELERATION * delta;
            else if (airspeed > target.getAirspeed()) airspeed -= ACCELERATION * delta;
            target.damage(FIREPOWER, delta);
        }
        double directionChange = 0;
        if (bearing > setBearing) directionChange = bearing - setBearing > 180 ? RATE_OF_TURN : RATE_OF_TURN * -1;
        else if (bearing < setBearing) directionChange = setBearing - bearing < 180 ? RATE_OF_TURN : RATE_OF_TURN * -1;
        bearing = (bearing + directionChange * delta + 360) % 360;
        if (airspeed > MAX_SPEED) airspeed = MAX_SPEED;
        position.setLatitude(position.getLatitude() + Math.sin(Math.toRadians(-1 * bearing + 90)) * (airspeed / 3600) * delta);
        position.setLongitude(position.getLongitude() + Math.cos(Math.toRadians(-1 * bearing + 90)) * (airspeed / 3600) * delta);
        if (integrity <= 0) Log.d(TAG, "INTERCEPTOR DESTROYED");
        return integrity > 0;
    }

    @Override
    public Coordinates getPosition() { return position; }

    public void damage(double damage, double delta) { integrity -= damage * delta; }
}
