package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;

/**
 * Created by Samuel on 21/3/2016.
 */
public class Overlay extends Movable{
    public Overlay(float y){
        super(0, y, 480, 200);
        if (PlayState.stage == Stage.DUNGEON) {
            this.setImage(new Texture(Gdx.files.internal("effects1.png")));
        }
        else if (PlayState.stage == Stage.ICE){
            this.setImage(new Texture(Gdx.files.internal("effects2.png")));
        }
    }
}
