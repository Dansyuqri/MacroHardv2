package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Door extends Movable implements Collidable{
    private static int nextID = 0;
    private int id;
    private boolean open = false;
    private boolean destroyed = false;

    public Door(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height);
        id = nextID;
        nextID = (nextID+1)%200;
        this.setImage(new Texture(Gdx.files.internal("gate_closed.png")));
    }

    public static void reset(){
        nextID = 0;
    }

    public int getId() {
        return id;
    }

    public void setOpen(){
        if (!open) {
            open = true;
            this.setImage(new Texture(Gdx.files.internal("gate_open.png")));
        }
    }

    public void setClose(){
        if (open) {
            open = false;
            this.setImage(new Texture(Gdx.files.internal("gate_closed.png")));
        }
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        if (player.overlaps(this) && !open) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}