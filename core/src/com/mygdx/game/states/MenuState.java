package com.mygdx.game.states;

import com.mygdx.game.MacroHardv2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Syuqri on 3/7/2016.
 */
public class MenuState extends State{
    private Texture background;
    private Texture playBtn;
    private Texture instructionBtn;
    private Texture quickGameBtn;
    private Texture sendInviteBtn;
    private Texture invitationBtn;
    private Texture signInBtn;

    private Vector3 touchPos = new Vector3(0,0,0);
    private float bufferFromTop = 20;
    private boolean touched = false;
    public static volatile boolean goToPlay = false;
    public static volatile boolean gotoPlayP = false;
    public static volatile boolean ready = false;


    //Resize variables
    private float playBtnX, playBtnY,
            instructionBtnX, instructionBtnY,
            quickGameBtnX, quickGameBtnY,
            graphicsX, graphicsY,
            sendInviteBtnX, sendInviteBtnY,
            invitationBtnX, invitationBtnY,
            signInBtnX, signInBtnY
            ;
    public MenuState(GameStateManager gsm) {
        super(gsm);
        background = new Texture("menu_bg.png");
        playBtn = new Texture("playBtn.png");
        instructionBtn = new Texture("instructionBtn.png");
        quickGameBtn = new Texture("quickGameBtn.png");
        sendInviteBtn = new Texture("sendInviteBtn.png");
        invitationBtn = new Texture("invitationBtn.png");
        signInBtn = new Texture("signInBtn.png");

        playBtnX = playBtn.getWidth()*3;
        playBtnY = playBtn.getHeight()*3;

        instructionBtnX = instructionBtn.getWidth()*3;
        instructionBtnY = instructionBtn.getHeight()*3;

        quickGameBtnX = quickGameBtn.getWidth()*3;
        quickGameBtnY = quickGameBtn.getHeight()*3;

        sendInviteBtnX = sendInviteBtn.getWidth()*3;
        sendInviteBtnY = sendInviteBtn.getHeight()*3;

        invitationBtnX = invitationBtn.getWidth()*3;
        invitationBtnY = invitationBtn.getHeight()*3;

        signInBtnX = signInBtn.getWidth()*3;
        signInBtnY = signInBtn.getHeight()*3;

        graphicsX = Gdx.graphics.getWidth();
        graphicsY = Gdx.graphics.getHeight();
        System.out.println(playBtnY);
        System.out.println(instructionBtnY);
        System.out.println(quickGameBtnY);
        System.out.println(sendInviteBtnY);



    }

