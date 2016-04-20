package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.MacroHardv2;
import com.mygdx.game.customEnum.MessageCode;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.print.attribute.standard.MediaSize;

/**
 * Created by Samuel on 14/4/2016.
 */
public class MagicCircle extends Movable implements Collidable {
    private static int nextID;
    private int id;
    private boolean on = false;
    private boolean sent = false;
    private AtomicBoolean otherOn = new AtomicBoolean(false);

    public MagicCircle(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height);
        id = nextID - 80;
        nextID = (nextID+1)%200;
        this.setImage(new Texture(Gdx.files.internal("magic_circle.png")));
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public int getId() {
        return id;
    }

    public boolean getOtherOn() {
        return otherOn.get();
    }

    public static void reset(){
        nextID = 0;
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return (player.overlaps(this));
    }

    public void setOn() {
        if (!on) {
            this.setImage(new Texture(Gdx.files.internal("magic_circle(active).png")));
            on = true;
        }
    }

    public void setOff() {
        if (on) {
            this.setImage(new Texture(Gdx.files.internal("magic_circle.png")));
            on = false;
        }
    }

    public boolean isOn() {
        return on;
    }
    public void OtherOn(){
        otherOn.compareAndSet(false, true);
    }
    public void OtherOff(){
        otherOn.compareAndSet(true, false);
    }
}