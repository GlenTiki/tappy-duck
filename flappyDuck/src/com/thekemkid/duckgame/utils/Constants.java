package com.thekemkid.duckgame.utils;

public class Constants {

    // Visible game world is 5 meters wide
    public static final float   VIEWPORT_WIDTH          = 5.0f;
    // Visible game world is 5 meters tall
    public static final float   VIEWPORT_HEIGHT         = 5.0f;
    // GUI Width
    public static final float   VIEWPORT_GUI_WIDTH      = 800.0f;
    // GUI Height
    public static final float   VIEWPORT_GUI_HEIGHT     = 480.0f;
    // Location of description file for texture atlas
    public static final String  TEXTURE_ATLAS_OBJECTS   = "images/flappyduck.pack";
    // Location of image file for level 01
    public static final String  LEVEL_01                = "levels/level-01.png";
 // Location of image file for level 02
    public static final String  LEVEL_02                = "levels/level-02.png";
    // Amount of extra lives at level start
    public static final int     LIVES_START             = 3;
    // Duration of feather power-up in seconds
    public static final float ITEM_FEATHER_POWERUP_DURATION = 9;
    // Delay after game over
    public static final float TIME_DELAY_GAME_OVER = 3;
	// Delay after winning
    public static final float TIME_DELAY_WIN = 3;
    // Number of levels
    public static final float NUM_LEVELS = 2;
}
