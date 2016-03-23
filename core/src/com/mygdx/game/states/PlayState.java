package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.DangerZone;
import com.mygdx.game.objects.Overlay;
import com.mygdx.game.objects.Barrier;
import com.mygdx.game.objects.BarrierOpen;
import com.mygdx.game.objects.Obstacle;
import com.mygdx.game.objects.Power;
import com.mygdx.game.objects.SideWall;
import com.mygdx.game.objects.Switch;
import com.mygdx.game.objects.JoyStick;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.UI;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Syuqri on 3/7/2016.
 */
public abstract class PlayState extends State{

    //objects
    private JoyStick joystick;
    protected Player player;
    private Vector3 touchPos = new Vector3();
    protected final int GAME_WIDTH = 9;

    //values
    public boolean running;
    private boolean touched;
    private boolean touchHeld;
    private long endPassivePowerTime, endActivePowerTime;
    protected float gameSpeed, speedChange, speedIncrease, dangerZoneSpeedLimit;
    protected int playerSpeed, dangerZone, powerCounter, doorCounter, score, scoreIncrement;
    public long start = System.currentTimeMillis();
    boolean passivePowerState, passivePowerEffectTaken, activePowerState, activePowerEffectTaken;

    //boolean arrays
    public boolean[] path = createArray(true);
    boolean[] current = createArray(true);
    boolean[] powerUp = createArray(false);
    float[] doorSwitch = null;
    boolean[] barrier = createArray(false);

    //Arraylists
    protected ArrayList<boolean[]> mapBuffer = new ArrayList<boolean[]>();
    protected ArrayList<boolean[]> doorBuffer = new ArrayList<boolean[]>();
    protected ArrayList<float[]> switchBuffer = new ArrayList<float[]>();
    protected ArrayList<boolean[]> powerUpBuffer = new ArrayList<boolean[]>();

    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    public static ArrayList<SideWall> sideWalls = new ArrayList<SideWall>();
    private ArrayList<Switch> switches = new ArrayList<Switch>();
    private ArrayList<Barrier> barriers = new ArrayList<Barrier>();
    private ArrayList<BarrierOpen> barrierOpens = new ArrayList<BarrierOpen>();
    private ArrayList<Power> powers = new ArrayList<Power>();
    private ArrayList<Background> bg = new ArrayList<Background>();
    private ArrayList<Overlay> effects = new ArrayList<Overlay>();
    private ArrayList<DangerZone> dz = new ArrayList<DangerZone>();
    private ArrayList<UI> ui = new ArrayList<UI>();

    //final values
    final int spriteWidth = 50;
    final int spriteHeight = 50;
    private final String[] TYPES_OF_POWER = {"slowGameDown","speedPlayerUp","doubleScore","morePowers","dangerZoneLower",
            "speedGameUp","slowPlayerDown","haltScore","lessPowers","dangerZoneHigher"};

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        running = true;

        //camera initialization
        cam = new OrthographicCamera();
        cam.setToOrtho(false, 480, 800);

        //object initialization
        player = new Player();
        joystick = new JoyStick();

        //misc values initialization
        gameSpeed = 80;
        speedIncrease = (float) 0.07;
        speedChange = (float) 0.6;
        playerSpeed = 300;
        dangerZone = 200;
        powerCounter = 0;
        doorCounter = 0;
        dangerZoneSpeedLimit = 250;
        score = 0;
        scoreIncrement = 1;
        passivePowerState = passivePowerEffectTaken = activePowerState = activePowerEffectTaken = false;
        endPassivePowerTime = endActivePowerTime = System.currentTimeMillis();

