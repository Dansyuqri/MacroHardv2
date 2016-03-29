package com.mygdx.game.states;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.MacroHardv2;
import com.mygdx.game.customEnum.MapTile;

import java.nio.ByteBuffer;

/**
 * Created by hj on 19/3/16.
 */
public class PlayStateNonHost extends PlayState {

    public PlayStateNonHost(GameStateManager gsm, int playerID) {
        super(gsm, playerID);
    }

    public void update(byte[] message) {
        //// TODO: 29/3/2016
        float id, a, b, c, d, e, f, g, h, i;
        ByteBuffer bf = ByteBuffer.wrap(message);
        id = bf.getFloat();
        if (id == 1) {
            a = bf.getFloat();
            b = bf.getFloat();
        } else if (id == 2) {
            a = bf.getFloat();
            b = bf.getFloat();
            c = bf.getFloat();
            d = bf.getFloat();
            e = bf.getFloat();
            f = bf.getFloat();
            g = bf.getFloat();
            h = bf.getFloat();
            i = bf.getFloat();


        }
    }
}
