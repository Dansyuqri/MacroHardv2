package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.states.PlayState;
import com.mygdx.game.customEnum.PowerType;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Power extends GameObject {
    private PowerType type;
    public Power(PowerType powerType, int i){
        super();
        this.setImage(new Texture(Gdx.files.internal("droplet.png")));
        this.type = powerType;
        this.x = (50 * i) + 15;
        this.y = PlayState.sideWalls.get(PlayState.sideWalls.size()-1).y+50;
        this.width = 50;
        this.height = 50;
    }

    public PowerType getType() {
        return type;
    }
}
