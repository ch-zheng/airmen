package com.chzheng.airmen.game;

import android.util.Log;

import java.util.Random;

public class Player implements Game.Entity {
    private static final String TAG = "Player";
    private Game game;
    //Specifications (Units: speed in knots, altitude in feet, rotation in degrees clockwise from north)
    private static final int MAX_SPEED = 200, TAXI_SPEED = 20, CEILING = 900, FIREPOWER = 10, TURRET_AUTOAIM = 60, TURRET_RANGE = 3;
    private static final int ACCELERATION = 20, TAXI_ACCELERATION = 2, RATE_OF_CLIMB = 50, RATE_OF_TURN = 6;
    private static final int DRAG = 10, GRAVITY = 10;
    //Physical variables
    private double airspeed = 0, bearing = 0, altitude, integrity = 100, turretAim = 0;
    private Coordinates position;
    private boolean engines = false, landingGear = true, armed = false;
    private int bombLoad = 2;
    //User-interface variables
    private double setAirspeed = 0, setDirection = 0, setAltitude = 0;

    public Player(Game game) {
        this.game = game;
        position = new Coordinates(1.5, game.getMap().getWidth() / 2 + 0.5);
        altitude = game.getMap().getElevation(position);
    }

    @Override
    public boolean update(double delta, double previousDelta) {
        //Fire guns
        final double turretBearing = (turretAim + bearing) % 360;
        Log.d(TAG, String.valueOf(turretBearing));
        for (Game.Entity entity : game.getEntities()) {
            if (entity instanceof Interceptor &&
                    Coordinates.distanceBetween(entity.getPosition(), position) < TURRET_RANGE &&
                    Math.abs(turretBearing - Coordinates.bearingTo(position, entity.getPosition())) < TURRET_AUTOAIM //FIXME: Interceptors die as soon as they enter within range
                    )
                ((Interceptor) entity).damage(FIREPOWER, delta);
        }
        //Movement
        final int elevation = game.getMap().getElevation(position);
        final boolean isGrounded = Math.abs(altitude - elevation) < RATE_OF_CLIMB * previousDelta;
        if (altitude < elevation - RATE_OF_CLIMB * previousDelta) return false;
        if (!isGrounded) {
            //Plane is in the air
            if (engines) {
                //Powered flight
                airspeed += (airspeed < setAirspeed && airspeed < MAX_SPEED ? ACCELERATION : ACCELERATION * -1) * delta;
                altitude += (altitude < setAltitude && altitude < CEILING ? RATE_OF_CLIMB : RATE_OF_CLIMB * -1) * delta;
            } else {
                //Gliding
                airspeed -= (airspeed > 0 ? DRAG : DRAG * -1) * delta;
                if (altitude > elevation) altitude -= GRAVITY * delta;
            }
        } else {
            //Plane is on the ground
            if (landingGear) {
                if (engines) {
                    airspeed += (airspeed < setAirspeed && airspeed < TAXI_SPEED ? TAXI_ACCELERATION : TAXI_ACCELERATION * -1) * delta;
                    if (altitude < setAltitude && airspeed > TAXI_SPEED - 1) altitude += RATE_OF_CLIMB * delta;
                } else {
                    airspeed -= (airspeed > 0 ? DRAG : DRAG * -1) * delta;
                }
            } else return false;
        }
        //Steering
        double directionChange = 0;
        if (bearing > setDirection) directionChange = bearing - setDirection > 180 ? RATE_OF_TURN : RATE_OF_TURN * -1;
        else if (bearing < setDirection) directionChange = setDirection - bearing < 180 ? RATE_OF_TURN : RATE_OF_TURN * -1;
        bearing = (bearing + directionChange * delta + 360) % 360;
        //Coordinate translation
        position.setLatitude(position.getLatitude() + Math.sin(Math.toRadians(-1 * bearing + 90)) * (airspeed / 3600) * delta);
        position.setLongitude(position.getLongitude() + Math.cos(Math.toRadians(-1 * bearing + 90)) * (airspeed / 3600) * delta);
        //Death conditions
        return !(position.getLatitude() < 0 || position.getLatitude() > game.getMap().getLength() || position.getLongitude() < 0 || position.getLongitude() > game.getMap().getWidth());
    }

    public void damage(double damage, double delta) {
        integrity -= damage * delta;
        if (armed && new Random().nextDouble() < delta / 20) integrity = 0;
    }

    public void launch() {
        if (bombLoad > 0) {
            game.getEntities().add(new Bomb(game, position, airspeed, bearing, altitude));
            bombLoad--;
        }
    }

    //Simple getters
    public double getAirspeed() { return airspeed; }
    public double getBearing() { return bearing; }
    public double getAltitude() { return altitude; }
    public double getIntegrity() { return integrity; }
    @Override
    public Coordinates getPosition() { return position; }

    //Simple setters
    public void setAirspeed(double airspeed) { setAirspeed = airspeed; }
    public void setBearing(double bearing) { setDirection = bearing; }
    public void setAltitude(double altitude) { setAltitude = altitude; }
    public void setEngines(boolean engines) { this.engines = engines; }
    public void setLandingGear(boolean landingGear) { this.landingGear = landingGear; }
    public void setTurretAim(double turretAim) { this.turretAim = turretAim; }
}