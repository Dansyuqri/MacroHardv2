package com.mygdx.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.example.games.basegameutils.GameHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nayr on 10/3/2016.
 */
public class GSGameHelper extends GameHelper implements RoomUpdateListener, RealTimeMessageReceivedListener,RoomStatusUpdateListener,OnInvitationReceivedListener {
    final static String TAG = "MacroHard";
    static final int RC_SELECT_PLAYERS = 10000;
    static final int RC_WAITING_ROOM = 10002;
    final static int RC_INVITATION_INBOX = 10001;
    private Activity activity;
    private String mRoomID;
    private MacroHardv2 game;
    private ArrayList<Participant> invitees = null;
    private String mRoomId = null;
    public String mMyId = null;
    public String host;
    public int myidno;
    String mIncomingInvitationId = null;


    public GSGameHelper(Activity activity, int clientsToUse) {
        super(activity, clientsToUse);
        this.activity = activity;
    }

    public void quickGame(){
        Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        RoomConfig roomConfig = roomConfigBuilder.build();
        Games.RealTimeMultiplayer.create(getApiClient(), roomConfig);
    }

    public void initMatch(){
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(getApiClient(), 1, 1);
        this.activity.startActivityForResult(intent, RC_SELECT_PLAYERS);

    }

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder((RoomUpdateListener) this)
                .setMessageReceivedListener((RealTimeMessageReceivedListener) this)
                .setRoomStatusUpdateListener((RoomStatusUpdateListener) this);
    }
    public void onActivityResult(int request,int response, Intent data){
        System.out.println("Room Created");
        if (request == GSGameHelper.RC_WAITING_ROOM){
            try {
                if (response == Activity.RESULT_CANCELED || response == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    Games.RealTimeMultiplayer.leave(getApiClient(), this, mRoomID);
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    BaseGameUtils.showAlert(activity, "Left Room");
                } else if (response == Activity.RESULT_OK) {
                    Participant host = invitees.get(0);
                    this.host = host.getParticipantId();
                    for (int i = 0; i < invitees.size(); i++) {
                        if (mMyId.equals(invitees.get(i).getParticipantId())) {
                            this.myidno = i;
                        }
                    }
                    this.game.multiplayerGameReady();
                }
            }
            catch (IllegalStateException illegal){
                BaseGameUtils.showAlert(activity, "Left Room");
            }

        }
        else if (request == GSGameHelper.RC_SELECT_PLAYERS){
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            Bundle extras = data.getExtras();
            final ArrayList<String> invitees =
                    data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get auto-match criteria
            Bundle autoMatchCriteria = null;
            int minAutoMatchPlayers =
                    data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers =
                    data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
            Gdx.app.log("J", "Jmin" + minAutoMatchPlayers + " Jmax:" + maxAutoMatchPlayers);
            for (String invitee : invitees){
                Gdx.app.log("L" , invitee);
            }
            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            // create the room and specify a variant if appropriate
            RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
            roomConfigBuilder.addPlayersToInvite(invitees);
            if (autoMatchCriteria != null) {
                roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }
            RoomConfig roomConfig = roomConfigBuilder.build();
            Games.RealTimeMultiplayer.create(getApiClient(), roomConfig);

            // prevent screen from sleeping during handshake
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }
        else if(request == GSGameHelper.RC_INVITATION_INBOX){
            handleInvitationInboxResult(response, data);
        }
        else{
            super.onActivityResult(request, response, data);
        }
    }

    @Override
    public void onJoinedRoom(int arg0, Room arg1) {
        if (arg0 != GamesStatusCodes.STATUS_OK) {
            Gdx.app.log("R", "Joined FAILED");
        }else{
            Gdx.app.log("R", "Joined Room");
        }
        Gdx.app.log("R", "Room Created");
        mRoomID = arg1.getRoomId();
        System.out.println("HEHE: ROOM CREATED" + mRoomID);
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(getApiClient(), arg1, 2);
        this.activity.startActivityForResult(i, RC_WAITING_ROOM);
    }

    @Override
    public void onLeftRoom(int arg0, String arg1) {
        BaseGameUtils.makeSimpleDialog(activity, "Abandoned Game");
        Gdx.app.log("LEAVE", "Left Room");

    }

    @Override
    public void onRoomConnected(int arg0, Room arg1) {
        Log.d(TAG, "onRoomConnected(" + arg0 + ", " + arg1 + ")");
        if (arg0 != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + arg0);
            return;
        }
        updateRoom(arg1);

    }

    void updateRoom(Room room) {
        if (room != null) {
            invitees = room.getParticipants();
        }
    }

    public void setGame(MacroHardv2 game){
        this.game = game;
    }

    @Override
    public void onRoomCreated(int arg0, Room arg1) {
        if (arg0 != GamesStatusCodes.STATUS_OK) {
            //BaseGameUtils.showAlert(activity, "Room creation error");
            BaseGameUtils.makeSimpleDialog(activity, "Error Creating Room", "Room creation error " + arg0).show();
            Gdx.app.log("R", "Room Created FAILED");
        }else{
            Gdx.app.log("R", "Room Created");
            mRoomID = arg1.getRoomId();
            Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(getApiClient(), arg1, 2);
            this.activity.startActivityForResult(i, RC_WAITING_ROOM);
        }
    }

    public void sendPing(byte[] message){
        try{
            Games.RealTimeMultiplayer.sendUnreliableMessageToOthers(getApiClient(), message, mRoomID);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendReliable(byte[] message){
        try{
            for (Participant p : invitees) {
                if (p.getParticipantId().equals(mMyId))
                    continue;
                if (p.getStatus() != Participant.STATUS_JOINED)
                    continue;
                // final score notification must be sent via reliable message
                Games.RealTimeMultiplayer.sendReliableMessage(getApiClient(), null, message,
                        mRoomId, p.getParticipantId());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        byte[] message = rtm.getMessageData();
        game.getGsm().update(message);
    }

    @Override
    public void onConnectedToRoom(Room arg0) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onConnectedToRoom.");

        //get participants and my ID:
        invitees = arg0.getParticipants();
        mMyId = arg0.getParticipantId(Games.Players.getCurrentPlayerId(getApiClient()));

        // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
        if(mRoomId==null)
            mRoomId = arg0.getRoomId();

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + mRoomId);
        Log.d(TAG, "My ID " + mMyId);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");


    }

    @Override
    public void onDisconnectedFromRoom(Room arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onP2PConnected(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onP2PDisconnected(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPeerDeclined(Room arg0, List<String> arg1) {
        updateRoom(arg0);
        // TODO Auto-generated method stub

    }

    @Override
    public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {
        // TODO Auto-generated method stub
        updateRoom(arg0);

    }

    @Override
    public void onPeerJoined(Room arg0, List<String> arg1) {
        updateRoom(arg0);
        // TODO Auto-generated method stub

    }

    @Override
    public void onPeerLeft(Room arg0, List<String> arg1) {
        updateRoom(arg0);
        // TODO Auto-generated method stub

    }

    @Override
    public void onPeersConnected(Room arg0, List<String> arg1) {
        updateRoom(arg0);
        // TODO Auto-generated method stub

    }

    @Override
    public void onPeersDisconnected(Room arg0, List<String> arg1) {
        updateRoom(arg0);
        // TODO Auto-generated method stub

    }

    @Override
    public void onRoomAutoMatching(Room arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRoomConnecting(Room arg0) {
        // TODO Auto-generated method stub

    }
    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Log.d(TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
        }

        // create the room
        Log.d(TAG, "Creating room...");
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        Games.RealTimeMultiplayer.create(getApiClient(), rtmConfigBuilder.build());
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        acceptInviteToRoom(inv.getInvitationId());
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        Games.RealTimeMultiplayer.join(getApiClient(), roomConfigBuilder.build());
    }

    // Called when we get an invitation to play a game. We react by showing that to the user.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        // We got an invitation to play a game! So, store it in
        // mIncomingInvitationId
        // and show the popup on the screen.
        mIncomingInvitationId = invitation.getInvitationId();
        seeinvites();
        //try to do popup TO DO
        /*((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                invitation.getInviter().getDisplayName() + " " +
                        getString(R.string.is_inviting_you));
        switchToScreen(mCurScreen); // This will show the invitation popup*/
    }

    @Override
    public void onInvitationRemoved(String invitationId) {

        if (mIncomingInvitationId.equals(invitationId)&&mIncomingInvitationId!=null) {
            mIncomingInvitationId = null;
        }

    }

    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected() called. Sign in successful!");

        Log.d(TAG, "Sign-in succeeded.");

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        Games.Invitations.registerInvitationListener(getApiClient(), this);

        if (connectionHint != null) {
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
            Invitation inv = connectionHint
                    .getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null && inv.getInvitationId() != null) {
                // retrieve and cache the invitation ID
                Log.d(TAG,"onConnected: connection hint has a room invite!");
                acceptInviteToRoom(inv.getInvitationId());
            }
        }

    }

    public void inviteplayers(){
        Intent intent;
        intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(getApiClient(), 1, 1);
        activity.startActivityForResult(intent, RC_SELECT_PLAYERS);
    }
    public void seeinvites(){
        Intent intent;
        intent = Games.Invitations.getInvitationInboxIntent(getApiClient());
        activity.startActivityForResult(intent, RC_INVITATION_INBOX);
    }
    public void acceptinvites(){
        System.out.println("I am invite " + mIncomingInvitationId);
        acceptInviteToRoom(mIncomingInvitationId);
        mIncomingInvitationId = null;
    }

    void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        if (mRoomId != null) {
            System.out.println("HEHE: LEFT ROOM" + mRoomID);
            Games.RealTimeMultiplayer.leave(getApiClient(), this, mRoomId);
            mRoomId = null;
        }
    }
    public void submitScoreGPGS(int score){
        Games.Leaderboards.submitScore(getApiClient(), "CgkIvIDL488DEAIQAQ", score);
    }
    public void getLeaderboardGPGS(){
        this.activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), "CgkIvIDL488DEAIQAQ"), 100);

    }
}
