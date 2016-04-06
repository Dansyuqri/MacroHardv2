package com.mygdx.game.states;

import java.util.concurrent.Semaphore;

/**
 * Created by hj on 19/3/16.
 */
public class PlayStateNonHost extends PlayState {
    public PlayStateNonHost(GameStateManager gsm, int playerID) {
        super(gsm, playerID);
        mapCon = new Semaphore(0);
    }

    public void goToRestartState(){
        coordSender.interrupt();
        dispose();
        gsm.set(new RestartState(gsm, getScore()));
    }
}
