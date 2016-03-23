package com.mygdx.game.Interface;

import com.mygdx.game.objects.Player;
import com.mygdx.game.states.PlayState;

/**
 * Created by Samuel on 23/3/2016.
 */
public interface Collidable {
    boolean collides(Player player, PlayState game);
}
