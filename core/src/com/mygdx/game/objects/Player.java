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
    private boolean passivePowerState, passivePowerEffectTaken, activePowerState, activePowerEffectTaken, canDestroy;

    public Player(){
        super(480 / 2 - 50 / 2, 400, 40, 40);
        this.setImage(new Texture(Gdx.files.internal("player_temp.png")));
        this.activePower = this.passivePower = PowerType.NOTHING;
        endActivePowerTime = endPassivePowerTime = System.currentTimeMillis();
        passivePowerState = passivePowerEffectTaken = activePowerState = activePowerEffectTaken = canDestroy = false;
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

    public boolean getCanDestroy() {
        return canDestroy;
    }

    public void setCanDestroy(boolean canDestroy) {
        this.canDestroy = canDestroy;
    }

    public void setActivePowerEffectTaken(boolean activePowerEffectTaken) {
        this.activePowerEffectTaken = activePowerEffectTaken;
    }

    public void setPassivePowerEffectTaken(boolean passivePowerEffectTaken) {
        this.passivePowerEffectTaken = passivePowerEffectTaken;
    }

    public void setPassivePowerState(boolean passivePowerState) {
        this.passivePowerState = passivePowerState;
    }

    public void setActivePowerState(boolean activePowerState) {
        this.activePowerState = activePowerState;
    }

    public boolean getActivePowerEffectTaken() {
        return activePowerEffectTaken;
    }
    public boolean getPassivePowerEffectTaken() {
        return passivePowerEffectTaken;
    }
    public boolean getPassivePowerState() {
        return passivePowerState;
    }
    public boolean getActivePowerState() {
        return activePowerState;
    }
}