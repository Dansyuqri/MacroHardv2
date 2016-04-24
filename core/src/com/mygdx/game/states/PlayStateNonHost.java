package com.mygdx.game.states;

/**
 * Created by hj on 19/3/16.
 */

/**
 * This is for the player which is not the host to start PlayState
 */
public class PlayStateNonHost extends PlayState {
    public PlayStateNonHost(GameStateManager gsm, int playerID) {
        super(gsm, playerID);
    }
}
