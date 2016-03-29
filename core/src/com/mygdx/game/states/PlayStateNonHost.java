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
        System.out.println("Received123");
        MapTile[] new_row = createArray(MapTile.OBSTACLES);
        //update player coordinates
        if(message[0] == 0){

        }
        //Incoming Message to update map
        if (message[0] == 1){
            for (int i = 1; i < message.length; i++) {
                new_row[i-1] = MapTile.fromByte(message[i]);
            }
            mapBuffer.add(new_row);
        }
        if (message[0] == 2){

        }
    }

    }
