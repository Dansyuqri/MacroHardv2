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
    private PowerType passivePower, activePower, innatePower;
    private boolean canDestroy, isInvicible;
    private boolean isSlowed;
    float prev_x, prev_y;
    
    private static final int        FRAME_COLS = 5;
    private static final int        FRAME_ROWS = 8;

    Texture                         walkSheet;
    TextureRegion[]                 walkFramesNorth, walkFramesEast, walkFramesSouth, walkFramesWest, walkFramesNorthEast, walkFramesNorthWest, walkFramesSouthEast, walkFramesSouthWest;
    TextureRegion                   faceNorth, faceSouth, faceEast, faceWest, faceNorthEast, faceNorthWest, faceSouthEast, faceSouthWest;
    Animation                       walkAnimationNorth, walkAnimationSouth, walkAnimationEast, walkAnimationWest, walkAnimationNorthEast, walkAnimationNorthWest, walkAnimationSouthEast, walkAnimationSouthWest;
    TextureRegion                   playImage;
    Direction                       orientation;

    public Player(int id){
        super(480 / 2 - 50 / 2, 450, 40, 40);
        switch (id) {
            case 0:
                innatePower = PowerType.DESTROY_WALL;
                walkSheet = new Texture(Gdx.files.internal("Player_sprite.png"));
                break;
            case 1:
                innatePower = PowerType.INVINCIBLE;
                walkSheet = new Texture(Gdx.files.internal("Player2_sprite.png"));
                break;
        }
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/FRAME_COLS, walkSheet.getHeight()/FRAME_ROWS);              // #10
        walkFramesNorth = new TextureRegion[4];
        walkFramesSouth = new TextureRegion[4];
        walkFramesEast = new TextureRegion[4];
        walkFramesWest = new TextureRegion[4];
        walkFramesNorthEast = new TextureRegion[4];
        walkFramesNorthWest = new TextureRegion[4];
        walkFramesSouthEast = new TextureRegion[4];
        walkFramesSouthWest = new TextureRegion[4];

        for(int i = 0; i < FRAME_COLS-1; i++){
            walkFramesNorth[i] = tmp[0][i];
            walkFramesWest[i] = tmp[1][i];
            walkFramesSouth[i] = tmp[2][i];
            walkFramesEast[i] = tmp[3][i];
            walkFramesNorthEast[i] = tmp[7][i];
            walkFramesNorthWest[i] = tmp[4][i];
            walkFramesSouthEast[i] = tmp[6][i];
            walkFramesSouthWest[i] = tmp[5][i];
        }
        faceNorth = tmp[0][FRAME_COLS-1];
        faceWest = tmp[1][FRAME_COLS-1];
        faceSouth = tmp[2][FRAME_COLS-1];
        faceEast = tmp[3][FRAME_COLS-1];
        faceNorthEast = tmp[7][FRAME_COLS-1];
        faceNorthWest = tmp[4][FRAME_COLS-1];
        faceSouthEast = tmp[6][FRAME_COLS-1];
        faceSouthWest = tmp[5][FRAME_COLS-1];

        walkAnimationNorth = new Animation(0.2f, walkFramesNorth);
        walkAnimationSouth = new Animation(0.2f, walkFramesSouth);
        walkAnimationEast = new Animation(0.25f, walkFramesEast);
        walkAnimationWest = new Animation(0.25f, walkFramesWest);
        walkAnimationNorthEast = new Animation(0.25f, walkFramesNorthEast);
        walkAnimationNorthWest = new Animation(0.25f, walkFramesNorthWest);
        walkAnimationSouthEast = new Animation(0.25f, walkFramesSouthEast);
        walkAnimationSouthWest = new Animation(0.25f, walkFramesSouthWest);
        switch (id) {
            case 0:
                this.setImage(faceNorth);
                break;
            case 1:
                this.setImage(faceNorth);
                break;
        }
        this.activePower = this.passivePower = PowerType.NOTHING;
        canDestroy = isSlowed = isInvicible = false;

    }

    @Override
    public void draw(SpriteBatch sb){
        sb.draw(playImage, x, y);
    }

    @Override
    public Texture getImage() {
        return walkSheet;
    }

    public void setImage(TextureRegion image){
        this.playImage = image;
    }

    public void setOrientation(Direction orientation) {
        this.orientation = orientation;
    }

    public void setPrevCoord(float prev_x, float prev_y){
        this.prev_x = prev_x;
        this.prev_y = prev_y;
    }

    public float getPrev_x() {
        return prev_x;
    }

    public float getPrev_y() {
        return prev_y;
    }

    public Direction getOrientation() {
        return orientation;
    }

    public void setDirection(){
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
        else if (orientation == Direction.NORTHEAST){
            this.setImage(faceNorthEast);
        }
        else if (orientation == Direction.NORTHWEST){
            this.setImage(faceNorthWest);
        }
        else if (orientation == Direction.SOUTHWEST){
            this.setImage(faceSouthWest);
        }
        else if (orientation == Direction.SOUTHEAST){
            this.setImage(faceSouthEast);
        }
    }

    public void setCurrentFrame(float stateTime, boolean check){
        if (orientation == Direction.NORTH){
            this.setImage(walkAnimationNorth.getKeyFrame(stateTime, check));
        }
        else if (orientation == Direction.SOUTH){
            this.setImage(walkAnimationSouth.getKeyFrame(stateTime, check));
        }
        else if (orientation == Direction.EAST){
            this.setImage(walkAnimationEast.getKeyFrame(stateTime, check));
        }
        else if (orientation == Direction.WEST){
            this.setImage(walkAnimationWest.getKeyFrame(stateTime, check));
        }
        else if (orientation == Direction.NORTHEAST){
            this.setImage(walkAnimationNorthEast.getKeyFrame(stateTime, check));
        }
        else if (orientation == Direction.NORTHWEST){
            this.setImage(walkAnimationNorthWest.getKeyFrame(stateTime, check));
        }
        else if (orientation == Direction.SOUTHWEST){
            this.setImage(walkAnimationSouthWest.getKeyFrame(stateTime, check));
        }
        else if (orientation == Direction.SOUTHEAST){
            this.setImage(walkAnimationSouthEast.getKeyFrame(stateTime, check));
        }
    }

    public PowerType getActivePower() {
        return activePower;
    }

    public void setActivePower(PowerType activePower) {
        this.activePower = activePower;
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

    public boolean isSlowed() {
        return isSlowed;
    }

    public void setIsSlowed(boolean isSlowed) {
        this.isSlowed = isSlowed;
    }

    public void setIsInvicible(boolean isInvicible) {
        this.isInvicible = isInvicible;
    }

    public boolean getIsInvicible() {
        return isInvicible;
    }

    public void setPassivePower(PowerType passivePower) {
        this.passivePower = passivePower;
    }

    public PowerType getInnatePower() {
        return innatePower;
    }

    public void setInnatePower(PowerType innatePower) {
        this.innatePower = innatePower;
    }
}