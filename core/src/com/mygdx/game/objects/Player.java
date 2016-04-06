package com.mygdx.game.objects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.customEnum.Direction;
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

    private static final int        FRAME_COLS = 5;
    private static final int        FRAME_ROWS = 4;

    Texture                         walkSheet;
    TextureRegion[]                 walkFramesNorth, walkFramesEast, walkFramesSouth, walkFramesWest;
    TextureRegion                   faceNorth, faceSouth, faceEast, faceWest;
    Animation                       walkAnimationNorth, walkAnimationSouth, walkAnimationEast, walkAnimationWest;

    public Player(int id){
        super(480 / 2 - 50 / 2, 450, 40, 40);
        walkSheet = new Texture(Gdx.files.internal("Player_sprite.png"));
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/FRAME_COLS, walkSheet.getHeight()/FRAME_ROWS);              // #10
        walkFramesNorth = new TextureRegion[4];
        walkFramesSouth = new TextureRegion[4];
        walkFramesEast = new TextureRegion[4];
        walkFramesWest = new TextureRegion[4];

        for(int i = 0; i < FRAME_COLS-1; i++){
            walkFramesNorth[i] = tmp[0][i];
            walkFramesWest[i] = tmp[1][i];
            walkFramesSouth[i] = tmp[2][i];
            walkFramesEast[i] = tmp[3][i];
        }
        faceNorth = tmp[0][FRAME_COLS-1];
        faceWest = tmp[1][FRAME_COLS-1];
        faceSouth = tmp[2][FRAME_COLS-1];
        faceEast = tmp[3][FRAME_COLS-1];

        walkAnimationNorth = new Animation(0.2f, walkFramesNorth);
        walkAnimationSouth = new Animation(0.2f, walkFramesSouth);
        walkAnimationEast = new Animation(0.25f, walkFramesEast);
        walkAnimationWest = new Animation(0.25f, walkFramesWest);
        switch (id) {
            case 0:
                this.setImage(faceNorth);
                break;
            case 1:
                this.setImage(faceNorth);
                break;
            case 2:
                this.setImage(faceNorth);
                break;
        }
        this.activePower = this.passivePower = PowerType.NOTHING;
        endActivePowerTime = endPassivePowerTime = System.currentTimeMillis();
        passivePowerState = passivePowerEffectTaken = activePowerState = activePowerEffectTaken = canDestroy = false;
    }

    @Override
    public void draw(SpriteBatch sb){
        sb.draw(playImage, x, y);
    }

    public void setOrientation(Direction orientation){
        if (orientation == Direction.NORTH){
            this.setImage(faceNorth);
        }
        else if (orientation == Direction.SOUTH){
            this.setImage(faceSouth);
        }
        else if (orientation == Direction.EAST){
            this.setImage(faceEast);
        }
        else if (orientation == Direction.WEST){
            this.setImage(faceWest);
        }
    }

    public void setCurrentFrame(Direction orientation, float stateTime, boolean check){
        if (orientation == Direction.NORTH){
            this.setImage(walkAnimationNorth.getKeyFrame(stateTime, check));
        }
        else if (orientation == Direction.SOUTH){
            this.setImage(walkAnimationSouth.getKeyFrame(stateTime, check));
        }
        else if (orientation == Direction.EAST){
            this.setImage(walkAnimationEast.getKeyFrame(stateTime, check));
        }
        else {
            this.setImage(walkAnimationWest.getKeyFrame(stateTime, check));
        }
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