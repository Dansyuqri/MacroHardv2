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

    public void update(byte[] message) {
        //update player coordinates
        if(message != null){
            if(message[0] == 0) {
                players.get((int)message[1]).x = ((float)message[2]*10 + (float) message[3]/10);
                players.get((int)message[1]).y = ((float)message[4]*10 + (float) message[5]/10);
            }
            //Incoming Message to update map
            if (message[0] == 1){
                MapTile[] new_row = new MapTile[GAME_WIDTH];
                for (int i = 1; i < message.length; i++) {
                    new_row[i-1] = MapTile.fromByte(message[i]);
                }

                while (mapBuffer.size() > 10);
                synchronized (this) {
                    mapBuffer.add(new_row);
                }
            }
            if (message[0] == 2){

            }
        }


    }

    }
