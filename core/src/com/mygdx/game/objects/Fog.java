package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.states.PlayState;

/**
 * Created by Samuel on 5/4/2016.
 */
public class Fog extends Movable {

    public Fog(float x, float y){
        super(x, y, 960, 200);
        this.setImage(new Texture(Gdx.files.internal("fog.png")));
    }

    @Override
    public void scroll (float gameSpeed){
        y -= gameSpeed * Gdx.graphics.getDeltaTime();
        x -= 50 * Gdx.graphics.getDeltaTime();
    }
}
