package com.mygdx.game.objects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.customEnum.PowerType;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Player extends Movable {
    private PowerType passivePower;
    private PowerType activePower;
    private long endPassivePowerTime;
    private long endActivePowerTime;

    public Player(){
        super(480/2-50/2, 400, 40, 40);
        this.setImage(new Texture(Gdx.files.internal("player_temp.png")));
        this.activePower = this.passivePower = PowerType.NOTHING;
        endActivePowerTime = endPassivePowerTime = System.currentTimeMillis();
    }

    public long getEndPassivePowerTime() {
        return endPassivePowerTime;
    }

    public void setEndPassivePowerTime(long endPassivePowerTime) {
        this.endPassivePowerTime = endPassivePowerTime;
    }

    public long getEndActivePowerTime() {
        return endActivePowerTime;
    }

    public void setEndActivePowerTime(long endActivePowerTime) {
        this.endActivePowerTime = endActivePowerTime;
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

    public boolean canGoThrough() {
        return (passivePower.equals(PowerType.GO_THROUGH_WALL));
    }

    public boolean canDestroy() {
        return (activePower.equals(PowerType.DESTROY_WALL));
    }

    public boolean isDead() {
        return y>=0;
    }
}