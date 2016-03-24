package com.mygdx.game.states;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.customEnum.MapTile;
import com.mygdx.game.objects.Switch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by hj on 19/3/16.
 */
public class PlayStateHost extends PlayState {
    private int doorCounter;
    private int powerCounter;
    private int[][] memory;

    public PlayStateHost(GameStateManager gsm){
        super(gsm);
        //spawning initialization
        doorCounter = 0;
        powerCounter = 0;
        memory = new int[5][9];
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 9; j++){
                memory[i][j] = 0;
            }
        }
        MapMaker mapMaker = new MapMaker(this);
        mapMaker.start();
    }

    void wallCoord(){
        powerCounter += 1;
        doorCounter += 1;
        boolean test = false;
        int out_index = 0;
        MapTile[] new_row = {MapTile.OBSTACLES, MapTile.OBSTACLES, MapTile.OBSTACLES, MapTile.OBSTACLES, MapTile.OBSTACLES, MapTile.OBSTACLES, MapTile.OBSTACLES, MapTile.OBSTACLES, MapTile.OBSTACLES};

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

        // spawning power ups after a certain time
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
        if (doorCounter > 45){
            for (int i = 0; i < current.length; i++) {
                if (new_row[i] == MapTile.EMPTY) {
                    new_row[i] = MapTile.DOOR;
                }
            }
            doorCounter = 0;
        }

        // updating the memory
        // 0 = wall, 1 = empty but unreachable, 2 = empty and reachable
        for (int i = memory.length-1; i > 0; i--){
            System.arraycopy(memory[i-1],0,memory[i],0,memory[i-1].length);
        }
        for (int i = 0; i < new_row.length; i++){
            if (new_row[i] == MapTile.OBSTACLES){
                memory[0][i] = 0;
            }
            else if (new_row[i] == MapTile.EMPTY){
                memory[0][i] = 1;
            }
            if (current[i]){
                memory[0][i] = 2;
            }
        }
        for (int i = 1; i < memory.length; i++){
            int temp = 0;
            for (int j = 0; j < memory[i].length; j++){
                if ((memory[i-1][j] == 2) && (memory[i][j] == 1)){
                    memory[i][j] = 2;
                    temp = j;
                }
            }
            for (int j = 0; j < memory[i].length; j++){
                if (temp-j >= 0){
                    if ((memory[i][temp-j] == 1) && (memory[i][temp-j+1] == 2)){
                        memory[i][temp-j] = 2;
                    }
                }
                if ((temp+j < 9) && (temp+j-1 > 0)){
                    if ((memory[i][temp+j] == 1) && (memory[i][temp+j-1] == 2)){
                        memory[i][temp+j] = 2;
                    }
                }
            }
        }

        int temp0 = 0;
        int temp1 = 0;

        // spawning door switch
        boolean switchCoord = false;
        if (doorCounter == 44){
            switchCoord = true;
            while (true){
                temp0 = MathUtils.random(0,8);
                temp1 = MathUtils.random(0,4);
                if (memory[temp1][temp0] == 2){
                    break;
                }
            }
        }

        synchronized (this) {
            while (mapBuffer.size() > 5){
                try {
                    wait();
                } catch (InterruptedException ignored){}
            }
            mapBuffer.add(new_row);
            if (switchCoord) {
                mapBuffer.get(5 - temp1)[temp0] = MapTile.SWITCH;
            }
            notifyAll();
        }
    }
}