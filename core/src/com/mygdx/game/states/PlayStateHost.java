package com.mygdx.game.states;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.MacroHardv2;
import com.mygdx.game.customEnum.MapTile;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;


/**
 * Created by hj on 19/3/16.
 */
public class PlayStateHost extends PlayState {
    private int doorCounter, powerCounter, spikeCounter, HostMapCounter;
    private ArrayList<MapTile[]> memory;
    private boolean[] current = createArray(true);
    private MapMaker mapMaker;

    public PlayStateHost(GameStateManager gsm, int playerID){
        super(gsm, playerID);
        //spawning initialization

        mapCon = new Semaphore(-4);

        HostMapCounter = 0;
        doorCounter = 0;
        powerCounter = 0;
        spikeCounter = 0;
        memory = new ArrayList<MapTile[]>();
        MapTile[] init = createArray(MapTile.EMPTY);
        for (int i = 0; i < 5; i++){
            memory.add(init);
        }

        mapMaker = new MapMaker(this);
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

    public MapTile[] genSwitch(ArrayList<MapTile[]> memory, boolean[] current, MapTile[] new_row){
        int i;
        for (i = 0; i < current.length; i++) {
            if (current[i]){
                break;
            }
        }

        int j = 0;

        int counter = 0;
        while (true) {
            if (counter > 8) {
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
                    if (j < 3 && memory.get(j + 1)[i] == MapTile.EMPTY) {
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
        if (j == 0){
            new_row[i] = MapTile.SWITCH;
            return new_row;
        } else {
            mapBuffer.get(mapBuffer.size() - j)[i] = MapTile.SWITCH;
            return null;
        }
    }

    void wallCoord() {
        powerCounter += 1;
        doorCounter = (doorCounter + 1)%60;
        spikeCounter += 1;
        MapTile[] new_row = createArray(MapTile.OBSTACLES);

        // random generator
        new_row = generator(new_row);

        // updating the path array
        current = updatePath(new_row, current);

        // creating random spikes
        if (spikeCounter > 3) {
            new_row = genSpikes(new_row);
            spikeCounter = 0;
        }

        // spawning power ups after a certain time. 20 is default. 20 is for testing
        if (powerCounter > 20) {
            new_row = genPower(new_row);
        }

        // spawning door
        if (doorCounter == 45) {
            new_row = genDoor(new_row);
        }

        // updating the memory
        memory.remove(memory.size() - 1);
        memory.add(0, new_row);

        // spawning door switch
        if (doorCounter == 44 || doorCounter == 48) {
            MapTile[] result;
            if ((result = genSwitch(memory, current, new_row)) != null){
                new_row = result;
            }
        }

        try {
            mapPro.acquire();
            mapMod.acquire();

            mapBuffer.add(new_row);
            if (mapBuffer.size() > 5) {
                MacroHardv2.actionResolver.sendReliable(tobyte(mapBuffer.get(mapBuffer.size() - 6), HostMapCounter));
                HostMapCounter = (HostMapCounter + 1) % 15;
            }

            mapMod.release();
            mapCon.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private byte[] tobyte(MapTile[] row, int count){
        byte[] temp = new byte[row.length+2];
        temp[0] = 1;
        temp[1] = (byte) count;
        for (int i = 2; i < temp.length; i++) {
            temp[i]=row[i-2].toByte();
        }
        return temp;
    }

    public void goToRestartState(){
        coordSender.interrupt();
        mapMaker.interrupt();
        dispose();
        gsm.set(new RestartState(gsm));
    }
}