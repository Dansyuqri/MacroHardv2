package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.states.PlayState;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Switch extends Obstacle {
    public Switch(int spriteWidth,int spriteHeight,int i){
        super();
        this.setImage(new Texture(Gdx.files.internal("switch_off.png")));
        this.x = (spriteWidth * i) + 15;
        this.y = PlayState.sideWalls.get(PlayState.sideWalls.size()-1).y+50;
        this.width = spriteWidth;
        this.height = spriteHeight;
    }
}