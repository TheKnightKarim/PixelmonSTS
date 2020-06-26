package io.github.theknightkarim.pixelmonsts.configs;

import io.github.theknightkarim.pixelmonsts.PixelmonSTS;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Method;

@net.minecraftforge.common.config.Config(modid = PixelmonSTS.MOD_ID, name = PixelmonSTS.MOD_NAME + "/config")
public class Config {

    @net.minecraftforge.common.config.Config.Comment({
            "Set to true if you want to have a cooldown on STS"
    })
    public static boolean Cooldown = true;

    @net.minecraftforge.common.config.Config.Comment({
            "Default Cooldown Timer (Seconds)",
            "You can set a cooldown per user/group if you give them the meta key (sts.cooldown) and set that timer! (Needs luckperms)"
    })
    @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    public static int CooldownTime = 60;

    @net.minecraftforge.common.config.Config.Comment({
            "Set to true if you want to have a UI to bypass cooldowns with money"
    })
    public static boolean CooldownPayment = true;

    @net.minecraftforge.common.config.Config.Comment({
            "Price for cooldown if CooldownPayment is set to true"
    })
    @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    public static int CooldownPaymentPrice = 50000;

    @net.minecraftforge.common.config.Config.Comment({
            "Currency name (Coin is default)"
    })
    public static String currency = "Coin";

    @net.minecraftforge.common.config.Config.Comment({
            "Set to true if you want to make eggs sellable"
    })
    public static boolean EggBool = false;

    @net.minecraftforge.common.config.Config.Comment({
            "Set to true if you allow Bulk mode"
    })
    public static boolean Bulk = true;

    @net.minecraftforge.common.config.Config.Comment({
            "Set to amount of pokemon allowed in bulk selling"
    })
    @net.minecraftforge.common.config.Config.RangeInt(min = 2, max = 840)
    public static int MaxPokemoninBulk = 28;

    public static void reloadConfig() {
        try {
            Method getCfg = ConfigManager.class.getDeclaredMethod("getConfiguration", String.class, String.class);
            getCfg.setAccessible(true);
            Configuration cfg = (Configuration) getCfg.invoke(null, PixelmonSTS.MOD_ID, PixelmonSTS.MOD_NAME + "/config");
            if(cfg == null) {
                return;
            }
            cfg.load();
            Cooldown = cfg.get("general", "Cooldown", true).getBoolean();
            CooldownTime = cfg.get("general", "CooldownTime", 60).getInt();
            CooldownPayment = cfg.get("general", "CooldownPayment", true).getBoolean();
            CooldownPaymentPrice = cfg.get("general", "CooldownPaymentPrice", 50000).getInt();
            currency = cfg.get("general", "currency", "Coin").getString();
            EggBool = cfg.get("general", "EggBool", false).getBoolean();
            Bulk = cfg.get("general", "Bulk", true).getBoolean();
            MaxPokemoninBulk = cfg.get("general", "MaxPokemoninBulk", 28).getInt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
