package com.mygdx.game;

/**
 * Created by Nayr on 16/3/2016.
 */

public class GameWorld {
    public MacroHardv2 game;
    public boolean multiplayer;
    public float x,y,px,py;

    public GameWorld(MacroHardv2 game){
        this.game = game;
        multiplayer = false;
        x = 200;
        y = 200;
        px = 200;
        py = 200;
    }

    public void update(float delta){

    }
}
