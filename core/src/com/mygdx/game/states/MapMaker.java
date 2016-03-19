package com.mygdx.game.states;

import com.mygdx.game.states.PlayState;

/**
 * Created by Syuqri on 3/18/2016.
 */
class MapMaker extends Thread{
    PlayStateHost playState;
    public MapMaker(PlayStateHost playState){
        this.playState = playState;
    }
    @Override
    public void run() {
        while (true) {
            if (isInterrupted()){
                break;
            }
            if (playState.running) {
                playState.wallCoord();
            }
        }
    }


}
