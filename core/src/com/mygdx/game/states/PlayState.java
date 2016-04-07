package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MacroHardv2;
import com.mygdx.game.customEnum.Direction;
import com.mygdx.game.customEnum.MapTile;
import com.mygdx.game.customEnum.MessageCode;
import com.mygdx.game.customEnum.PowerType;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.Fog;
import com.mygdx.game.objects.GameObject;
import com.mygdx.game.objects.Ghost;
import com.mygdx.game.objects.Hole;
import com.mygdx.game.objects.Icon;
import com.mygdx.game.objects.Movable;
import com.mygdx.game.objects.Overlay;
import com.mygdx.game.objects.Door;
import com.mygdx.game.objects.Obstacle;
import com.mygdx.game.objects.Power;
import com.mygdx.game.objects.SideWall;
import com.mygdx.game.objects.Spikes;
import com.mygdx.game.objects.Switch;
import com.mygdx.game.objects.JoyStick;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.UI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;

/**
 * Created by Syuqri on 3/7/2016.
 */
public abstract class PlayState extends State{

    //Synchronising
    protected boolean sync = false;

    //objects
    protected Semaphore mapPro;
    protected Semaphore mapCon;
    protected Semaphore mapMod;
    protected Semaphore spawn;

    private JoyStick joystick;
    protected Player player;
    private Vector3 touchPos = new Vector3();

    protected PlayerCoordinateSender coordSender;
    private MapSynchronizer mapSynchronizer;

    //values
    private int mapCounter;
    protected final int GAME_WIDTH = 9;
    protected final int playerID;
    private boolean running;

    private boolean touchHeld, gotSwitch = false, onSwitch = false, end = false;
    protected float gameSpeed, speedChange, speedIncrease, dangerZoneSpeedLimit, tempGameSpeed;
    protected int playerSpeed, dangerZone, powerCounter, doorCounter;
    public float tracker;
    public float trackerBG;
    private int score;
    private String yourScoreName;
    BitmapFont yourBitmapFontName;
    public Stage stage;
    public Direction orientation;
    public boolean touched;
    float animateTime;

    private HashMap<Integer, MapTile[]> messageBuffer = new HashMap<Integer, MapTile[]>();

    //boolean arrays
    public MapTile[] path = createArray(MapTile.EMPTY);

    //Arraylists
    protected ArrayList<MapTile[]> mapBuffer = new ArrayList<MapTile[]>();

    private ArrayList<ArrayList<GameObject>> gameObjects = new ArrayList<ArrayList<GameObject>>();
    protected ArrayList<GameObject> players = new ArrayList<GameObject>(
            Arrays.asList(
                    new Player[]{
                            new Player(0), new Player(1), new Player(2)
                    }));
    private ArrayList<GameObject> obstacles = new ArrayList<GameObject>();
    private ArrayList<GameObject> sideWalls = new ArrayList<GameObject>();
    private ArrayList<GameObject> switches = new ArrayList<GameObject>();
    private ArrayList<GameObject> doors = new ArrayList<GameObject>();
    private ArrayList<GameObject> powers = new ArrayList<GameObject>();
    private ArrayList<GameObject> spikes = new ArrayList<GameObject>();
    private ArrayList<GameObject> holes = new ArrayList<GameObject>();
    private ArrayList<GameObject> bg = new ArrayList<GameObject>();
    private ArrayList<GameObject> effects = new ArrayList<GameObject>();
    private ArrayList<GameObject> ui = new ArrayList<GameObject>();
    private ArrayList<GameObject> icons = new ArrayList<GameObject>();
    private ArrayList<GameObject> ghosts = new ArrayList<GameObject>();
    private ArrayList<GameObject> fogs = new ArrayList<GameObject>();

    //final values
    final int tileLength = 50;

    protected ScheduledThreadPoolExecutor backgroundTaskExecutor = new ScheduledThreadPoolExecutor(3);

