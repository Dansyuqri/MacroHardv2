## Introduction

This is a project done during the **Elements of Software Construction** course. The project deliverables consist of a concurrent multiplayer game (set in Android) in which players are able to interact with each other in real time.
At the end of the course, the game will potentially be pushed onto the Google Play Store.

## Project contents

This project is mainly based on BadLogicGames' libGDX open source library.
#### Using the libGDX library
After generating the library in the folder of your choice, AndroidLauncher will automatically initialize MacroHardv2 class, which essentially uses libGDX's library.
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;

		initialize(new MacroHardv2(this), config);
		Gdx.graphics.requestRendering();

####Contents
1. android
    - AndroidLauncher
    - GSGameHelper
2. core
    - ActionResolver
    - MacroHardv2
    - **customEnum**
        - MapTile
        - MessageCode
        - PowerType
        - Stage
    - **Interface**
        - Collidable
    - **objects**
        - Background
        - CustomButton
        - DangerZone
        - Door
        - GameObject
        - Ghost
        - Hole
        - Icon
        - JoyStick
        - Movable
        - Obstacle
        - Overlay
        - Player
        - Power
        - SideWall
        - Spikes
        - Switch
        - UI
    - **states**
        - GameStateManager
        - InstructionState
        - MapMaker
        - MenuState
        - PlayerCoordinateSender
        - PlayState
        - PlayStateHost
        - PlayStateNonHost
        - RestartState
        - State
    


## Motivation


## Installation

Simply pull the git repo and run the application either on an Android Emulator or your Android device.

##### Note 
Do change the directory of the storeFile found in the **build.gradle(Module: android)**

        storeFile file('C:/Users/User/Desktop/GDX/macrokey.jks')

## API Reference

Depending on the size of the project, if it is small and simple enough the reference docs can be added to the README. For medium size to larger projects it is important to at least provide a link to where the API reference docs live.


## Contributors

Lim Zhi Han Ryan [nayr43](https://github.com/nayr43)  
Muhammad Syuqri bin Johanna [dansyuqri](https://github.com/dansyuqri)  
Nguyen Dang Minh [ndmnh](https://github.com/ndmnh)  
Samuel Lim Jik Hao [sabbath65](https://github.com/sabbath65)  
Sng Han Jie [hansthefearless](https://github.com/hansthefearless)

## License

A short snippet describing the license (MIT, Apache, etc)

descriptions of all the project, and all sub-modules and libraries
5-line code snippet on how its used (if it's a library)
copyright and licensing information (or "Read LICENSE")
instruction to grab the documentation
instructions to install, configure, and to run the programs
instruction to grab the latest code and detailed instructions to build it (or quick overview and "Read INSTALL")
list of authors or "Read AUTHORS"
instructions to submit bugs, feature requests, submit patches, join mailing list, get announcements, or join the user or dev community in other forms
other contact info (email address, website, company name, address, etc)
a brief history if it's a replacement or a fork of something else
legal notices (crypto stuff)