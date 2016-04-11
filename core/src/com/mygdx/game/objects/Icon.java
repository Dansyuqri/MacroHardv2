package com.mygdx.game.objects;


/**
 * Created by hj on 24/3/16.
 */
public class Icon extends GameObject {
    public Icon(Power power){
        super(30, 30, 50, 50);
        this.setImage(power.getImage());
    }
}
