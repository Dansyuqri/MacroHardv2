package com.mygdx.game.customEnum;


/**
 * Created by Samuel on 23/3/2016.
 */
public enum MapTile {
    EMPTY, OBSTACLES, POWER, DOOR, SWITCH, SPIKES;

    public byte toByte(){
        for (int i = 0; i < values().length; i++) {
            if (this.equals(values()[i])){
                return (byte) i;
            }
        }
        return (byte) -1;
    }

    public MapTile fromByte(byte b){
        for (int i = 0; i < values().length; i++) {
            if (b == (byte) i){
                return values()[i];
            }
        }
        return EMPTY;
    }
}