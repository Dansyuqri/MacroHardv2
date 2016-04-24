package com.mygdx.game.Interface;

import com.mygdx.game.objects.Player;
import com.mygdx.game.states.PlayState;

/**
 * Created by Samuel on 23/3/2016.
 */

/**
 * This interface is used to see if entities in game are collidable or not
 */
public interface Collidable {
    boolean collides(Player player, PlayState game);
}
