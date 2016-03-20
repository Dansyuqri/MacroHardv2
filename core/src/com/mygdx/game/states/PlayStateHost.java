package com.mygdx.game.states;

import com.badlogic.gdx.math.MathUtils;
/**
 * Created by hj on 19/3/16.
 */
public class PlayStateHost extends PlayState {
    PlayStateHost(GameStateManager gsm){
        super(gsm);
        //spawning initialization
        MapMaker mapMaker = new MapMaker(this);
        mapMaker.start();
    }

    void wallCoord(){
        boolean test = false;
        int out_index = 0;
        boolean[] new_row = {false, false, false, false, false, false, false, false, false};

        // random generator
        while (!test) {
            int temp = MathUtils.random(1, 9);
            for (int i = 0; i < temp; i++) {
                int coord = MathUtils.random(0,8);
                new_row[coord] = true;
            }
            for (int i = 0; i < new_row.length; i++) {
                if (new_row[i] && current[i]) {
                    test = true;
                    break;
                }
            }
        }

        // updating the path array (only 1 path)
        for (int k = 0; k < new_row.length; k++) {
            if (new_row[k] && current[k]) {
                current[k] = true;
                out_index = k;
            } else {
                current[k] = false;
            }
        }

        // updating the path array (if there are walkable areas next to the true path then they are
        // also true)
        for (int j = 1; j < new_row.length; j++) {
            if (out_index + j < new_row.length) {
                if (new_row[out_index + j] && current[out_index + j - 1]) {
                    current[out_index + j] = true;
                }
                else {
                    break;
                }
            }
            if (out_index - j >= 0) {
                if (new_row[out_index - j] && current[out_index - j + 1]) {
                    current[out_index - j] = true;
                }
                else {
                    break;
                }
            }
        }

        // spawning power ups after a certain time
        boolean[] temp_power = {false, false, false, false, false, false, false, false, false};
        if (powerCounter > 20){
            while (true){
                int temp = MathUtils.random(0,8);
                if (new_row[temp]){
                    temp_power[temp] = true;
                    powerCounter = 0;
                    break;
                }
            }
        }

        // spawning door switch
        boolean[] temp_switch = {false, false, false, false, false, false, false, false, false};
        if (doorCounter == 40){
            while (true){
                int temp = MathUtils.random(0,8);
                if (current[temp]){
                    temp_switch[temp] = true;
                    break;
                }
            }
        }

        // spawning door/barrier
        boolean[] temp_door = {false, false, false, false, false, false, false, false, false};
        if (doorCounter > 45){
            for (int i = 0; i < current.length; i++) {
                if (new_row[i]) {
                    temp_door[i] = true;
                }
            }
            doorCounter = 0;
        }

        synchronized (mapBuffer) {
            while (mapBuffer.size() > 3){
                try {
                    //System.out.println("wallcoord");
                    //System.out.println(player.x);
                   // System.out.println(player.width);
                    mapBuffer.wait();
                    //System.out.println("wallcoord done");
                } catch (InterruptedException e){

                }
            }
            powerUpBuffer.add(temp_power);
            mapBuffer.add(new_row);
            switchBuffer.add(temp_switch);
            doorBuffer.add(temp_door);
            mapBuffer.notifyAll();
        }
    }
}
