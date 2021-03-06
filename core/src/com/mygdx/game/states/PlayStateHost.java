package com.mygdx.game.states;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.customEnum.MapTile;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by hj on 19/3/16.
 */
public class PlayStateHost extends PlayState {
    private int doorCounter, powerCounter, spikeCounter;
    private ArrayList<MapTile[]> memory;
    private boolean[] current = createArray(true);

    public PlayStateHost(GameStateManager gsm, int playerID){
        super(gsm, playerID);
        //spawning initialization
        doorCounter = 0;
        powerCounter = 0;
        spikeCounter = 0;
        memory = new ArrayList<MapTile[]>();
        MapTile[] init = createArray(MapTile.EMPTY);
        for (int i = 0; i < 5; i++){
            memory.add(init);
        }
        MapMaker mapMaker = new MapMaker(this);
        mapMaker.start();
    }

    public MapTile[] generator(MapTile[] new_row){
        boolean test = false;
        while (!test) {
            int temp = MathUtils.random(1, 9);
            for (int i = 0; i < temp; i++) {
                int coord = MathUtils.random(0, 8);
                new_row[coord] = MapTile.EMPTY;
            }
            for (int i = 0; i < new_row.length; i++) {
                if ((new_row[i] == MapTile.EMPTY) && current[i]) {
                    test = true;
                    break;
                }
            }
        }
        return new_row;
    }

    public boolean[] updatePath(MapTile[] new_row, boolean[] current){
        int out_index = 0;
        for (int k = 0; k < new_row.length; k++) {
            if ((new_row[k] == MapTile.EMPTY) && current[k]) {
                current[k] = true;
                out_index = k;
            } else {
                current[k] = false;
            }
        }

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
        return current;
    }

    public MapTile[] genSpikes(MapTile[] new_row){
        int spikeNo = MathUtils.random(0,2);
        for (int j = 0; j < spikeNo; j++) {
            int pos = MathUtils.random(0,8);
            if (new_row[pos] == MapTile.OBSTACLES){
                new_row[pos] = MapTile.SPIKES;
            }
        }
        return new_row;
    }

    public MapTile[] genHole(MapTile[] new_row){
        int holeNo = MathUtils.random(0,2);
        for (int j = 0; j < holeNo; j++) {
            int pos = MathUtils.random(0,8);
            if (new_row[pos] == MapTile.EMPTY){
                new_row[pos] = MapTile.HOLE;
            }
        }
        return new_row;
    }

    public MapTile[] genPower(MapTile[] new_row){
        while (true){
            int temp = MathUtils.random(0,8);
            if (new_row[temp] == MapTile.EMPTY){
                new_row[temp] = MapTile.POWER;
                powerCounter = 0;
                break;
            }
        }
        return new_row;
    }

    public MapTile[] genDoor(MapTile[] new_row){
        for (int i = 0; i < current.length; i++) {
            if (new_row[i] == MapTile.EMPTY) {
                new_row[i] = MapTile.DOOR;
            }
        }
        return new_row;
    }

    public void genSwitch(ArrayList<MapTile[]> memory, boolean[] current){
        int i;
        for (i = 0; i < current.length; i++) {
            if (current[i]){
                break;
            }
        }

        int j = 0;

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
                    if (j < 4 && memory.get(j + 1)[i] == MapTile.EMPTY) {
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
        mapBuffer.get(mapBuffer.size() - j - 1)[i] = MapTile.SWITCH;
    }

    void wallCoord(){
        powerCounter += 1;
        doorCounter += 1;
        spikeCounter += 1;
        MapTile[] new_row = createArray(MapTile.OBSTACLES);

        // random generator
        new_row = generator(new_row);

        // updating the path array
        current = updatePath(new_row, current);

        // creating random spikes
        if (spikeCounter > 3) {
            new_row = genSpikes(new_row);
            new_row = genHole(new_row);
            spikeCounter = 0;
        }

        // spawning power ups after a certain time. 20 is default. 5 is for testing
        if (powerCounter > 20){
            new_row = genPower(new_row);
        }

        // spawning door
        if (doorCounter > 44){
            new_row = genDoor(new_row);
            doorCounter = 0;
        }

        // updating the memory
        memory.remove(memory.size()-1);
        memory.add(0, new_row);

        // spawning door switch
        if (doorCounter == 44) {
            genSwitch(memory, current);
        }

        synchronized (this) {
            while (mapBuffer.size() > 10){
                try {
                    wait();
                } catch (InterruptedException ignored){}
            }
            mapBuffer.add(new_row);
            notifyAll();
        }
    }
}
