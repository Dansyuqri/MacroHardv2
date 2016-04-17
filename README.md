## Introduction

This is a project done during the **Elements of Software Construction** course. The project deliverables consist of a concurrent multiplayer game (set in Android) in which players are able to interact with each other in real time.
At the end of the course, the game will potentially be pushed onto the Google Play Store.

## Project contents

This project is mainly based on BadLogicGames' libGDX open source library.
#### Using the libGDX library
After generating the library in the folder of your choice, AndroidLauncher will initialize MacroHardv2 class, which essentially uses libGDX's library, with the following lines of code in the onCreate() method.
		
			@Override
        	protected void onCreate (Bundle savedInstanceState) {
        		super.onCreate(savedInstanceState);
        		_gameHelper = new GSGameHelper(this, GameHelper.CLIENT_GAMES);
        		_gameHelper.enableDebugLog(false);
        
        		GameHelperListener gameHelperListerner = new GameHelper.GameHelperListener() {
        
        			@Override
        			public void onSignInSucceeded() {
        				// TODO Auto-generated method stub
        
        			}
        
        			@Override
        			public void onSignInFailed() {
        				// TODO Auto-generated method stub
        
        			}
        		};
        		_gameHelper.setup(gameHelperListerner);
        		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        		config.useImmersiveMode = true;
        
        		initialize(new MacroHardv2(this), config);
        		Gdx.graphics.requestRendering();
        	}

####Contents
1. android
    - AndroidLauncher
    - GSGameHelper
2. core
    - ActionResolver
    - MacroHardv2
    - **customEnum**
        - Direction
        - MapTile
        - MessageCode
        - PowerType
        - Stage
        - StageType
    - **Interface**
        - Collidable
    - **objects**
        - Background
        - Boulder
        - CustomButton
        - DangerZone
        - Direction
        - Door
        - Fog
        - GameObject
        - Ghost
        - Hole
        - Icon
        - JoyStick
        - MagicCircle
        - Movable
        - Obstacle
        - Overlay
        - Player
        - Power
        - Sand
        - SideWall
        - Spikes
        - Switch
        - UI
    - **states**
        - GameStateManager
        - InstructionState
        - MapMaker
        - MapSynchronizer
        - MenuState
        - PlayerCoordinateSender
        - PlayState
        - PlayStateHost
        - PlayStateNonHost
        - RestartState
        - State
    


## Motivation

This game was done as part of a project under the Elements of Software Construction module, during term 5 in the Information Systems Technology and Design pillar in Singapore University of Technology and Design. The motivation beyond the purpose of solely an academic project, is the fulfilment of creating a complete game, and also value adding to the game by producing updates if the game is deemed successful.

## Installation

Simply pull the git repo and run the application either on an Android Emulator or your Android device.

##### Note 
Request an invite to the Google Play Services game room list in order to play this game in multiplayer mode. Contact any of the contributors for this access.
Do change the directory of the storeFile found in the **build.gradle(Module: android)**

        storeFile file('C:/Users/User/Desktop/GDX/macrokey.jks')

## API Reference

Kindly refer to the LibGDX website for full API documentation. [LibGDX API](https://libgdx.badlogicgames.com/nightlies/docs/api/)


## Contributors

Lim Zhi Han Ryan [nayr43](https://github.com/nayr43)  
Muhammad Syuqri bin Johanna [dansyuqri](https://github.com/dansyuqri)  
Nguyen Dang Minh [ndmnh](https://github.com/ndmnh)  
Samuel Lim Jik Hao [sabbath65](https://github.com/sabbath65)  
Sng Han Jie [hansthefearless](https://github.com/hansthefearless)

## License

A short snippet describing the license (MIT, Apache, etc)

descriptions of all the project, and all sub-modules and libraries
copyright and licensing information (or "Read LICENSE")
instructions to submit bugs, feature requests, submit patches, join mailing list, get announcements, or join the user or dev community in other forms
other contact info (email address, website, company name, address, etc)
a brief history if it's a replacement or a fork of something else
legal notices (crypto stuff)