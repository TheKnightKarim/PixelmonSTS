package io.github.theknightkarim;

import ca.landonjw.gooeylibs.inventory.api.InventoryAPI;
import io.github.theknightkarim.configs.Config;
import io.github.theknightkarim.utils.Command;
import io.github.theknightkarim.utils.Utils;
import net.luckperms.api.LuckPerms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Mod(
        modid = PixelmonSTS.MOD_ID,
        name = PixelmonSTS.MOD_NAME,
        version = PixelmonSTS.VERSION,
        acceptableRemoteVersions = "*",
        serverSideOnly = true
)
public class PixelmonSTS {

    public static final String MOD_ID = "pixelmonsts";
    public static final String MOD_NAME = "PixelmonSTS";
    public static final String VERSION = "1.1.5";
    public static File stsLog;
    public static File customprices;
    public static EconomyService economyService;
    public static Currency currency;
    public static LuckPerms api;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Optional<EconomyService> serviceOpt = Sponge.getServiceManager().provide(EconomyService.class);
        if (!serviceOpt.isPresent()) {
            System.out.println("Server doesn't have a economy plugin, PixelmonSTS won't work properly");
        } else {
            economyService = serviceOpt.get();
        }
        Optional<Currency> currencyCheck = Sponge.getRegistry().getType(org.spongepowered.api.service.economy.Currency.class, "economylite:" + Config.currency.toLowerCase());
        if (!currencyCheck.isPresent()) {
            System.out.println("Config currency is not recognized, Default currency is set");
            currency = economyService.getDefaultCurrency();
        } else {
            currencyCheck.ifPresent(value -> currency = value);
        }
        Optional<ProviderRegistration<LuckPerms>> provider = Sponge.getServiceManager().getRegistration(LuckPerms.class);
        if (provider.isPresent()) {
            api = provider.get().getProvider();
        } else {
            System.out.println("Luckperms isn't installed on your server, PixelmonSTS cooldowns won't work properly");
        }

        InventoryAPI.register();

        //Mod Directory
        File directory = new File(event.getModConfigurationDirectory().getPath(), MOD_NAME);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("PixelWT Directory created!");
            } else {
                System.out.println("PixelWT Directory error");
            }
        }

        //Custom GSON Prices
        customprices = new File(directory, "CustomPrices.json");
        boolean filexists = customprices.exists();
        if (!filexists) {
            try {
                filexists = customprices.createNewFile();
                Utils.writeGSONContent(customprices);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.getGSONContent(customprices);


        // STS Log
        stsLog = new File(directory,"STSLog.txt");
        boolean poolactionsexist = stsLog.exists();
        if (!poolactionsexist) {
            try {
                poolactionsexist = stsLog.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new Command());
    }
}
