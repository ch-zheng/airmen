package com.chzheng.airmen.game;

import java.io.Serializable;
import java.util.Locale;

import static com.chzheng.airmen.game.Coordinates.Type.NONE;

public class Coordinates implements Serializable {
    public enum Type {NONE, INTERCEPTOR, FLAK, TARGET}
    private double latitude, longitude;
    private Type type = NONE;

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinates(Coordinates coordinates) {
        this(coordinates.getLatitude(), coordinates.getLongitude());
    }

    public static double distanceBetween(Coordinates one, Coordinates two) {
        return Math.abs(Math.hypot(one.longitude - two.longitude, one.latitude - two.latitude));
    }

    public static double bearingTo(Coordinates center, Coordinates other) {
        final double angle = Math.toDegrees(Math.atan2(other.latitude - center.latitude, other.longitude - center.longitude));
        return (angle * -1 + 90 + 360) % 360;
    }

    public static Coordinates relativePosition(Coordinates center, Coordinates other) {
        return new Coordinates(other.latitude - center.latitude, other.longitude - center.longitude);
    }

    @Override
    public String toString() { return String.format(Locale.ENGLISH, "%1$.3f, %2$.3f", latitude, longitude); }

    //Simple getters
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public Type getType() { return type; }

    //Simple setters
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setType(Type type) { this.type = type; }
}
