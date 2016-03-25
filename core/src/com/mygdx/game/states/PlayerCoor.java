package com.mygdx.game.states;

import com.mygdx.game.MacroHardv2;

/**
 * Created by Nayr on 25/3/2016.
 */
public class PlayerCoor extends Thread {
    PlayStateHost playState;
    public PlayerCoor(PlayStateHost playStateHost){this.playState = playStateHost;}
    @Override
    public void run() {
        while (true) {
            if (isInterrupted()){
                break;
            }
            if (playState.running) {
                long start = System.currentTimeMillis();
                while(System.currentTimeMillis()-start < 0.01){

                }
                MacroHardv2.actionResolver.sendPos(playState.player.x,playState.player.y);
            }
        }
    }
}
