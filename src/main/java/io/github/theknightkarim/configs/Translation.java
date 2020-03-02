package io.github.theknightkarim.configs;

import io.github.theknightkarim.PixelmonSTS;
import net.minecraftforge.common.config.Config;

@Config(modid = PixelmonSTS.MOD_ID, name = "PixelmonSTS/translation")
public class Translation {
    public static StatLore lore = new StatLore();
    public static PriceLore price = new PriceLore();

    public static class StatLore {
        public static String egglore = "&9Pokemon: ???";
        public static String levellore = "&7Level&f: &b";
        public static String abilitylore = "&7Ability&f: &b";
        public static String naturelore = "&7Nature&f: &b";
        public static String growthlore = "&7Growth&f: &b";
        public static String IVslore = "&7IVs&f: ";
        public static String EVslore = "&7EVs&f: ";
        public static String genderlore = "&7Gender&f: ";
    }

    public static class PriceLore {
        public static String shiny = "&bShiny: &f";
        public static String customtexture = "&bCustom Texture: &f";
        public static String legendary = "&bLegendary: &f";
        public static String ultrabeast = "&bUltra Beast: &f";
        public static String maxIVEVnumbercolor = "&d";
        public static String maxIVs = "&bMax IV(s): &f";
        public static String maxEVs = "&bMax EV(s): &f";
        public static String maxLevel = "&bMax Level: &f";
        public static String level = "&bLevel &d";
        public static String levelpricecolor = "&b: &f";
        public static String HA = "&bHidden Ability: &f";
        public static String egg = "&bEgg: &f";
        public static String base = "&bBase price: &f";
        public static String totalprice = "&bTotal price: &f";
    }

    public static String eggButton = "&b&lEgg";
    public static String emptyPartySlot = "&cEmpty Party Slot";
    public static String emptyPCSlot = "&cEmpty PC Slot";
    public static String cooldownResetMessage = "&bYou have reset your cooldown!";
    public static String notEnoughMoneyMessage = "&cYou don't have enough money to reset your cooldown!";
    public static String soldPokemonForMessage = "&bYou have sold your pokemon for &6";
    public static String soldAllPokemonForMessage = "&bYou sold all your pokemon for &6";
    public static String exclamationPointcolor = "&b!";
    public static String shinyInName = "&7[&6Shiny&7]";
    public static String lastpokemoninparty = "&cYou have only one pokemon! You can't trade it away";

}
