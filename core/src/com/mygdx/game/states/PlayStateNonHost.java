package com.mygdx.game.states;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.MacroHardv2;
import com.mygdx.game.customEnum.MapTile;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by hj on 19/3/16.
 */
public class PlayStateNonHost extends PlayState {

    public PlayStateNonHost(GameStateManager gsm, int playerID) {
        super(gsm, playerID);
    }
}
