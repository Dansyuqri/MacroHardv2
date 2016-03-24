package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class SideWall extends Movable {
    public SideWall(int spriteHeight, float y, int i){
        super(465*i, y, 15, spriteHeight);
        this.setImage(new Texture(Gdx.files.internal("wall4.2.png")));
    }
}