package com.chzheng.airmen.game;

import java.util.Random;

public class City implements Game.Entity {
    private final static String[] cityNames = {
            //Kazakh cities
            "Almaty",
            "Shymkent",
            "Karagandy",
            "Taraz",
            "Astana",
            "Pavlodar",
            "Oskemen",
            "Semey",
            "Aktobe",
            "Kostanay"
    };
    private Game game;
    private Coordinates position;
    private String name;
    private boolean exists = true;

    public City(Game game, Coordinates position) {
        this.game = game;
        this.position = position;
        name = cityNames[new Random().nextInt(cityNames.length)];
        game.getMessages().add("Target identified: " + name + " at " + position.toString());
    }

    @Override
    public boolean update(double delta, double previousDelta) {
        return exists;
    }

    @Override
    public Coordinates getPosition() {
        return position;
    }

    public void destroy() {
        game.getMessages().add(name + " has been levelled.");
        exists = false;
    }
}
