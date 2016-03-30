package com.mygdx.game.states;

import com.mygdx.game.MacroHardv2;

/**
 * Created by Nayr on 30/3/2016.
 */
public class PlayerCoorSend extends Thread {
    private PlayState game;
    PlayerCoorSend(PlayState game){
        this.game = game;
    }

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()){
                break;
            }
            try{
                sleep(1);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            byte[] message = wrapCoords(MacroHardv2.actionResolver.getmyidint(),game.player.x,game.player.y);
            MacroHardv2.actionResolver.sendPos(message);
        }
    }

    private static byte[] wrapCoords(int id, float x, float y){
        byte[] result = new byte[6];
        result[0] = 0;
        result[1] = (byte) id;
        result[2] = (byte) (x/10);
        result[3] = (byte)((x*10)%100);
        result[4] = (byte)(y/10);
        result[5] = (byte)((y*10)%100);
        return result;
    }
}
