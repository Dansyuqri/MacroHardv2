package com.mygdx.game.states;

import com.mygdx.game.states.PlayState;

/**
 * Created by Syuqri on 3/18/2016.
 */
class MapMaker extends Thread{
    PlayStateHost playStateHost;
    public MapMaker(PlayStateHost playStateHost){this.playStateHost = playStateHost;}
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