    protected PlayState(GameStateManager gsm, int playerID) {
        super(gsm);

        this.playerID = playerID;
        player = (Player) players.get(playerID);

        mapPro = new Semaphore(15);
        mapMod = new Semaphore(1);
        spawn = new Semaphore(0);
        mapCounter = 0;

        touchHeld = false;

        //camera initialization
        cam = new OrthographicCamera(480,800);
        cam.setToOrtho(false, 480, 800);

        //object initialization
        joystick = new JoyStick();

        //misc values initialization
        gameSpeed = 60;
        speedIncrease = (float) 0.07;
        speedChange = (float) 0.4;
        playerSpeed = 200;
        dangerZone = 300;
        powerCounter = 0;
        doorCounter = 0;
        dangerZoneSpeedLimit = 250;
        stage = Stage.DUNGEON;
        tracker = 800;
        trackerBG = 800;
        score = 0;
        yourScoreName = "score: 0";
        yourBitmapFontName = new BitmapFont();
        orientation = Direction.NORTH;
        touched = false;
        animateTime = 0f;

        gameObjects.add(bg);
        gameObjects.add(spikes);
        gameObjects.add(holes);
        gameObjects.add(powers);
        gameObjects.add(doors);
        gameObjects.add(obstacles);
        gameObjects.add(sideWalls);
        gameObjects.add(switches);
        gameObjects.add(players);
        gameObjects.add(ghosts);
        gameObjects.add(fogs);
        gameObjects.add(effects);
        gameObjects.add(ui);
        gameObjects.add(icons);

        Thread timeCheck = new Thread(){
            @Override
            public void run(){
                while(Thread.currentThread().isAlive()){
                    effectPassivePower();
                    effectActivePower();
                }
            }
        };

        timeCheck.start();
        createBg();
        createObstacle();
        createSides();

        mapSynchronizer = new MapSynchronizer();

        coordSender = new PlayerCoordinateSender(this);
        running = false;
    }

    @Override
    protected void handleInput() {
        touched = Gdx.input.isTouched();
        if(touched) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(),0);
            cam.unproject(touchPos);
            float relativex = touchPos.x - (joystick.getX());
            float relativey = touchPos.y - (joystick.getY());

