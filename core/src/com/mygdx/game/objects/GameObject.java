package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class GameObject extends Rectangle {
    private Texture image;

    public Texture getImage() {
        return image;
    }
    public void setImage(Texture image) {
        this.image = image;
    }
    public void draw(SpriteBatch sb){
        sb.draw(image, x, y);
    }
}
