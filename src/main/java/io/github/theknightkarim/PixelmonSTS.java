package io.github.theknightkarim;

import ca.landonjw.gooeylibs.inventory.api.InventoryAPI;
import com.sun.glass.ui.Pixels;
import net.luckperms.api.LuckPerms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Plugin(
        id = PixelmonSTS.MOD_ID,
        name = PixelmonSTS.MOD_NAME,
        version = PixelmonSTS.VERSION,
        description = "Pixelmon Sell to Server"
)
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
    public static final String VERSION = "1.0.0";
    private static File poolactions;
    public static EconomyService economyService;
    public static Currency currency;
    public static LuckPerms api;
    private static PluginContainer container = Sponge.getPluginManager().getPlugin(MOD_ID).get();

    @Mod.Instance(MOD_ID)
    public static PixelmonSTS INSTANCE;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        Optional<EconomyService> serviceOpt = Sponge.getServiceManager().provide(EconomyService.class);
        if (!serviceOpt.isPresent()) {
            System.out.println("Server doesn't have a economy plugin, PixelmonSTS won't work properly");
        } else {
            economyService = serviceOpt.get();
        }
        Optional<Currency> currencyCheck = Sponge.getRegistry().getType(org.spongepowered.api.service.economy.Currency.class, "economylite:coin");
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

        File directory = new File(event.getModConfigurationDirectory().getPath(), MOD_NAME);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("PixelmonSTS Directory created!");
            } else {
                System.out.println("PixelmonSTS Directory error");
            }
        }


        // Pool Actions TXT
        poolactions = new File(directory,"STSLog.txt");
        boolean poolactionsexist = poolactions.exists();
        if (!poolactionsexist) {
            try {
                poolactionsexist = poolactions.createNewFile();
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
