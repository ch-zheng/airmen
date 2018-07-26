package com.chzheng.airmen;

import com.chzheng.airmen.memos.UpdateMemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class GameModel implements Serializable {
    private static final String TAG = "Game model";
    private double mPreviousDelta = 0;
    private Map mMap = new Map(20,20);
    private Bomber mProtagonist = new Bomber();
    private ArrayList<String> mMessages = new ArrayList<>(); //DEBUGGING

    //Returns whether updates should continue
    public boolean update(double delta) {
        boolean result = mProtagonist.update(delta);
        mPreviousDelta = delta;
        return result;
    }

    public UpdateMemo getMemo() {
        final UpdateMemo result = new UpdateMemo(mProtagonist, mMap);
        result.setMessages(mMessages); //DEBUGGING
        return result;
    }

    //DEBUGGING
    public void addMessage(String message) {
        mMessages.add(message);
    }

    public Bomber getProtagonist() {
        return mProtagonist;
    }

    public class Bomber {
        //Measurement units: Speed is in knots, altitude in feet, and rotation in degrees clockwise from north.
        //Specifications
        private static final int MAX_SPEED = 200, TAXI_SPEED = 20, CEILING = 1000;
        private static final int ACCELERATION = 20, TAXI_ACCELERATION = 2, RATE_OF_CLIMB = 20, RATE_OF_TURN = 6;
        private static final int DRAG = 10, GRAVITY = 10;
        //Physical variables
        public double airspeed = 0, direction = 0;
        public double latitude = 1, longitude = 1, altitude = mMap.getElevation((int) latitude, (int) longitude);
        public boolean enginesEnabled = false, landingGearDeployed = true;
        //User-interface variables
        public int setAirspeed = 0, setDirection = 0, setAltitude = 0;
        public boolean setEngines = false, setLandingGear = true;

        public boolean update(double delta) {
            enginesEnabled = setEngines;
            landingGearDeployed = setLandingGear;
            //Movement
            final int elevation = mMap.getElevation((int) latitude, (int) longitude);
            final boolean isGrounded = Math.abs(altitude - elevation) < RATE_OF_CLIMB * mPreviousDelta ||
                    altitude <= elevation - RATE_OF_CLIMB * mPreviousDelta;
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
                        if (altitude < setAltitude) altitude += RATE_OF_CLIMB * delta;
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
            latitude += Math.sin(Math.toRadians(-1 * direction + 90)) * (airspeed / 3600) * delta;
            longitude += Math.cos(Math.toRadians(-1 * direction + 90)) * (airspeed / 3600) * delta;
            //Death conditions
            if (
                    latitude < 0 || latitude > mMap.getLength() ||
                    longitude < 0 || longitude > mMap.getWidth()
            ) return false;
            return true;
        }
    }

    public class Map {
        private int[][] elevationTable;
        private final int ELEVATION_DIFFERENTIAL = 100;
        private int length, width;

        public Map(int length, int width) {
            this.length = length;
            this.width = width;
            //Generate altitude table
            elevationTable = new int[length][width];
            Random random = new Random();
            for (int row = 0; row < elevationTable.length; row++) {
                for (int column = 0; column < elevationTable[0].length; column++) {
                    int reference = 0, result;
                    //Pick a reference point
                    if (column > 0) reference = elevationTable[row][column -1];
                    else if (row > 0) reference = elevationTable[row - 1][column];
                    //Determine the offset
                    final int triCoin = random.nextInt(3);
                    if (triCoin == 0) result = reference + ELEVATION_DIFFERENTIAL;
                    else if (triCoin == 1) result = reference - ELEVATION_DIFFERENTIAL;
                    else result = reference;
                    //Certify result
                    if (result < 0) result = 0;
                    else if (result > 900) result = 900;
                    elevationTable[row][column] = result;
                }
            }
        }

        public int getLength() { return length; }

        public int getWidth() { return width; }

        public int getElevation(int latitude, int longitude) {
            int result = -1;
            try { result = elevationTable[elevationTable.length - latitude - 1][longitude]; }
            catch (ArrayIndexOutOfBoundsException e) {}
            return result;
        }

        public int[][] getElevationTable() { return elevationTable; }
    }
}
