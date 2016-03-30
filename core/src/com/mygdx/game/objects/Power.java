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
    public Power(PowerType powerType, float x, float y, float width, float height){
        super(x, y, width, height);
        this.type = powerType;
        switch(powerType){
            case FREEZE_MAZE:
                this.setImage(new Texture(Gdx.files.internal("power_freeze.png")));
                break;
            case SPEED_PLAYER_UP:
                this.setImage(new Texture(Gdx.files.internal("power_speed_up.png")));
                break;
            case SLOW_GAME_DOWN:
                this.setImage(new Texture(Gdx.files.internal("power_slow_down.png")));
                break;
            case DESTROY_WALL:
                this.setImage(new Texture(Gdx.files.internal("power_destroy_walls.png")));
                break;
            default:
                this.setImage(new Texture(Gdx.files.internal("power_ghost.png")));
                break;
        }
    }

    @Override
    public boolean collides(Player player, PlayState game) {
        if (player.overlaps(this)){
            if (isPassive()) {
                player.setPassivePowerState(true);
                player.setPassivePower(type);
                player.setEndPassivePowerTime(System.currentTimeMillis()+5000);
            }
            else {
                player.setActivePower(type);
            }
            return true;
        }
        return false;
    }

    public boolean isPassive() {
        int index=0;
        for (int i=0; i<PowerType.values().length; i++) {
            if (type.equals(PowerType.values()[i])) {
                index = i;
                break;
            }
        }
        return (index<5);
    }
}