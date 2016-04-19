package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.customEnum.PowerType;

/**
 * Created by MinhBreaker on 19/4/16.
 */
public class InnatePowerIcon extends GameObject {
    PowerType powerType;
    private boolean available = true;
    public InnatePowerIcon(PowerType powerType){
        super(340, 30, 50, 50);
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
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void refreshIcon() {
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