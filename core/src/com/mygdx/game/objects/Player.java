package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.customEnum.PowerType;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Player extends GameObject {
    private PowerType passivePower;
    private PowerType activePower;
    private Texture texture;
    public Player(){
        super();
        texture = new Texture(Gdx.files.internal("player_temp.png"));
        this.x = 480/2-50/2;
        this.y = 400;
        this.width = 40;
        this.height = 40;
        this.activePower = this.passivePower = PowerType.NOTHING;
    }

    public PowerType getActivePower() {
        return activePower;
    }

    public void setActivePower(PowerType activePower) {
        this.activePower = activePower;
    }

    public void setPassivePower(PowerType power) {
        this.passivePower = power;
    }

    public PowerType getPassivePower() {
        return passivePower;
    }

    public Texture getTexture(){
        return texture;
    }

    public boolean canGoThrough() {
        return (passivePower.equals(PowerType.GO_THROUGH_WALL) || activePower.equals(PowerType.DESTROY_WALL));
    }
}
