package com.chzheng.airmen.game;

import java.io.Serializable;
import java.util.Random;

public class Map {
    private int[][] elevationTable;
    private final int ELEVATION_DIFFERENTIAL = 100;
    private final int MAXIMUM_ELEVATION = 800;
    private int length, width;

    public Map(int length, int width) {
        this.length = length;
        this.width = width;
        //Generate elevation table
        elevationTable = new int[length][width];
        Random random = new Random();
        elevationTable[0][0] = MAXIMUM_ELEVATION / 2;
        for (int row = 0; row < elevationTable.length; row++) {
            for (int column = 0; column < elevationTable[0].length; column++) {
                if (row == 0 && column == 0) continue;
                int sum = 0, n = 0;
                //Traverse adjacent tiles
                for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
                    for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                        if (rowOffset == -1 || (rowOffset == 0 && columnOffset == -1)) {
                            try {
                                sum += elevationTable[row + rowOffset][column + columnOffset];
                                n++;
                            } catch (ArrayIndexOutOfBoundsException e) {}
                        }
                    }
                }
                //Generate a random altitude in the range [average - ELEVATION_DIFFERENTIAL, average + ELEVATION_DIFFERENTIAL]
                int average = sum / n, result;
                do {
                    result = random.nextInt(average + ELEVATION_DIFFERENTIAL * 2 + 1);
                } while (result < average - ELEVATION_DIFFERENTIAL * 2);
                result = Math.round((float) result / ELEVATION_DIFFERENTIAL) * ELEVATION_DIFFERENTIAL;
                if (result > MAXIMUM_ELEVATION) result = random.nextBoolean() ? MAXIMUM_ELEVATION : MAXIMUM_ELEVATION - ELEVATION_DIFFERENTIAL;
                else if (result < 0) result = 0;
                elevationTable[row][column] = result;
            }
        }
    }

    public Coordinates getRandomLocation() {
        Random random = new Random();
        return new Coordinates(random.nextInt(length), random.nextInt(width));
    }

    //Simple getters
    public int getLength() { return length; }
    public int getWidth() { return width; }
    public int[][] getElevationTable() { return elevationTable; }

    public int getElevation(int latitude, int longitude) {
        int result = -1;
        try { result = elevationTable[elevationTable.length - latitude - 1][longitude]; }
        catch (ArrayIndexOutOfBoundsException e) {}
        return result;
    }

    public int getElevation(double latitude, double longitude) {
        return getElevation((int) latitude, (int) longitude);
    }

    public int getElevation(Coordinates coordinates) {
        return getElevation(coordinates.getLatitude(), coordinates.getLongitude());
    }
}