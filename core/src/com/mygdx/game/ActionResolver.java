package com.mygdx.game;

import com.mygdx.game.MacroHardv2;

/**
 * Created by Nayr on 12/3/2016.
 */
public interface ActionResolver {
    void SignIn();
    void SignOut();
    boolean isSignedIn();
    void QuickGame();
    void initMatch();
    void setGame(MacroHardv2 game);

    void sendPing(byte[] ping);
    void sendReliable(byte[] message);

    String gethostid();
    String getyourid();
    void Inviteplayers();
    void Acceptinvites();
    void Seeinvites();
    int getmyidint();
}
