package com.mygdx.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.game.MacroHardv2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.customEnum.StateType;
import com.mygdx.game.objects.CustomButton;

/**
 * Created by Syuqri on 3/7/2016.
 */
public class MenuState extends State{
    private Texture background,playBtnImage,instructionBtnImage,quickGameBtnImage, sendInviteBtnImage, invitationBtnImage, signInBtnImage, leaderboardBtnImage;

    private CustomButton playBtn, instructionBtn, invitationBtn, sendInviteBtn, quickGameBtn, signInBtn, leaderboardBtn;
    private Vector3 touchPos = new Vector3(0,0,0);
    private float bufferFromTop = 20;
    private boolean touched = false;
    public static volatile boolean startHost = false;
    public static volatile boolean startNonHost = false;


    //Resize variables
    private float playBtnX, playBtnY,
            instructionBtnX, instructionBtnY,
            quickGameBtnX, quickGameBtnY,
            graphicsX, graphicsY,
            sendInviteBtnX, sendInviteBtnY,
            invitationBtnX, invitationBtnY,
            signInBtnX, signInBtnY,
            leaderboardBtnX, leaderboardBtnY,
            resizeFactor
            ;
    public MenuState(GameStateManager gsm) {
        super(gsm);
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        graphicsX = Gdx.graphics.getWidth();
        graphicsY = Gdx.graphics.getHeight();
        generateTextures();

        gsm.startMusicLoop("MainMenuSound.mp3", (float) 0.15);

        resizeFactor = resizeFactor(playBtnImage.getWidth());

        playBtnX = playBtnImage.getWidth()*resizeFactor;
        playBtnY = playBtnImage.getHeight()*resizeFactor;
        quickGameBtnX = quickGameBtnImage.getWidth()*resizeFactor;
        quickGameBtnY = quickGameBtnImage.getHeight()*resizeFactor;
        instructionBtnX = instructionBtnImage.getWidth()*resizeFactor;
        instructionBtnY = instructionBtnImage.getHeight()*resizeFactor;
        sendInviteBtnX = sendInviteBtnImage.getWidth()*resizeFactor;
        sendInviteBtnY = sendInviteBtnImage.getHeight()*resizeFactor;
        invitationBtnX = invitationBtnImage.getWidth()*resizeFactor;
        invitationBtnY = invitationBtnImage.getHeight()*resizeFactor;
        signInBtnX = signInBtnImage.getWidth()*resizeFactor;
        signInBtnY = signInBtnImage.getHeight()*resizeFactor;
        leaderboardBtnX = leaderboardBtnImage.getWidth()*resizeFactor;
        leaderboardBtnY = leaderboardBtnImage.getHeight()*resizeFactor;

        playBtn = new CustomButton((graphicsX/2),(graphicsY/2+instructionBtnY/2 + quickGameBtnY + 2*bufferFromTop),playBtnX,playBtnY);
        playBtn.setImage(playBtnImage);
        quickGameBtn = new CustomButton( (graphicsX/2),(graphicsY/2 + instructionBtnY/2 +bufferFromTop),quickGameBtnX,quickGameBtnY);
        quickGameBtn.setImage(quickGameBtnImage);
        instructionBtn = new CustomButton((graphicsX / 2), graphicsY / 2 - instructionBtnY/2, instructionBtnX, instructionBtnY);
        instructionBtn.setImage(instructionBtnImage);
        sendInviteBtn = new CustomButton((graphicsX/2),(graphicsY/2 - quickGameBtnY/2 - sendInviteBtnY- bufferFromTop),sendInviteBtnX,sendInviteBtnY);
        sendInviteBtn.setImage(sendInviteBtnImage);
        invitationBtn = new CustomButton((graphicsX/2),(graphicsY/2 - quickGameBtnY/2 - sendInviteBtnY-invitationBtnY- 2*bufferFromTop),invitationBtnX,invitationBtnY);
        invitationBtn.setImage(instructionBtnImage);
        signInBtn = new CustomButton((graphicsX/2),(graphicsY/2 - quickGameBtnY/2 - sendInviteBtnY - invitationBtnY - signInBtnY- 3*bufferFromTop),signInBtnX,signInBtnY);
        signInBtn.setImage(signInBtnImage);
        leaderboardBtn = new CustomButton((graphicsX/2),(graphicsY/2 - quickGameBtnY/2 - sendInviteBtnY - invitationBtnY - signInBtnY-leaderboardBtnY- 4*bufferFromTop),leaderboardBtnX,leaderboardBtnY);
        leaderboardBtn.setImage(leaderboardBtnImage);

    }
    @Override
    public void handleInput() {
        touchPos.x = Gdx.input.getX();
        touchPos.y = Gdx.input.getY();
        cam.unproject(touchPos);

        if (startHost) {
            gsm.startMusic("MenuSelectionClick.wav", (float) 1);
            startHost = false;
            dispose();
            gsm.set(new PlayStateHost(gsm, MacroHardv2.actionResolver.getmyidint()), StateType.PLAY);
        }

        if(startNonHost){
            gsm.startMusic("MenuSelectionClick.wav", (float) 1);
            startNonHost = false;
            dispose();
            gsm.set(new PlayStateNonHost(gsm, MacroHardv2.actionResolver.getmyidint()), StateType.PLAY);
        }

        /****************************************************************************************
         * This is a set of conditions to handle the highlighting of the buttons when pressed
         * **************************************************************************************
         */
        if(Gdx.input.isTouched() && !touched){
            if(playBtn.contains(touchPos.x,touchPos.y)){
                playBtnImage.dispose();
                playBtnImage = new Texture("playBtn_pressed.png");
                touched = true;
            }
            else if(instructionBtn.contains(touchPos.x,touchPos.y)){
                instructionBtnImage.dispose();
                instructionBtnImage = new Texture("instructionBtn_pressed.png");
                touched = true;
            }
            else if(quickGameBtn.contains(touchPos.x,touchPos.y)){
                quickGameBtnImage.dispose();
                quickGameBtnImage = new Texture("quickGameBtn_pressed.png");
                touched = true;
            }
            else if(sendInviteBtn.contains(touchPos.x,touchPos.y)){
                sendInviteBtnImage.dispose();
                sendInviteBtnImage = new Texture("sendInviteBtn_pressed.png");
                touched = true;
            }
            else if(invitationBtn.contains(touchPos.x,touchPos.y)){
                invitationBtnImage.dispose();
                invitationBtnImage = new Texture("invitationBtn_pressed.png");
                touched = true;
            }
            else if(signInBtn.contains(touchPos.x,touchPos.y)){
                signInBtnImage.dispose();
                signInBtnImage = new Texture("signInBtn_pressed.png");
                touched = true;
            }
            else if(leaderboardBtn.contains(touchPos.x,touchPos.y)){
                leaderboardBtnImage.dispose();
                leaderboardBtnImage = new Texture("leaderboardBtn_pressed.png");
                touched = true;
            }
        }

        else if(!Gdx.input.isTouched() && touched){
            if(playBtn.contains(touchPos.x,touchPos.y)){
                gsm.startMusic("MenuSelectionClick.wav", (float) 1);
                gsm.set(new PlayStateHost(gsm, 0, true), StateType.PLAY);
                //dispose();
                touched = false;
            }
            else if(instructionBtn.contains(touchPos.x,touchPos.y)){
                gsm.startMusic("MenuSelectionClick.wav", (float) 1);
                gsm.set(new InstructionState(gsm), StateType.NON_PLAY);
                //dispose();
                touched = false;
            }
            else if(quickGameBtn.contains(touchPos.x,touchPos.y)){
                gsm.startMusic("MenuSelectionClick.wav",(float)1);
                if(MacroHardv2.actionResolver.isSignedIn()){
                    MacroHardv2.actionResolver.QuickGame();

                }
                quickGameBtnImage.dispose();
                quickGameBtnImage = new Texture("quickGameBtn.png");
                touched = false;
            }
            else if(sendInviteBtn.contains(touchPos.x,touchPos.y)){
                gsm.startMusic("MenuSelectionClick.wav",(float)1);
                MacroHardv2.actionResolver.Inviteplayers();
                sendInviteBtnImage.dispose();
                sendInviteBtnImage = new Texture("sendInviteBtn.png");
                touched = false;
            }
            else if(invitationBtn.contains(touchPos.x,touchPos.y)){
                gsm.startMusic("MenuSelectionClick.wav",(float)1);
                MacroHardv2.actionResolver.Seeinvites();
                invitationBtnImage.dispose();
                invitationBtnImage = new Texture("invitationBtn.png");
                touched = false;
            }
            else if(signInBtn.contains(touchPos.x,touchPos.y)){
                gsm.startMusic("MenuSelectionClick.wav", (float) 1);
                MacroHardv2.actionResolver.SignIn();
                signInBtnImage.dispose();
                signInBtnImage = new Texture("signInBtn.png");
                touched = false;
            }
            else if(leaderboardBtn.contains(touchPos.x,touchPos.y)){
                gsm.startMusic("MenuSelectionClick.wav", (float) 1);
                MacroHardv2.actionResolver.getLeaderboardGPGS();
                leaderboardBtnImage.dispose();
                leaderboardBtnImage = new Texture("leaderboardBtn.png");
                touched = false;
            }
            else{
                dispose();
                generateTextures();
                touched = false;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        handleInput();
        sb.begin();
        cam.update();
        sb.setProjectionMatrix(cam.combined);
        sb.draw(background, 0, 0, graphicsX, graphicsY);
        sb.draw(playBtnImage, (graphicsX / 2) , (graphicsY / 2 + instructionBtnY / 2 + quickGameBtnY + 2 * bufferFromTop), playBtnX, playBtnY);
        sb.draw(quickGameBtnImage, (graphicsX / 2), (graphicsY / 2 + instructionBtnY / 2 + bufferFromTop), quickGameBtnX, quickGameBtnY);
        sb.draw(instructionBtnImage, (graphicsX / 2), graphicsY / 2 - instructionBtnY / 2, instructionBtnX, instructionBtnY);
        sb.draw(sendInviteBtnImage, (graphicsX / 2), (graphicsY / 2 - quickGameBtnY / 2 - sendInviteBtnY - bufferFromTop), sendInviteBtnX, sendInviteBtnY);
        sb.draw(invitationBtnImage, (graphicsX / 2) , (graphicsY / 2 - quickGameBtnY / 2 - sendInviteBtnY - invitationBtnY - 2 * bufferFromTop), invitationBtnX, invitationBtnY);
        sb.draw(signInBtnImage, (graphicsX / 2) , (graphicsY / 2 - quickGameBtnY / 2 - sendInviteBtnY - invitationBtnY - signInBtnY - 3 * bufferFromTop), signInBtnX, signInBtnY);
        sb.draw(leaderboardBtnImage,(graphicsX/2),(graphicsY/2 - quickGameBtnY/2 - sendInviteBtnY - invitationBtnY - signInBtnY-leaderboardBtnY- 4*bufferFromTop),leaderboardBtnX,leaderboardBtnY);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        playBtnImage.dispose();
        instructionBtnImage.dispose();
        quickGameBtnImage.dispose();
        sendInviteBtnImage.dispose();
        invitationBtnImage.dispose();
        signInBtnImage.dispose();
        leaderboardBtnImage.dispose();
    }

    @Override
    public void update(byte[] message) {

    }

    public void generateTextures(){
        background = new Texture("Main_menu.png");
        playBtnImage = new Texture("playBtn.png");
        instructionBtnImage = new Texture("instructionBtn.png");
        quickGameBtnImage = new Texture("quickGameBtn.png");
        sendInviteBtnImage = new Texture("sendInviteBtn.png");
        invitationBtnImage = new Texture("invitationBtn.png");
        signInBtnImage = new Texture("signInBtn.png");
        leaderboardBtnImage = new Texture("leaderboardBtn.png");
    }

    private float resizeFactor(float x){
        return (float)0.33*Gdx.graphics.getWidth()/x;
    }
}