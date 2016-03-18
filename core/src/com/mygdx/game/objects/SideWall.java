package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class SideWall extends GameObject {
    public SideWall(int spriteWidth, int spriteHeight, float in, int i){
        super();
        this.setImage(new Texture(Gdx.files.internal("wall4.2.png")));
        this.x = (465*i);
        this.y = in;
        this.width = 15;
        this.height = spriteHeight;
    }
}