            if (touchHeld ||
                    (Math.abs(relativex) < joystick.getJoystickWidth()/2 &&
                            Math.abs(relativey) < joystick.getJoystickWidth()/2)) {
                touchHeld = true;

                //calculates the relevant numbers needed for omnidirectional movement
                float angle = (float) Math.atan2(relativey, relativex);
                if (angle > (Math.PI)/4 && angle <= 3*(Math.PI)/4 ){
                    orientation = Direction.NORTH;
                }
                else if (angle > 3*(Math.PI)/4 || angle <= -3*(Math.PI)/4 ){
                    orientation = Direction.WEST;
                }
                else if (angle < -(Math.PI)/4 && angle >= -3*(Math.PI)/4 ){
                    orientation = Direction.SOUTH;
                }
                else if (angle > -(Math.PI)/4 && angle <= (Math.PI)/4 ){
                    orientation = Direction.EAST;
                }
                player.setOrientation(orientation);
                float cos = (float) Math.cos(angle);
                float sin = (float) Math.sin(angle);
                float ratio;
                //setting joystick centre coordinates
                if ((Math.abs(relativex) < joystick.getJoystickWidth()/2 &&
                        Math.abs(relativey) < joystick.getJoystickWidth()/2)){
                    ratio = (float) ((Math.pow(relativex, 2) + Math.pow(relativey, 2))/Math.pow(joystick.getJoystickWidth()/2,2));
                    joystick.setCX(touchPos.x - joystick.getJoystickCenterWidth() / 2);
                    joystick.setCY(touchPos.y - joystick.getJoystickCenterHeight()/2);
                } else {
                    ratio = 1;
                    joystick.setCX(joystick.getX() - joystick.getJoystickCenterWidth()/2 + joystick.getJoystickWidth()/2*cos);
                    joystick.setCY(joystick.getY() - joystick.getJoystickCenterHeight()/2 + joystick.getJoystickWidth()/2*sin);
                }

                omniMove(cos, sin, (float) Math.pow(ratio, 0.5));
            } else {
                if (!icons.isEmpty()){
                    if (icons.get(0).contains(touchPos.x, touchPos.y)){
                        activateActivePower();
                        icons.remove(0);
                    }
                }
            }
        } else {
            touchHeld = false;
            joystick.setCX(joystick.getX() - joystick.getJoystickCenterWidth()/2);
            joystick.setCY(joystick.getY() - joystick.getJoystickCenterHeight()/2);
        }
    }

    @Override
    public void render(SpriteBatch sb) {

        //Host
        if(!sync){
            sync = true;
            mapSynchronizer.sync();
        }

        if (!running){
            running = true;
            backgroundTaskExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    coordSender.send();
                }
            }, 0, 1, TimeUnit.MILLISECONDS);
        }

        handleInput();

        while (tracker < 1000 || spawn.availablePermits() > 0) {
            try {
                mapCon.acquire();
                mapMod.acquire();
                path = mapBuffer.remove(0);
                mapMod.release();
                mapPro.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (playerID == 0) {
                mapSynchronizer.sendSpawnMessage();
                spawnObjects();
                spawnSides(tracker + tileLength);
                tracker += tileLength;
            } else {
                try {
                    spawn.acquire();
                    spawnObjects();
                    spawnSides(tracker + tileLength);
                    tracker += tileLength;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (trackerBG <= 1000) {
            spawnBg();
            trackerBG += 200;
        }
        // tell the camera to update its matrices.
        cam.update();
        // tell the SpriteBatch to render in the coordinate system specified by the camera.
        sb.setProjectionMatrix(cam.combined);

        // begin a new batch and draw the player and all objects
        sb.begin();
        draw(sb);
        sb.draw(joystick.getJoystickCentreImage(), joystick.getCX(), joystick.getCY());
        yourBitmapFontName.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        yourBitmapFontName.draw(sb, yourScoreName, 25, 100);

        sb.end();

        checkSwitchCollision();
        checkBoundaryCollision();
        checkFatalCollision();

        checkDangerZone(player);

        tracker -= gameSpeed * Math.min(Gdx.graphics.getDeltaTime(), (float) 0.03);

        trackerBG -= gameSpeed * Math.min(Gdx.graphics.getDeltaTime(), (float) 0.03);

        for (ArrayList<GameObject> gameObj: gameObjects){
            for (GameObject gameObject : gameObj) {
                if (gameObject instanceof Movable) {
                    ((Movable) gameObject).scroll(gameSpeed);
                }
            }
        }

        mapSynchronizer.scroll(gameSpeed);

        synchronized (this) {
            if (end) {
                goToRestartState();
            }
        }
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        for (ArrayList<GameObject> gameObj: gameObjects) {
            for (GameObject gameObject : gameObj) {
                gameObject.getImage().dispose();
            }
        }
        joystick.getJoystickImage().dispose();
        joystick.getJoystickCentreImage().dispose();
    }

    /***************************************************
     *  CREATE METHODS HERE
     ****************************************************
     */

    private void createObstacle() {
        for (int i = 0; i < path.length; i++) {
            if (path[i] == MapTile.OBSTACLES) {
                Obstacle obstacle = new Obstacle((tileLength * i) + 15, 800, tileLength, tileLength, stage);
                obstacles.add(obstacle);
            }
        }
    }

    private void createBg(){
        int counter = 0;
        while (counter*200 < 800) {
            Background backg = new Background(counter*200, stage);
            Overlay effect = new Overlay(counter*200, stage);
            bg.add(backg);
            effects.add(effect);
            counter++;
        }
        UI inter = new UI(0);
        ui.add(inter);
    }

    private void createSides(){
        int counter = 0;
        while (counter* tileLength <= 800) {
            for (int i = 0; i < 2; i++) {
                SideWall sideWall = new SideWall(tileLength, counter* tileLength, i);
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

    private void spawnObjects(){
        for (int i = 0; i < path.length; i++) {
            switch (path[i]){
                case SWITCH:
                    switches.add(new Switch((tileLength * (i % GAME_WIDTH) + 15), tracker, tileLength, tileLength, stage));
                    break;
                case OBSTACLES:
                    obstacles.add(new Obstacle((tileLength * (i % GAME_WIDTH) + 15), tracker, tileLength, tileLength, stage));
                    break;
                case POWER:
                    powers.add(new Power(PowerType.values()[(int)(Math.random() * (PowerType.values().length-1) + 1)], tileLength * (i % GAME_WIDTH) + 15, tracker, tileLength, tileLength));
                    break;
                case DOOR:
                    doors.add(new Door((tileLength * (i % GAME_WIDTH)) + 15, tracker, tileLength, tileLength, stage));
                    break;
                case SPIKES:
                    if (score > 100 && stage == Stage.DUNGEON) {
                        spikes.add(new Spikes((tileLength * (i % GAME_WIDTH)) + 20, tracker + 5, 40, 40));
                    }
                    break;
                case HOLE:
                    if (stage == Stage.ICE){
                        holes.add(new Hole((tileLength * (i % GAME_WIDTH)) + 15, tracker, tileLength, tileLength,stage));
                    }
                    break;
            }
        }
        if (score > 200 && stage == Stage.DUNGEON) {
            if (score % 15 == 0) {
                ghosts.add(new Ghost((tileLength * 9) + 15, tracker, tileLength, tileLength));
            }
        }
    }

    private void spawnBg(){
        if (score % 20 == 0){
            stage = Stage.values()[MathUtils.random(0, 1)];
        }
        Background backg = new Background(trackerBG, stage);
        Overlay effect = new Overlay(trackerBG, stage);
        bg.add(backg);
        effects.add(effect);
        if (stage == Stage.ICE){
            Fog fog = new Fog(0,trackerBG);
            fogs.add(fog);
        }
    }

    /**
     Spawn side walls to fill up the gap between the playing field and the actual maze
     */
    private void spawnSides(float in){
        score++;
        yourScoreName = "score: " + score;
        for (int i = 0; i < 2; i++) {
            SideWall sideWall = new SideWall(tileLength,in,i);
            sideWalls.add(sideWall);
        }
    }
    /**
     Method to move the player
     */
    private void omniMove(float x, float y, float ratio){
        float prevx = player.x;
        float prevy = player.y;
        player.x += ratio * x * playerSpeed * Math.min(Gdx.graphics.getDeltaTime(), (float) 0.03);
        player.y += ratio * y * playerSpeed * Math.min(Gdx.graphics.getDeltaTime(), (float) 0.03);
        if (checkObstacleCollision()){
            player.x = prevx;
            player.y = prevy;

            if (x > 0) {
                player.x += ratio * playerSpeed * Math.min(Gdx.graphics.getDeltaTime(), (float) 0.03);
            } else {
                player.x -= ratio * playerSpeed * Math.min(Gdx.graphics.getDeltaTime(), (float) 0.03);
            }
            if (checkObstacleCollision()){
                player.x = prevx;
            }
            if (y > 0) {
                player.y += ratio * playerSpeed * Math.min(Gdx.graphics.getDeltaTime(), (float) 0.03);
            } else {
                player.y -= ratio * playerSpeed * Math.min(Gdx.graphics.getDeltaTime(), (float) 0.03);
            }
            if (checkObstacleCollision()){
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

    private void checkBoundaryCollision(){
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

        if (player.y < 150){
            MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.END_GAME});
            goToRestartState();
        }
    }
    private boolean checkObstacleCollision(){

//    		collides with normal wall obstacle
        Iterator<GameObject> obstacleIterator = obstacles.iterator();
        while (obstacleIterator.hasNext()){
            if (((Obstacle)obstacleIterator.next()).collides(player, this)){
//              DESTROY_WALL implementation
                if (player.getCanDestroy()) {
                    obstacleIterator.remove();
                    score++;
                    yourScoreName = "score: " + score;
                    break;
                }
                return true;
            }
        }
        //		collides with doors
        Iterator<GameObject> doorIterator = doors.iterator();
        while (doorIterator.hasNext()){
            if (((Door)doorIterator.next()).collides(player, this)){
//              DESTROY_WALL implementation
                if (player.getCanDestroy()) {
                    doorIterator.remove();
                    break;
                }
                return true;
            }
        }

        return false;
    }

    private void checkFatalCollision(){
        //      collide with spikes
        for (GameObject spike : spikes) {
            if (((Spikes) spike).collides(player, this)) {
                MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.END_GAME});
                goToRestartState();
            }
        }

        //      collide with ghosts
        for (GameObject ghost : ghosts) {
            if (((Ghost) ghost).collides(player, this)) {
                MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.END_GAME});
                goToRestartState();
            }
        }

        // collides with holes
        for (GameObject hole1 : holes) {
            final Hole hole = ((Hole) hole1);
            if (hole.collides(player, this)) {
                if (hole.isBroken()) {
                    goToRestartState();
                } else {
                    backgroundTaskExecutor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            hole.setBreakHole(true);
                        }
                    }, Hole.HOLE_BREAK_TIME, TimeUnit.SECONDS);
                }
            }

            if (hole.isBreakHole()) {
                hole.setBroken();
            }
        }

    }

    /**
     Method to calculate and randomize the wall, power, switch etc. placement. It keeps randomly
     generating a sequence until it fulfils the condition where the player can reach to the next
     layer of walls. I.e, no dead ends. Also ensures that the power is spawned on a 'box' not
     occupied by a wall and that switches are reachable.
     */

    private void checkSwitchCollision(){
        //		collides with switch

        boolean open = false;
        for (GameObject eachSwitch:switches){
            if (((Switch) eachSwitch).collides(player, this)){
                open = true;
                ((Switch) eachSwitch).setOn();
            } else {
                ((Switch) eachSwitch).setOff();
            }
        }

        if (!open && onSwitch) {
            MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.CLOSE_DOORS});
        }

        onSwitch = open;

        synchronized (Switch.class) {
            if (gotSwitch) {
                open = true;
            }
        }

        if (open) {
            for (GameObject door : doors) {
                ((Door) door).setOpen();
            }
        } else {
            for (GameObject door: doors){
                ((Door)door).setClose();
            }
        }

        //		collides with power up
        Iterator<GameObject> powerIterator = powers.iterator();
        while (powerIterator.hasNext()){
            if (((Power)powerIterator.next()).collides(player, this)){
                powerIterator.remove();
            }
        }
    }

    /**
     Methods handling power-ups/affecting game attributes
     */

    private void checkDangerZone(Player p) {
        if (p.getY()<=dangerZone && gameSpeed<=dangerZoneSpeedLimit) {
            if (!(player.getPassivePower().equals(PowerType.FREEZE_MAZE)&&player.getPassivePowerState())) {
                gameSpeed += speedIncrease;
            }
        }
    }

    private void effectPassivePower(){
        if (player.getPassivePowerState()) {
            if (!player.getPassivePowerEffectTaken()) {
                if (player.getPassivePower().equals(PowerType.FREEZE_MAZE)) {
                    synchronized (Player.class) {
                        tempGameSpeed = gameSpeed;
                    }
                    gameSpeed = 0;
                    sendGameSpeed();
                } else if (player.getPassivePower().equals(PowerType.SLOW_GAME_DOWN)) {
                    gameSpeed *= speedChange;
                    sendGameSpeed();
                } else if (player.getPassivePower().equals(PowerType.SPEED_PLAYER_UP)) {
                    playerSpeed /= speedChange;
                }
                player.setPassivePowerEffectTaken(true);
            }
            if (System.currentTimeMillis() >= player.getEndPassivePowerTime()) {
                if (player.getPassivePower().equals(PowerType.FREEZE_MAZE)) {
                    synchronized (Player.class) {
                        gameSpeed = tempGameSpeed;
                    }
                    sendGameSpeed();
                } else if (player.getPassivePower().equals(PowerType.SLOW_GAME_DOWN)) {
                    gameSpeed /= speedChange;
                    sendGameSpeed();
                } else if (player.getPassivePower().equals(PowerType.SPEED_PLAYER_UP)) {
                    playerSpeed *= speedChange;
                }
                player.setPassivePowerState(false);
                player.setPassivePowerEffectTaken(false);
            }
        }
    }

    private void activateActivePower(){
        player.setActivePowerState(true);
        player.setEndActivePowerTime(System.currentTimeMillis()+5000);

    }

    private void effectActivePower(){
        if (player.getActivePowerState()) {
            if (!player.getActivePowerEffectTaken()) {
                if (player.getActivePower().equals(PowerType.DESTROY_WALL)) {
                    player.setCanDestroy(true);
                }
                player.setActivePowerEffectTaken(true);
            }
            if (System.currentTimeMillis() >= player.getEndActivePowerTime()) {
                if (player.getActivePower().equals(PowerType.DESTROY_WALL)) {
                    player.setCanDestroy(false);
                }
                player.setActivePower(PowerType.NOTHING);
                player.setActivePowerState(false);
                player.setActivePowerEffectTaken(false);
            }
        }
    }

    protected MapTile[] createArray(MapTile m){
        MapTile[] array = new MapTile[GAME_WIDTH];
        for (int i = 0; i < GAME_WIDTH; i++) {
            array[i] = m;
        }
        return array;
    }

    protected boolean[] createArray(boolean b){
        boolean[] array = new boolean[GAME_WIDTH];
        for (int i = 0; i < GAME_WIDTH; i++) {
            array[i] = b;
        }
        return array;
    }

    public void draw(SpriteBatch sb){
        for (ArrayList<GameObject> gameObjList: gameObjects) {
            Iterator<GameObject> gameObjectIterator = gameObjList.iterator();
            while (gameObjectIterator.hasNext()){
                GameObject gameObject = gameObjectIterator.next();
                if (gameObject.y < -gameObject.height){
                    if (!(gameObject instanceof Player)) {
                        gameObjectIterator.remove();
                        continue;
                    }
                }
                if (gameObject instanceof Player){
                    animateTime += Gdx.graphics.getDeltaTime();
                    if (touched) {
                        ((Player) gameObject).setCurrentFrame(orientation, animateTime, true);
                    }
                    else {
                        ((Player) gameObject).setOrientation(orientation);
                    }
                }
                gameObject.draw(sb);
            }
        }
    }

    public void addIcon(Icon icon) {
        icons.add(icon);
    }

    private void sendGameSpeed(){
        byte[] message = new byte[3];
        message[0] = MessageCode.CHANGE_GAME_SPEED;
        message[1] = (byte) (gameSpeed / 10);
        message[2] = (byte) ((gameSpeed * 10) % 100);
        MacroHardv2.actionResolver.sendReliable(message);
    }

    public void update(byte[] message) {
        //update player coordinates
        if(message != null){
            switch(message[0]) {

                //other player's coordinates
                case MessageCode.PLAYER_POSITION:
                    int other = (int) message[1];
                    if (!mapSynchronizer.isSet(other)){
                        mapSynchronizer.set(other);
                    }
                    float x = (float) message[2] * 10 + (float) message[3] / 10;
                    float y = mapSynchronizer.offset((float) message[4] * 10 + (float) message[5] / 10, other);
                    players.get(other).x = x;
                    players.get(other).y = y;
                    break;

                //map
                case MessageCode.MAP_TILES:
                    MapTile[] new_row = new MapTile[GAME_WIDTH];
                    for (int i = 2; i < message.length; i++) {
                        new_row[i - 2] = MapTile.fromByte(message[i]);
                    }

                    try {
                        mapPro.acquire();
                        if (message[1] == mapCounter){
                            mapMod.acquire();
                            mapBuffer.add(new_row);
                            mapMod.release();
                            mapCounter = (mapCounter + 1) % 15;
                            mapCon.release();
                        } else {
                            messageBuffer.put((int) message[1], new_row);
                            if (messageBuffer.containsKey(mapCounter)) {
                                mapMod.acquire();
                                mapBuffer.add(messageBuffer.get(mapCounter));
                                mapMod.release();
                                messageBuffer.remove(mapCounter);
                                mapCounter = (mapCounter + 1) % 15;
                                mapCon.release();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                //open doors
                case MessageCode.OPEN_DOORS:
                    synchronized (Switch.class) {
                        gotSwitch = true;
                    }
                    break;

                //close doors
                case MessageCode.CLOSE_DOORS:
                    synchronized (Switch.class) {
                        gotSwitch = false;
                    }
                    break;

                //game speed update
                case MessageCode.CHANGE_GAME_SPEED:
                    synchronized (Player.class) {
                        gameSpeed = ((float) message[1] * 10 + (float) message[2] / 10);
                    }
                    break;

                //end game
                case MessageCode.END_GAME:
                    synchronized (this){
                        end = true;
                    }
                    break;

                case MessageCode.SYNCING:
                    if(message[2] == 0 && (MacroHardv2.actionResolver.getmyidint() != 0)){
                        byte[] temp = new byte[4];
                        //Message ID
                        temp[0] = 5;
                        //Origin of message
                        temp[1] = (byte) MacroHardv2.actionResolver.getmyidint();
                        //0 for ping, 1 for sleep
                        temp[2] = 0;
                        //Sleep duration
                        temp[3] = 0;
                        MacroHardv2.actionResolver.sendReliable(temp);
                        break;
                    }
                    else if(message[2] == 0 && (MacroHardv2.actionResolver.getmyidint() == 0)){
                        mapSynchronizer.gethost().countDown();
                        break;
                    }
                    else if(message[2] == 1){
                        mapSynchronizer.getplayer1().countDown();
                    }

                    break;

                case MessageCode.SYNC_TRACKER:
                    spawn.release();
                    break;
            }
        }
    }

    public void goToRestartState(){
        backgroundTaskExecutor.shutdownNow();
        dispose();
        gsm.set(new RestartState(gsm, getScore()));
    }

    public int getScore() {
        return score;
    }
}