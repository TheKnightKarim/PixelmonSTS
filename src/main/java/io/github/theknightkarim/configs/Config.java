package io.github.theknightkarim.configs;

import io.github.theknightkarim.PixelmonSTS;

@net.minecraftforge.common.config.Config(modid = PixelmonSTS.MOD_ID, name = PixelmonSTS.MOD_NAME + "/config.cfg")
public class Config {

    @net.minecraftforge.common.config.Config.Comment({
            "Set to true if you want to have a cooldown on STS"
    })
    public static boolean Cooldown = true;

    @net.minecraftforge.common.config.Config.Comment({
            "Default Cooldown Timer (Seconds)",
            "You can set a cooldown per user/group if you give them the meta key (sts.cooldown) and set that timer! (Needs luckperms)"
    })
    public static int CooldownTime = 60;

    @net.minecraftforge.common.config.Config.Comment({
            "Set to true if you want to have a UI to bypass cooldowns with money"
    })
    public static boolean CooldownPayment = true;

    @net.minecraftforge.common.config.Config.Comment({
            "Price for cooldown if CooldwonPayment is set to true"
    })
    public static int CooldownPaymentPrice = 50000;

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
    public static int MaxPokemoninBulk = 28;

}
