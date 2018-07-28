package com.chzheng.airmen.game;

public class Player {
    private Map map;
    //Specifications (Units: speed in knots, altitude in feet, rotation in degrees clockwise from north)
    private static final int MAX_SPEED = 200, TAXI_SPEED = 20, CEILING = 900;
    private static final int ACCELERATION = 20, TAXI_ACCELERATION = 2, RATE_OF_CLIMB = 20, RATE_OF_TURN = 6;
    private static final int DRAG = 10, GRAVITY = 10;
    //Physical variables
    private double airspeed = 0, direction = 0, altitude;
    private Coordinates coordinates;
    private boolean enginesEnabled = false, landingGearDeployed = true;
    //User-interface variables
    private double setAirspeed = 0, setDirection = 0, setAltitude = 0;

    public Player(Map map) {
        this.map = map;
        coordinates = new Coordinates(1, map.getWidth() / 2);
        altitude = map.getElevation(coordinates);
    }

    public boolean update(double delta, double previousDelta) {
        //Movement
        final int elevation = map.getElevation(coordinates);
        final boolean isGrounded = Math.abs(altitude - elevation) < RATE_OF_CLIMB * previousDelta;
        if (altitude < elevation - RATE_OF_CLIMB * previousDelta) return false;
        if (!isGrounded) {
            //Plane is in the air
            if (enginesEnabled) {
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
            if (landingGearDeployed) {
                if (enginesEnabled) {
                    airspeed += (airspeed < setAirspeed && airspeed < TAXI_SPEED ? TAXI_ACCELERATION : TAXI_ACCELERATION * -1) * delta;
                    if (altitude < setAltitude && airspeed > TAXI_SPEED - 1) altitude += RATE_OF_CLIMB * delta;
                } else {
                    airspeed -= (airspeed > 0 ? DRAG : DRAG * -1) * delta;
                }
            } else return false;
        }
        //Steering
        double directionChange = 0;
        if (direction > setDirection) directionChange = direction - setDirection > 180 ? RATE_OF_TURN : RATE_OF_TURN * -1;
        else if (direction < setDirection) directionChange = setDirection - direction < 180 ? RATE_OF_TURN : RATE_OF_TURN * -1;
        direction = (direction + directionChange * delta + 360) % 360;
        //Coordinate translation
        coordinates.setLatitude(coordinates.getLatitude() + Math.sin(Math.toRadians(-1 * direction + 90)) * (airspeed / 3600) * delta);
        coordinates.setLongitude(coordinates.getLongitude() + Math.cos(Math.toRadians(-1 * direction + 90)) * (airspeed / 3600) * delta);
        //Death conditions
        return !(coordinates.getLatitude() < 0 || coordinates.getLatitude() > map.getLength() || coordinates.getLongitude() < 0 || coordinates.getLongitude() > map.getWidth());
    }

    //Simple getters
    public double getAirspeed() { return airspeed; }
    public double getDirection() { return direction; }
    public double getAltitude() { return altitude; }
    public Coordinates getCoordinates() { return coordinates; }

    //Simple setters
    public void setAirspeed(double airspeed) { setAirspeed = airspeed; }
    public void setDirection(double direction) { setDirection = direction; }
    public void setAltitude(double altitude) { setAltitude = altitude; }
    public void setEngines(boolean engines) { enginesEnabled = engines; }
    public void setLandingGear(boolean landingGear) { landingGearDeployed = landingGear; }
}