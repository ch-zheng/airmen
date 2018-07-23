package com.chzheng.airmen;

import com.chzheng.airmen.memos.UpdateMemo;

import java.io.Serializable;
import java.util.ArrayList;

public class GameModel implements Serializable {
    private Bomber mProtagonist = new Bomber();
    private Map mMap = new Map(40,40);
    private ArrayList<String> mMessages = new ArrayList<>(); //DEBUGGING

    //Returns whether updates should continue
    public boolean update(double delta) {
        //TODO
        return mMessages.size() < 10;
    }

    public UpdateMemo getMemo() {
        return new UpdateMemo(mMessages); //DEBUGGING
    }

    //DEBUGGING
    public void addMessage(String message) {
        mMessages.add(message);
    }

    public Bomber getProtagonist() {
        return mProtagonist;
    }

    private interface Entity {
        boolean update(double delta);
    }

    private interface Event {
        //TODO
    }

    protected class Bomber implements Entity {
        //Measurement units: Speed is in knots, altitude in feet, and rotation in degrees clockwise from north.
        //Specifications
        private static final int MAX_SPEED = 200, RANGE = 25, CEILING = 1000;
        private static final int ACCELERATION = 20, RATE_OF_CLIMB = 15, RATE_OF_TURN = 60;
        //Physical variables
        private double airspeed = 0, direction = 0;
        private double latitude = 0, longitude = 0, altitude = 0;
        private boolean enginesEnabled = false, landingGearDeployed = true;
        //User-Interface variables
        public double setAirspeed = 0, setDirection = 0, setAltitude = 0;
        public boolean setEngines = false, setLandingGear = false;

        public boolean update(double delta) {
            enginesEnabled = setEngines;
            landingGearDeployed = setLandingGear;
            //Movement
            if (enginesEnabled) {
                airspeed += (airspeed < setAirspeed ? ACCELERATION : ACCELERATION * -1) * delta;
                altitude += (altitude < setAltitude ? RATE_OF_CLIMB : RATE_OF_CLIMB * -1) * delta;
                double directionChange;
                if (direction > setDirection) directionChange = direction - setDirection > 180 ? RATE_OF_TURN : RATE_OF_TURN * -1;
                else directionChange = setDirection - direction < 180 ? RATE_OF_TURN : RATE_OF_TURN * -1;
                direction = (direction + directionChange * delta) % 360;
            } else {
                altitude -= 10 * delta;
                if (airspeed > 0) airspeed -= 20 * delta;
            }
            latitude += Math.sin(Math.toRadians(-1 * direction + 90)) * airspeed * delta;
            longitude += Math.cos(Math.toRadians(-1 * direction + 90)) * airspeed * delta;
            //Death conditions
            return true;
        }
    }

    private class Map {
        private int[][] altitudes;

        public Map(int length, int width) {
            altitudes = new int[length][width];
            for (int row = 0; row < altitudes.length; row++) {
                for (int column = 0; column < altitudes[0].length; column++) {
                    altitudes[row][column] = 0; //TODO
                }
            }
        }

        public int getAltitude(int latitude, int longitude) {
            return altitudes[latitude][longitude];
        }
    }
}
