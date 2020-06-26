package io.github.theknightkarim.pixelmonsts.configs;

import io.github.theknightkarim.pixelmonsts.PixelmonSTS;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Method;

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

    public static void reloadConfig() {
        try {
            Method getCfg = ConfigManager.class.getDeclaredMethod("getConfiguration", String.class, String.class);
            getCfg.setAccessible(true);
            Configuration cfg = (Configuration) getCfg.invoke(null, PixelmonSTS.MOD_ID, PixelmonSTS.MOD_NAME + "/config");
            if(cfg == null) {
                return;
            }
            cfg.load();
            Shiny = cfg.get("general", "Shiny", true).getBoolean();
            CustomTexture = cfg.get("general", "CustomTexture", true).getBoolean();
            Legendary = cfg.get("general", "Legendary", true).getBoolean();
            UltraBeast = cfg.get("general", "UltraBeast", true).getBoolean();
            MaxIV = cfg.get("general", "MaxIV", true).getBoolean();
            MaxEV = cfg.get("general", "MaxEV", true).getBoolean();
            Level = cfg.get("general", "Level", true).getBoolean();
            HA = cfg.get("general", "HA", true).getBoolean();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
