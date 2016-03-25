package com.mygdx.game;

import com.mygdx.game.MacroHardv2;

/**
 * Created by Nayr on 12/3/2016.
 */
public interface ActionResolver {
    public void SignIn();
    public void SignOut();
    public boolean isSignedIn();
    public void QuickGame();
    public void initMatch();
    public void setGame(MacroHardv2 game);
    public void sendPos(float x,float y);
    public void sendMap(boolean[] map);
    public String gethostid();
    public String getyourid();
    public void Inviteplayers();
    public void Acceptinvites();
    public void Seeinvites();
}
