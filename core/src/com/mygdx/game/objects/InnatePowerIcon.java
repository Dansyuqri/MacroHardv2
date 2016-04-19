package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.customEnum.PowerType;

/**
 * Created by MinhBreaker on 19/4/16.
 */
public class InnatePowerIcon extends GameObject {
    PowerType powerType;
    public InnatePowerIcon(PowerType powerType){
        super(90, 30, 50, 50);
        this.powerType = powerType;
        switch (powerType) {
            case DESTROY_WALL:
                this.setImage(new Texture(Gdx.files.internal("power_destroy_walls.png")));
                break;
            case INVINCIBLE:
                // TODO: change image
                this.setImage(new Texture(Gdx.files.internal("power_ghost.png")));
                break;
        }
    }
    public void changeIcon(boolean available) {
        switch (powerType) {
            case DESTROY_WALL:
                if (available) this.setImage(new Texture(Gdx.files.internal("power_destroy_walls.png")));
                else this.setImage(new Texture(Gdx.files.internal("power_destroy_walls_unavailable.png")));
                break;
            case INVINCIBLE:
                if (available) this.setImage(new Texture(Gdx.files.internal("power_ghost.png")));
                else this.setImage(new Texture(Gdx.files.internal("power_ghost_unavailable.png")));
                break;
        }
    }
}