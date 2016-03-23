package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.states.PlayState;
import com.mygdx.game.customEnum.PowerType;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Power extends Movable implements Collidable {
    private PowerType type;
    public Power(PowerType powerType, int i){
        super();
        this.setImage(new Texture(Gdx.files.internal("droplet.png")));
        this.type = powerType;
        this.x = (50 * i) + 15;
        this.y = PlayState.tracker;
        this.width = 50;
        this.height = 50;
    }

    @Override
    public boolean collide(Player player, PlayState game) {
        if (player.overlaps(this)){
            if (PlayState.isPassive(this)) {
                player.setPassivePower(power.getType());
                passivePowerState = true;
                endPassivePowerTime = System.currentTimeMillis()+5000;
            }
            else player.setActivePower(power.getType());
            powers.remove(power);
        }
        return false;
    }

    public PowerType getType() {
        return type;
    }
}