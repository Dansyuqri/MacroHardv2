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
            case FREEZE_MAZE:
                this.setImage(new Texture(Gdx.files.internal("power_freeze.png")));
                break;
            case SLOW_GAME_DOWN:
                this.setImage(new Texture(Gdx.files.internal("power_slow_down.png")));
                break;
            case SPEED_PLAYER_UP:
                this.setImage(new Texture(Gdx.files.internal("power_speed_up.png")));
                break;
            case TELEPORT:
                this.setImage(new Texture(Gdx.files.internal("power_teleport.png")));
                break;
        }
    }
}
