package com.chzheng.airmen.game;

import com.chzheng.airmen.memos.UpdateMemo;

import java.util.ArrayList;

public class Game {
    private double previousDelta = 0;
    private Map map = new Map(20, 20);
    private Player player = new Player(map);
    private ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<String> messages = new ArrayList<>();

    //Returns whether updates should continue
    //The delta modifier assumes a regular update rate of 1 hertz
    public boolean update(double delta) {
        boolean result = player.update(delta, previousDelta);
        for (Entity entity : entities) {
            if (!entity.update(delta)) entities.remove(entity);
        }
        previousDelta = delta;
        return result;
    }

    //Simple getters
    public Player getPlayer() { return player; }
    public UpdateMemo getMemo() { return new UpdateMemo(player, map, messages); }

    public interface Entity {
        boolean update(double delta);
    }
}
