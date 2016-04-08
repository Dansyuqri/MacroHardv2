package com.mygdx.game.states;

import com.mygdx.game.states.PlayState;

import java.util.Random;

/**
 * Created by Syuqri on 3/18/2016.
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
