package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.customEnum.PowerType;

/**
 * Created by hj on 24/3/16.
 */
public class ActivePowerIcon extends GameObject {
    public ActivePowerIcon(PowerType powerType){
        super(400, 30, 50, 50);
        switch (powerType) {
            case TELEPORT:
                //TODO: change image
                this.setImage(new Texture(Gdx.files.internal("power_destroy_walls.png")));
                break;
        }
    }
}
