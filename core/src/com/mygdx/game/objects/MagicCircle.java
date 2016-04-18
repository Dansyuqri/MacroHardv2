package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.MacroHardv2;
import com.mygdx.game.customEnum.MessageCode;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;

import javax.print.attribute.standard.MediaSize;

/**
 * Created by Samuel on 14/4/2016.
 */
public class MagicCircle extends Movable implements Collidable {
    private static int nextID;
    private int id;
    private boolean on = false;
    private boolean otherOn = false;

    public MagicCircle(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height);
        id = nextID - 80;
        nextID = (nextID+1)%200;
        this.setImage(new Texture(Gdx.files.internal("magic_circle.png")));
    }

    public int getId() {
        return id;
    }

    public boolean getOtherOn() {
        return otherOn;
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
            MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.MAGIC_CIRCLE_OFF, (byte) id});
            this.setImage(new Texture(Gdx.files.internal("magic_circle.png")));
            on = false;
        }
    }

    public boolean isOn() {
        return on;
    }
    public void OtherOn(){
        otherOn = true;
    }
    public void OtherOff(){
        otherOn = false;
    }
}