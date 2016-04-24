package com.mygdx.game.states;

import com.mygdx.game.MacroHardv2;
import com.mygdx.game.customEnum.MessageCode;

import java.util.Random;


/**
 * Created by hj on 19/3/16.
 */
/**
 * This is for the player which is the host to start PlayState
 */
public class PlayStateHost extends PlayState {

    public PlayStateHost(GameStateManager gsm, int playerID){
        super(gsm, playerID);
        Random random = new Random();
        seed = random.nextInt();
        mapRandomizer = new Random(seed);
        seedSem.release();
        MacroHardv2.actionResolver.sendReliable(wrapSeed(seed));
    }

    public PlayStateHost(GameStateManager gsm, int playerID, boolean soloPLay){
        super(gsm, playerID);
        Random random = new Random();
        seed = random.nextInt();
        mapRandomizer = new Random(seed);
        seedSem.release();
        MacroHardv2.actionResolver.sendReliable(wrapSeed(seed));
        sync = true;
    }

    private byte[] wrapSeed(long seed){
        String seedString = Long.toString(seed);
        byte[] seedStringBytes = seedString.getBytes();
        byte[] result = new byte[seedStringBytes.length + 1];
        result[0] = MessageCode.MAP_SEED;
        for (int i = 0; i < seedStringBytes.length; i++) {
            result[i+1] = seedStringBytes[i];
        }
        return result;
    }
}