        createBg();
        createObstacle();
        createSides();
    }
    @Override
    protected void handleInput() {
        if(Gdx.input.isTouched()){
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            touched = true;
        } else {
            touched = false;
        }

        float relativex = 0;
        float relativey = 0;
        if (touched) {
            cam.unproject(touchPos);
            if (!touchHeld) {
                //if this is the initial touch initialize the joystick at the touched location
                joystick.setX(touchPos.x - joystick.getJoystickWidth()/2);
                joystick.setY(touchPos.y - joystick.getJoystickHeight()/2);
                joystick.setCX(touchPos.x - joystick.getJoystickCenterWidth()/2);
                joystick.setCY(touchPos.y - joystick.getJoystickCenterHeight()/2);
                touchHeld = true;
            }
            relativex = touchPos.x - (joystick.getX() + joystick.getJoystickWidth()/2);
            relativey = touchPos.y - (joystick.getY() + joystick.getJoystickHeight()/2);
        } else {
            touchHeld = false;
        }

        if (touchHeld) {
            //calculates the relevant numbers needed for omnidirectional movement
            float angle = (float) Math.atan2(relativey, relativex);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            if (Math.abs(relativex) < joystick.getJoystickWidth()/2
                    && (Math.abs(relativey) < joystick.getJoystickHeight()/2)) {
                //if the touched position is within the circle set the dot to the touched position
                joystick.setCX(touchPos.x - joystick.getJoystickCenterWidth()/2);
                joystick.setCY(touchPos.y - joystick.getJoystickCenterHeight()/2);
            } else {
                //otherwise set it to the edge of the circle
                joystick.setCX(cos * joystick.getJoystickWidth()/2 + joystick.getX() + joystick.getJoystickWidth()/2 - joystick.getJoystickCenterWidth()/2);
                joystick.setCY(sin * joystick.getJoystickWidth()/2 + joystick.getY() + joystick.getJoystickHeight()/2 - joystick.getJoystickCenterHeight()/2);
            }
            //if the touch is within a specific distance from the centre of the circle then move the player
            if (Math.pow(relativex, 2) + Math.pow(relativey, 2) > 400) {
                omniMove(cos, sin);
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        long score = System.currentTimeMillis()-start;
        handleInput();
        checkSwitchCollision();
        // tell the camera to update its matrices.
        while (sideWalls.get(sideWalls.size() - 1).y < 1000) {
            synchronized (this) {
                while (mapBuffer.size() == 0){
                    try {
                        wait();
                    } catch (InterruptedException ignored){}
                }
                path = mapBuffer.remove(0);
                barrier = doorBuffer.remove(0);
                doorSwitch = switchBuffer.remove(0);
                powerUp = powerUpBuffer.remove(0);
                notifyAll();
            }
            float temp = sideWalls.get(sideWalls.size() - 1).y + 50;
            spawnObstacle(temp);
            spawnPower();
            spawnSwitch();
            spawnDoor();
            spawnSides(temp);
        }
        if (bg.get(bg.size() - 1).y < 1) {
            spawnBg();
        }
        // tell the camera to update its matrices.
        cam.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        sb.setProjectionMatrix(cam.combined);

        // begin a new batch and draw the player and all objects
        sb.begin();

        for (Background backg : bg) {
            sb.draw(backg.getImage(), backg.x, backg.y);
        }
        for (Obstacle obstacle : obstacles) {
            sb.draw(obstacle.getImage(), obstacle.x, obstacle.y);
        }
        for (SideWall sideWall : sideWalls) {
            sb.draw(sideWall.getImage(), sideWall.x, sideWall.y);
        }
        for (Power power : powers) {
            sb.draw(power.getImage(), power.x, power.y);
        }
        for (Switch eachSwitch : switches) {
            sb.draw(eachSwitch.getImage(), eachSwitch.x, eachSwitch.y);
        }
        for (Barrier barrier : barriers) {
            sb.draw(barrier.getImage(), barrier.x, barrier.y);
        }
        for (BarrierOpen barrier : barrierOpens) {
            sb.draw(barrier.getImage(), barrier.x, barrier.y);
        }

        sb.draw(player.getTexture(), player.x, player.y);

        for (Overlay effect : effects) {
            sb.draw(effect.getImage(), effect.x, effect.y);
        }
        for (DangerZone danger : dz) {
            sb.draw(danger.getImage(), danger.x, danger.y);
        }
        for (UI inter : ui) {
            sb.draw(inter.getImage(), inter.x, inter.y);
        }

        if(touched){

            sb.draw(joystick.getJoystickImage(), joystick.getX(), joystick.getY());
            sb.draw(joystick.getJoystickCentreImage(), joystick.getCX(), joystick.getCY());
        }

        sb.end();

//		constantly check if any power/DangerZone's effect still lingers
        effectPassivePower();
        effectDangerZone(player);

        // move the obstacles, remove any that are beneath the bottom edge of the screen.

        player.y -= gameSpeed * Gdx.graphics.getDeltaTime();

        Iterator<Obstacle> iter = obstacles.iterator();
        Iterator<SideWall> iter2 = sideWalls.iterator();
        Iterator<Power> iter3 = powers.iterator();
        Iterator<Switch> iter4 = switches.iterator();
        Iterator<Barrier> iter5 = barriers.iterator();
        Iterator<BarrierOpen> iter6 = barrierOpens.iterator();
        Iterator<Background> iter7 = bg.iterator();
        Iterator<Overlay> iter8 = effects.iterator();
        while (iter.hasNext()) {
            Rectangle obstacle = iter.next();
            obstacle.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (obstacle.y + spriteHeight < 0) iter.remove();
        }
        while (iter2.hasNext()) {
            Rectangle side = iter2.next();
            side.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (side.y + spriteHeight < 0) iter2.remove();
        }
        while (iter3.hasNext()) {
            Rectangle power = iter3.next();
            power.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (power.y + spriteHeight < 0) iter3.remove();
        }
        while (iter4.hasNext()) {
            Rectangle swt = iter4.next();
            swt.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (swt.y + spriteHeight < 0) iter4.remove();
        }
        while (iter5.hasNext()) {
            Rectangle door = iter5.next();
            door.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (door.y + spriteHeight < 0) iter5.remove();
        }
        while (iter6.hasNext()) {
            Rectangle door = iter6.next();
            door.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (door.y + spriteHeight < 0) iter6.remove();
        }
        while (iter7.hasNext()) {
            Rectangle bg = iter7.next();
            bg.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (bg.y + 800 < 0) iter7.remove();
        }
        while (iter8.hasNext()) {
            Rectangle effect = iter8.next();
            effect.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (effect.y + 800 < 0) iter8.remove();
        }
    }
    @Override
    public void update(float dt) {
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        for (Obstacle obstacle:obstacles) {
            obstacle.getImage().dispose();
        }
        for (Power power:powers) {
            power.getImage().dispose();
        }
        for (Barrier barrier:barriers) {
            barrier.getImage().dispose();
        }
        for (Switch eachSwitch:switches) {
            eachSwitch.getImage().dispose();
        }
        for (SideWall sideWall:sideWalls) {
            sideWall.getImage().dispose();
        }
        for (BarrierOpen barrier:barrierOpens) {
            barrier.getImage().dispose();
        }
        player.getImage().dispose();
        joystick.getJoystickImage().dispose();
        joystick.getJoystickCentreImage().dispose();
    }






/***************************************************
*  CREATE METHODS HERE
****************************************************
*/

    private void createObstacle() {
        for (int i = 0; i < path.length; i++) {
            if (!path[i]) {
                Obstacle obstacle = new Obstacle();
                obstacle.x = (spriteWidth * i) + 15;
                obstacle.y = 800;
                obstacle.width = spriteWidth;
                obstacle.height = spriteHeight;
                obstacles.add(obstacle);
            }
        }
        powerCounter += 1;
        doorCounter += 1;
    }
    private void createBg(){
        Background backg = new Background(0);
        Overlay effect = new Overlay(0);
        DangerZone danger = new DangerZone(0);
        UI inter = new UI(0);
        bg.add(backg);
        effects.add(effect);
        dz.add(danger);
        ui.add(inter);
    }
    private void createSides(){
        int counter = 0;
        while (counter*spriteHeight <= 800) {
            for (int i = 0; i < 2; i++) {
                SideWall sideWall = new SideWall(spriteHeight, counter*spriteHeight, i);
                sideWalls.add(sideWall);
            }
            counter++;
        }
    }

/***********************************************
 *  SPAWN METHODS HERE
 ***********************************************
 */
    /**
     Method to spawn the walls using the coordinates from the wallCoord() method
     */
    private void spawnObstacle(float in) {
        for (int i = 0; i < path.length; i++) {
            if (!path[i]) {
                Obstacle obstacle = new Obstacle();
                obstacle.x = (spriteWidth * i) + 15;
                obstacle.y = in;
                obstacle.width = spriteWidth;
                obstacle.height = spriteHeight;
                obstacles.add(obstacle);
            }
        }
        score += scoreIncrement;
        powerCounter += 1;
        doorCounter += 1;
    }
    private void spawnBg(){
        Background backg = new Background(800);
        Overlay effect = new Overlay(800);
        bg.add(backg);
        effects.add(effect);
    }
    private void spawnPower() {
        for (int i = 0; i < powerUp.length; i++) {
            if (powerUp[i]) {
                Power power = new Power(TYPES_OF_POWER[(int)(Math.random()*TYPES_OF_POWER.length)],i);
                powers.add(power);
            }
        }
    }
    private void spawnSwitch(){
        Switch doorSwitch = new Switch(spriteWidth, spriteHeight, this.doorSwitch[0], this.doorSwitch[1]);
        switches.add(doorSwitch);
    }
    private void spawnDoor(){
        for (int i = 0; i < barrier.length; i++) {
            if (barrier[i]) {
                Barrier door = new Barrier();
                door.x = (spriteWidth * i) + 15;
                door.y = sideWalls.get(sideWalls.size()-1).y+50;
                door.width = spriteWidth;
                door.height = spriteHeight;
                barriers.add(door);
            }
        }
    }
    /**
     Spawn side walls to fill up the gap between the playing field and the actual maze
     */
    private void spawnSides(float in){
        for (int i = 0; i < 2; i++) {
            SideWall sideWall = new SideWall(spriteHeight,in,i);
            sideWalls.add(sideWall);
        }
    }
    /**
     Method to move the player
     */
    private void omniMove(float x, float y){
        float prevx = player.x;
        float prevy = player.y;
        player.x += x * playerSpeed * Gdx.graphics.getDeltaTime();
        player.y += y * playerSpeed * Gdx.graphics.getDeltaTime();
        if (collidesObstacle()){
            player.x = prevx;
            player.y = prevy;
            if (x > 0) x = (float)2/3;
            if (x < 0) x = -(float)2/3;
            if (y > 0) y = (float)2/3;
            if (y < 0) y = -(float)2/3;
            player.x += x * playerSpeed * Gdx.graphics.getDeltaTime();
            if (collidesObstacle()){
                player.x = prevx;
            }
            player.y += y * playerSpeed * Gdx.graphics.getDeltaTime();
            if (collidesObstacle()){
                player.y = prevy;
            }
        }
    }

/***********************************************
* MISC METHODS HERE
************************************************
*/

    /**
     Method handling collision. If there is an overlap over an object that should be impassable,
     the player will be moved back to his previous position (remembered by a temporary variable)
     */
    private boolean collidesObstacle(){
// collision with screen boundaries
        if (player.x > 465 - player.width ){
            player.x = 465 - player.height;
        }

        if (player.x < 15){
            player.x = 15;
        }

        if (player.y > 750){
            player.y = 750;
        }

//		collide with normal wall obstacle
        for (Obstacle obstacle : obstacles) {
            if (player.overlaps(obstacle)) {
                return true;
            }
        }
//		collide with barriers
        for (Barrier barrier : barriers) {
            if (player.overlaps(barrier)) {
                return true;
            }
        }
        return false;
    }

    /**
     Method to calculate and randomize the wall, power, switch etc. placement. It keeps randomly
     generating a sequence until it fulfils the condition where the player can reach to the next
     layer of walls. I.e, no dead ends. Also ensures that the power is spawned on a 'box' not
     occupied by a wall and that switches are reachable.
     */

    private void checkSwitchCollision(){
        //		collide with switch
        for (Switch eachSwitch:switches){
            if (player.overlaps(eachSwitch)){
                // change this to another different switch image
                eachSwitch.setImage(new Texture(Gdx.files.internal("switch_on.png")));
                for (Barrier barrier: barriers){
                    BarrierOpen bg = new BarrierOpen();
                    bg.x = barrier.x;
                    bg.y = barrier.y;
                    bg.width = 50;
                    bg.height = 50;
                    barrierOpens.add(bg);
                }
                removeBarriers();
                // then notify server
            }
        }
//		collide with power up
        for (Power power:powers){
            if (player.overlaps(power)){
                if (isPassive(power)) {
                    player.setPassivePower(power.getType());
                    passivePowerState = true;
                    endPassivePowerTime = System.currentTimeMillis()+5000;
                }
                else player.setActivePower(power.getType());
                powers.remove(power);
            }
        }
    }


    private void removeBarriers(){
//		TODO: if notified by server (Ryan)
        barriers.clear();
    }

    /**
     Methods handling power-ups/affecting game attributes
     */

    private void effectDangerZone(Player p) {
        if (p.getY()<=dangerZone && gameSpeed<=dangerZoneSpeedLimit) {
            gameSpeed += speedIncrease;
        }
    }
    private void effectPassivePower(){
        if (passivePowerState) {
            if (!passivePowerEffectTaken) {
                if (player.getPassivePower().equals("speedGameUp")) {
                    gameSpeed /= speedChange;
                } else if (player.getPassivePower().equals("slowPlayerDown")) {
                    playerSpeed *= speedChange;
                } else if (player.getPassivePower().equals("haltScore")) {
                    scoreIncrement = 0;
                } else if (player.getPassivePower().equals("lessPowers")) {

                } else if (player.getPassivePower().equals("dangerZoneHigher")) {
                    dangerZone += 20;
                }
                passivePowerEffectTaken = true;
            }
            if (System.currentTimeMillis() >= endPassivePowerTime) {
                if (player.getPassivePower().equals("speedGameUp")) {
                    gameSpeed *= speedChange;
                } else if (player.getPassivePower().equals("slowPlayerDown")) {
                    playerSpeed /= speedChange;
                } else if (player.getPassivePower().equals("haltScore")) {
                    scoreIncrement = 1;
                } else if (player.getPassivePower().equals("lessPowers")) {

                } else if (player.getPassivePower().equals("dangerZoneHigher")) {
                    dangerZone -= 20;
                }
                passivePowerState = false;
                passivePowerEffectTaken = false;
            }
        }
    }

    private void activateActivePower(){
        if (!player.getActivePower().equals("nothing")) {
            activePowerState = true;
        }
    }
    private void effectActivePower(){
        if (activePowerState) {
            if (!activePowerEffectTaken) {
                if (player.getActivePower().equals("slowGameDown")) {
                    gameSpeed *= speedChange;
                } else if (player.getActivePower().equals("speedPlayerUp")) {
                    playerSpeed /= speedChange;
                } else if (player.getActivePower().equals("doubleScore")) {
                    scoreIncrement = 2;
                } else if (player.getActivePower().equals("lessPowers")) {

                } else if (player.getActivePower().equals("dangerZoneLower")) {
                    dangerZone -= 20;
                }
                activePowerEffectTaken = true;
            }
            if (System.currentTimeMillis() >= endPassivePowerTime) {
                if (player.getPassivePower().equals("slowGameDown")) {
                    gameSpeed /= speedChange;
                } else if (player.getActivePower().equals("speedPlayerUp")) {
                    playerSpeed *= speedChange;
                } else if (player.getActivePower().equals("doubleScore")) {
                    scoreIncrement = 1;
                } else if (player.getActivePower().equals("lessPowers")) {

                } else if (player.getActivePower().equals("dangerZoneLower")) {
                    dangerZone += 20;
                }
                player.setActivePower("nothing");
                activePowerState = false;
                activePowerEffectTaken = false;
            }
        }
    }

    protected boolean[] createArray(boolean b){
        boolean[] array = new boolean[GAME_WIDTH];
            for (int i = 0; i < GAME_WIDTH; i++) {
                array[i] = b;
            }
        return array;
    }

    protected boolean isPassive(Power newPower) {
        int index=0;
        for (int i=0; i<TYPES_OF_POWER.length; i++) {
            if (newPower.getType().equals(TYPES_OF_POWER[i])) {
                index = i;
                break;
            }
        }
        if (index<TYPES_OF_POWER.length/2) return false;
        return true;
    }
}

