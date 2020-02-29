package io.github.theknightkarim.configs;

import io.github.theknightkarim.PixelmonSTS;
import net.minecraftforge.common.config.Config;

@Config(modid = PixelmonSTS.MOD_ID, name = PixelmonSTS.MOD_NAME + "/boosters")
public class Boosters {

    @Config.Comment({
            "Shiny Booster"
    })
    public static boolean Shiny = true;

    @Config.Comment({
            "Custom Texture Booster"
    })
    public static boolean CustomTexture = true;

    @Config.Comment({
            "Legendary Booster"
    })
    public static boolean Legendary = true;

    @Config.Comment({
            "Ultra Beast Booster"
    })
    public static boolean UltraBeast = true;

    @Config.Comment({
            "Max IV (per) Booster"
    })
    public static boolean MaxIV = true;

    @Config.Comment({
            "Max EV (per) Booster"
    })
    public static boolean MaxEV = true;

    @Config.Comment({
            "Level Booster"
    })
    public static boolean Level = true;

    @Config.Comment({
            "Hidden Ability Booster"
    })
    public static boolean HA = true;
}
