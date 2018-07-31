package com.chzheng.airmen.game;

import com.chzheng.airmen.memos.UpdateMemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Game {
    private double previousDelta = 0;
    private Map map = new Map(20, 20);
    private Player player = new Player(this);
    private ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<String> messages = new ArrayList<>();

    public Game() {
        entities.add(new City(this, map.getRandomLocation()));
        entities.add(new Interceptor(this, new Coordinates(
                player.getPosition().getLatitude() + 3,
                player.getPosition().getLongitude() + 3
        )));
    }

    //Returns whether updates should continue
    //The delta modifier assumes a regular update rate of 1 hertz
    public boolean update(double delta) {
        //Update entities
        boolean result = player.update(delta, previousDelta);
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            final Entity entity = iterator.next();
            if (!entity.update(delta, previousDelta)) iterator.remove();
        }
        //Random events
        if (getRandomBoolean(0.01 * (player.getAltitude() / 900) * delta)) {
            entities.add(new Interceptor(this, map.getRandomLocation()));
        }
        previousDelta = delta;
        return result;
    }

    //Simple getters
    public Player getPlayer() { return player; }
    public Map getMap() { return map; }
    public ArrayList<Entity> getEntities() { return entities; }
    public ArrayList<String> getMessages() { return messages; }
    public UpdateMemo getMemo() {
        final ArrayList<SerialEntity> serialEntities = new ArrayList<>();
        for (Entity entity: entities) {
            serialEntities.add(new SerialEntity(entity));
        }
        return new UpdateMemo(player, map, serialEntities, messages);
    }

    private boolean getRandomBoolean(double probability) {
        return new Random().nextDouble() < probability;
    }

    public interface Entity {
        boolean update(double delta, double previousDelta);
        Coordinates getPosition();
    }
}
