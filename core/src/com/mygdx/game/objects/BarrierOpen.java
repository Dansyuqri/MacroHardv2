package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Syuqri on 3/16/2016.
 */
public class BarrierOpen extends Obstacle{
    public BarrierOpen(){
        super();
        this.setImage(new Texture(Gdx.files.internal("gate_open.png")));
    }
}
