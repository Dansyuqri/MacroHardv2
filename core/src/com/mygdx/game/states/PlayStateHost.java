package com.mygdx.game.states;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.customEnum.MapTile;
import com.mygdx.game.objects.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by hj on 19/3/16.
 */
public class PlayStateHost extends PlayState {
    private int doorCounter;
    private int powerCounter;
    private ArrayList<MapTile[]> memory;

    public PlayStateHost(GameStateManager gsm){
        super(gsm);
        //spawning initialization
        doorCounter = 0;
        powerCounter = 0;
        memory = new ArrayList<MapTile[]>();
        MapTile[] init = {MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY};
        for (int i = 0; i < 5; i++){
            memory.add(init);
        }
        MapMaker mapMaker = new MapMaker(this);
        mapMaker.start();
    }

    void wallCoord(){
        powerCounter += 1;
        doorCounter += 1;
        boolean test = false;
        int out_index = 0;
        MapTile[] new_row = createArray(MapTile.OBSTACLES);

        // random generator
        while (!test) {
            int temp = MathUtils.random(1, 9);
            for (int i = 0; i < temp; i++) {
                int coord = MathUtils.random(0,8);
                new_row[coord] = MapTile.EMPTY;
            }
            for (int i = 0; i < new_row.length; i++) {
                if ((new_row[i] == MapTile.EMPTY) && current[i]) {
                    test = true;
                    break;
                }
            }
        }

        // updating the path array (only 1 path)
        for (int k = 0; k < new_row.length; k++) {
            if ((new_row[k] == MapTile.EMPTY) && current[k]) {
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
                if ((new_row[out_index + j] == MapTile.EMPTY) && current[out_index + j - 1]) {
                    current[out_index + j] = true;
                }
                else {
                    break;
                }
            }
            if (out_index - j >= 0) {
                if ((new_row[out_index - j] == MapTile.EMPTY) && current[out_index - j + 1]) {
                    current[out_index - j] = true;
                }
                else {
                    break;
                }
            }
        }

        // creating random spikes
        int spikeNo = MathUtils.random(0,3);
        for (int j = 0; j < spikeNo; j++) {
            int pos = MathUtils.random(0,8);
            if (new_row[pos] == MapTile.OBSTACLES){
                new_row[pos] = MapTile.SPIKES;
            }
        }

        // spawning power ups after a certain time. 20 is default. 5 is for testing
        if (powerCounter > 20){
            while (true){
                int temp = MathUtils.random(0,8);
                if (new_row[temp] == MapTile.EMPTY){
                    new_row[temp] = MapTile.POWER;
                    powerCounter = 0;
                    break;
                }
            }
        }

        // spawning door
        if (doorCounter > 44){
            for (int i = 0; i < current.length; i++) {
                if (new_row[i] == MapTile.EMPTY) {
                    new_row[i] = MapTile.DOOR;
                }
            }
            doorCounter = 0;
        }

        // updating the memory
        memory.remove(memory.size()-1);
        memory.add(0, new_row);

        int i = out_index;
        int j = 0;

        // spawning door switch
        boolean switchCoord = false;
        if (doorCounter == 44) {
            switchCoord = true;
            int counter = 0;
            while (true) {
                if (counter > 10) {
                    break;
                }
                int dir = MathUtils.random(0, 3);
                switch (dir){
                    case 0:
                        if (i > 0 && memory.get(j)[i - 1] == MapTile.EMPTY) {
                            i--;
                            counter++;
                        }
                        break;
                    case 1:
                        if (j < memory.size() - 1 && memory.get(j + 1)[i] == MapTile.EMPTY) {
                            j++;
                            counter++;
                        }
                        break;
                    case 2:
                        if (i < GAME_WIDTH - 1 && memory.get(j)[i + 1] == MapTile.EMPTY) {
                            i++;
                            counter++;
                        }
                        break;
                    case 3:
                        if (j > 0 && memory.get(j - 1)[i] == MapTile.EMPTY) {
                            j--;
                            counter++;
                        }
                        break;
                }
            }
        }

        synchronized (this) {
            while (mapBuffer.size() > 10){
                try {
                    wait();
                } catch (InterruptedException ignored){}
            }
            mapBuffer.add(new_row);
            if (switchCoord) {
                mapBuffer.get(mapBuffer.size() - j - 1)[i] = MapTile.SWITCH;
            }
            notifyAll();
        }
    }
}