    @Override
    public void handleInput() {
        touchPos.x = Gdx.input.getX();
        touchPos.y = Gdx.input.getY();
        //System.out.println(touchPos.y);
        if(goToPlay == true){
            gsm.set(new PlayStateHost(gsm));
            dispose();
        }

        if(gotoPlayP == true){
            gsm.set(new PlayStateNonHost(gsm));
            dispose();
            ready = true;
        }
        /****************************************************************************************
         * This is a set of conditions to handle the highlighting of the play button when pressed
         * **************************************************************************************
         */

        if(Gdx.input.isTouched() && touched == false){
            if(touchPos.x<=(graphicsX/2)+(playBtnX/2) && touchPos.x>=(graphicsX/2)-(playBtnX/2)){
                if(touchPos.y<=(graphicsY/2)+(playBtnY/2) && touchPos.y>=(graphicsY/2)-(playBtnY/2)){
                    playBtn.dispose();
                    playBtn = new Texture("playBtn_pressed.png");
                    touched = true;
                }
            }
        }
        else if(!Gdx.input.isTouched() && touched == true){
            if(touchPos.x<=(graphicsX/2)+(playBtnX/2) && touchPos.x>=(graphicsX/2)-(playBtnX/2)){
                if(touchPos.y<=(graphicsY/2) + (playBtnY / 2) && touchPos.y>=(graphicsY/2)-(playBtnY/2)){
                    gsm.set(new PlayStateHost(gsm));
                    dispose();
                    touched = false;
                }
            }
            else{
                playBtn.dispose();
                playBtn = new Texture("playBtn.png");
                touched = false;
            }
        }

        /***********************************************************************************************
         * This is a set of conditions to handle the highlighting of the instruction button when pressed
         * *********************************************************************************************
         */
        if(Gdx.input.isTouched() && touched == false){
            if(touchPos.x<=(graphicsX/2)+(instructionBtnX/2) && touchPos.x>=(graphicsX/2)-(instructionBtnX/2)){
                if(touchPos.y<=(graphicsY/2+playBtnY/2+instructionBtnY + bufferFromTop) && touchPos.y>=(graphicsY/2+playBtnY/2+ bufferFromTop)){
                    instructionBtn.dispose();
                    instructionBtn = new Texture("instructionBtn_pressed.png");
                    touched = true;

                }
            }
        }
        else if(!Gdx.input.isTouched() && touched == true){
            if(touchPos.x<=(graphicsX/2)+(instructionBtnX/ 2) && touchPos.x >= (graphicsX/2)-(instructionBtnX/2)){
                if(touchPos.y<=(graphicsY/2+playBtnY/2+instructionBtnY + bufferFromTop) && touchPos.y>=(graphicsY/2+playBtnY/2+ bufferFromTop)){
                    gsm.set(new InstructionState(gsm));
                    dispose();
                    touched = false;
                }
            }
            else{
                instructionBtn.dispose();
                instructionBtn = new Texture("instructionBtn.png");
                touched = false;
            }
        }

        /***********************************************************************************************
         * This is a set of conditions to handle the highlighting of the quick game button when pressed
         * *********************************************************************************************
         */
        if(Gdx.input.isTouched() && touched == false){
            if(touchPos.x<=(graphicsX/2)+(quickGameBtnX/2) && touchPos.x>=(graphicsX/2)-(quickGameBtnX/2)){
                if(touchPos.y<=(graphicsY/2+playBtnY/2+instructionBtnY+ 2*bufferFromTop + quickGameBtnY) && touchPos.y>=(graphicsY/2+playBtnY/2+instructionBtnY+ bufferFromTop)){
                    quickGameBtn.dispose();
                    quickGameBtn = new Texture("quickGameBtn_pressed.png");
                    touched = true;

                }
            }
        }
        else if(!Gdx.input.isTouched() && touched == true){
            if(touchPos.x<=(graphicsX/2)+(quickGameBtnX/2) && touchPos.x>=(graphicsX/2)-(quickGameBtnX/2)){
                if(touchPos.y<=(graphicsY/2+playBtnY/2+instructionBtnY+ 2*bufferFromTop + quickGameBtnY) && touchPos.y>=(graphicsY/2+playBtnY/2+instructionBtnY+ bufferFromTop)){
                    //TODO: put your method call here
                    if(MacroHardv2.actionResolver.isSignedIn()){
                        MacroHardv2.actionResolver.QuickGame();

                    }
                    quickGameBtn.dispose();
                    System.out.println("qg");
                    quickGameBtn = new Texture("quickGameBtn.png");
                    touched = false;
                }
            }
            else{
                quickGameBtn.dispose();
                quickGameBtn = new Texture("quickGameBtn.png");
                touched = false;
            }
        }

        /**********************************************************************************************
         * This is a set of conditions to handle the highlighting of the send invite button when pressed
         * *********************************************************************************************
         */
        if(Gdx.input.isTouched() && touched == false){
            if(touchPos.x<=(graphicsX/2)+(sendInviteBtnX/2) && touchPos.x>=(graphicsX/2)-(sendInviteBtnX/2)){
                if(touchPos.y<=(graphicsY/2-playBtnY/2-bufferFromTop) && touchPos.y>=(graphicsY/2-playBtnY/2-bufferFromTop-sendInviteBtnY)){
                    sendInviteBtn.dispose();
                    sendInviteBtn = new Texture("sendInviteBtn_pressed.png");
                    touched = true;

                }
            }
        }
        else if(!Gdx.input.isTouched() && touched == true){
            if(touchPos.x<=(graphicsX/2)+(quickGameBtnX/2) && touchPos.x>=(graphicsX/2)-(quickGameBtnX/2)){
                if(touchPos.y<=(graphicsY/2-playBtnY/2-bufferFromTop) && touchPos.y>=(graphicsY/2-playBtnY/2-bufferFromTop-sendInviteBtnY)){
                    //TODO: RYAN PUT YOUR SEND INVITE HERE
                    MacroHardv2.actionResolver.Inviteplayers();
                    sendInviteBtn.dispose();
                    sendInviteBtn = new Texture("sendInviteBtn.png");
                    touched = false;
                }
            }
            else{
                sendInviteBtn.dispose();
                sendInviteBtn = new Texture("sendInviteBtn.png");
                touched = false;
            }
        }
        /**********************************************************************************************
         * This is a set of conditions to handle the highlighting of the invitation button when pressed
         * *********************************************************************************************
         */
        if(Gdx.input.isTouched() && touched == false){
            if(touchPos.x<=(graphicsX/2)+(sendInviteBtnX/2) && touchPos.x>=(graphicsX/2)-(sendInviteBtnX/2)){
                if(touchPos.y<=(graphicsY/2-playBtnY/2-sendInviteBtnY - 2*bufferFromTop) && touchPos.y>=(graphicsY/2-playBtnY/2-2*bufferFromTop-sendInviteBtnY - invitationBtnY)){
                    invitationBtn.dispose();
                    invitationBtn = new Texture("invitationBtn_pressed.png");
                    touched = true;

                }
            }
        }
        else if(!Gdx.input.isTouched() && touched == true){
            if(touchPos.x<=(graphicsX/2)+(quickGameBtnX/2) && touchPos.x>=(graphicsX/2)-(quickGameBtnX/2)){
                if(touchPos.y<=(graphicsY/2-playBtnY/2-sendInviteBtnY - 2*bufferFromTop) && touchPos.y>=(graphicsY/2-playBtnY/2-2*bufferFromTop-sendInviteBtnY - invitationBtnY)){
                    //TODO: RYAN PUT YOUR INVITATION BOX HERE
                    MacroHardv2.actionResolver.Seeinvites();
                    invitationBtn.dispose();
                    invitationBtn = new Texture("invitationBtn.png");
                    touched = false;
                }
            }
            else{
                invitationBtn.dispose();
                invitationBtn = new Texture("invitationBtn.png");
                touched = false;
            }
        }

        /**********************************************************************************************
         * This is a set of conditions to handle the highlighting of the sign in button when pressed
         * *********************************************************************************************
         */
        if(Gdx.input.isTouched() && touched == false){
            if(touchPos.x<=(graphicsX/2)+(sendInviteBtnX/2) && touchPos.x>=(graphicsX/2)-(sendInviteBtnX/2)){
                if(touchPos.y<=(graphicsY/2-playBtnY/2-sendInviteBtnY - invitationBtnY - 3*bufferFromTop) && touchPos.y>=(graphicsY/2-playBtnY/2-3*bufferFromTop-sendInviteBtnY - invitationBtnY - signInBtnY)){
                    signInBtn.dispose();
                    signInBtn = new Texture("signInBtn_pressed.png");
                    touched = true;

                }
            }
        }
        else if(!Gdx.input.isTouched() && touched == true){
            if(touchPos.x<=(graphicsX/2)+(quickGameBtnX/2) && touchPos.x>=(graphicsX/2)-(quickGameBtnX/2)){
                if(touchPos.y<=(graphicsY/2-playBtnY/2-sendInviteBtnY - invitationBtnY - 3*bufferFromTop) && touchPos.y>=(graphicsY/2-playBtnY/2-3*bufferFromTop-sendInviteBtnY - invitationBtnY - signInBtnY)){
                    //TODO: RYAN PUT YOUR SIGN IN HERE
                    MacroHardv2.actionResolver.SignIn();
                    signInBtn.dispose();
                    signInBtn = new Texture("signInBtn.png");
                    touched = false;
                }
            }
            else{
                signInBtn.dispose();
                signInBtn = new Texture("signInBtn.png");
                touched = false;
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, graphicsX, graphicsY);
        sb.draw(playBtn,(graphicsX/2)-(playBtnX/2),graphicsY/2-playBtnY/2,playBtnX,playBtnY);
        sb.draw(instructionBtn, (graphicsX/ 2) - (instructionBtnX/2), graphicsY/2 - playBtnY/2 - instructionBtnY - bufferFromTop, instructionBtnX, instructionBtnY);
        sb.draw(quickGameBtn,(graphicsX/2)-(quickGameBtnX/2),(graphicsY/2 - playBtnY/2 - instructionBtnY- quickGameBtnY- bufferFromTop*2),quickGameBtnX,quickGameBtnY);
        sb.draw(sendInviteBtn,(graphicsX/2)-(sendInviteBtnX/2),(graphicsY/2 + playBtnY/2 + bufferFromTop),sendInviteBtnX,sendInviteBtnY);
        sb.draw(invitationBtn,(graphicsX/2)-(invitationBtnX/2),(graphicsY/2 + playBtnY/2 + sendInviteBtnY+ 2*bufferFromTop),invitationBtnX,invitationBtnY);
        sb.draw(signInBtn,(graphicsX/2)-(signInBtnX/2),(graphicsY/2 + playBtnY/2 + sendInviteBtnY + invitationBtnY + 3*bufferFromTop),signInBtnX,signInBtnY);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        playBtn.dispose();
        instructionBtn.dispose();
        quickGameBtn.dispose();

    }

}