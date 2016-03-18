package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.states.PlayState;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Power extends GameObject {
    private String type;
    public Power(String type, int i){
        super();
        this.setImage(new Texture(Gdx.files.internal("droplet.png")));
        this.type = type;
        this.x = (50 * i) + 15;
        this.y = PlayState.sideWalls.get(PlayState.sideWalls.size()-1).y+50;
        this.width = 50;
        this.height = 50;
    }

    public String getType() {
        return type;
    }
}
