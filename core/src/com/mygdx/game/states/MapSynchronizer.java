package com.mygdx.game.states;

import com.mygdx.game.objects.Movable;

/**
 * Created by hj on 5/4/16.
 */
public class MapSynchronizer extends Movable{

    private boolean[] set;
    private float[] yOffset;

    MapSynchronizer(){
        super(0, 450, 0, 0);
        set = new boolean[]{false, false, false};
        yOffset = new float[3];
    }

    public void set(int id){
        set[id] = true;
        yOffset[id] = 450 - y;
    }

    public float offset(float y, int id){
        return y - yOffset[id];
    }

    public boolean isSet(int id){
        return set[id];
    }

}
