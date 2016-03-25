package com.mygdx.game.states;

/**
 * Created by Nayr on 25/3/2016.
 */
public class MapMaker2 extends Thread {
    PlayStateNonHost playStateHost;
    public MapMaker2(PlayStateNonHost playStateHost){this.playStateHost = playStateHost;}
    @Override
    public void run() {
        while (true) {
            if (isInterrupted()){
                break;
            }
            if (playStateHost.running) {
                playStateHost.wallCoord();
            }
        }
    }

}
