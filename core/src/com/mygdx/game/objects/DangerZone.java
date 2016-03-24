package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Samuel on 22/3/2016.
 */
public class DangerZone extends GameObject{
    public DangerZone(int y){
        super(0, y, 480, 200);
        this.setImage(new Texture(Gdx.files.internal("dangerzone.png")));
    }
}
