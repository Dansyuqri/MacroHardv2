package com.mygdx.game.states;

/**
 * Created by Syuqri on 3/18/2016.
 */

/**
 * This thread generates the maze and map in game
 */
public class MapMaker extends Thread{
    PlayState playState;
    public MapMaker(PlayState playState){this.playState = playState;}
    @Override
    public void run() {
        try {
            playState.seedSem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            if (isInterrupted()){
                break;
            }
            playState.wallCoord();
        }
    }


}
