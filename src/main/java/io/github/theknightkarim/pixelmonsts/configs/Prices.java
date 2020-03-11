package io.github.theknightkarim.pixelmonsts.configs;

import io.github.theknightkarim.pixelmonsts.PixelmonSTS;
import net.minecraftforge.common.config.Config;

@Config(modid = PixelmonSTS.MOD_ID, name = PixelmonSTS.MOD_NAME + "/prices")
public class Prices {

    @Config.Comment({
            "Shiny price"
    })
    public static int Shiny = 50000;

    @Config.Comment({
            "Custom Texture price"
    })
    public static int CustomTexture = 4000;

    @Config.Comment({
            "Legendary price"
    })
    public static int Legendary = 5000;

    @Config.Comment({
            "Ultra Beast price"
    })
    public static int UltraBeast = 4500;

    @Config.Comment({
            "Max IV (per) price"
    })
    public static int MaxIV = 500;

    @Config.Comment({
            "Max EV (per) price"
    })
    public static int MaxEV = 250;

    @Config.Comment({
            "Level price (per)"
    })
    public static int Level = 10;

    @Config.Comment({
            "Max Level (put it as Level * 100 if you don't want a different price)"
    })
    public static int MaxLevel = 900;

    @Config.Comment({
            "Hidden Ability price"
    })
    public static int HA = 6000;

    @Config.Comment({
            "Base pokemon price"
    })
    public static int Base = 700;

    @Config.Comment({
            "Egg price (If eggBool is set to true in config)"
    })
    public static int Egg = 5;
}
