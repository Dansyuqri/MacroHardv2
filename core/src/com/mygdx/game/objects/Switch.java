package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Switch extends Movable implements Collidable{
    private static int nextID;
    private int id;
    private boolean selfOn = false;
    private AtomicBoolean otherOn = new AtomicBoolean(false);

    public Switch(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height);
        id = nextID - 80;
        nextID = (nextID+1)%200;
        this.setImage(new Texture(Gdx.files.internal("pressure_plate1.png")));
    }

    public int getId() {
        return id;
    }

    public static void reset(){
        nextID = 0;
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return (player.overlaps(this));
    }

    public void setOn(){
        if (!selfOn) {
            this.setImage(new Texture(Gdx.files.internal("pressure_plate1_pressed.png")));
            selfOn = true;
        }
    }

    public void setOff()
    {
        if (selfOn) {
            this.setImage(new Texture(Gdx.files.internal("pressure_plate1.png")));
            selfOn = false;
        }
    }

    public void setOtherOn(){
        otherOn.compareAndSet(false, true);
    }

    public void setOtherOff()
    {
       otherOn.compareAndSet(true, false);
    }

    public boolean isOtherOn() {
        return otherOn.get();
    }
}