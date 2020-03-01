package io.github.theknightkarim.utils;

import ca.landonjw.gooeylibs.inventory.api.Button;
import ca.landonjw.gooeylibs.inventory.api.InventoryAPI;
import com.google.gson.*;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.theknightkarim.GSON.SellData;
import io.github.theknightkarim.PixelmonSTS;
import io.github.theknightkarim.configs.Config;
import io.github.theknightkarim.configs.Prices;
import io.github.theknightkarim.configs.Translation;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static HashMap<UUID, Button> playerButton = new HashMap<>();
    public static HashMap<UUID, Pokemon> playerPokemon = new HashMap<>();
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static LocalDateTime now = LocalDateTime.now();
    private static EnumMap<EnumSpecies, SellData> prices = new EnumMap<>(EnumSpecies.class);
    private static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static List<String> getDesc(Pokemon pokemon) {
        List<String> lore = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("###.##");
        String evPercentage = df.format(((pokemon.getStats().evs.hp + pokemon.getStats().evs.attack + pokemon.getStats().evs.defence + pokemon.getStats().evs.specialAttack + pokemon.getStats().evs.specialDefence + pokemon.getStats().evs.speed)*100)/510) + "%";
        if (pokemon.isEgg()) {
            lore.add(regex(Translation.StatLore.egglore));
            return lore;
        } else {
            lore.add(regex(Translation.StatLore.levellore) + pokemon.getLevel());
            lore.add(regex(Translation.StatLore.abilitylore) + pokemon.getAbility().getLocalizedName());
            noGender(pokemon, lore);
            lore.add(regex(Translation.StatLore.naturelore) + pokemon.getNature());
            lore.add(regex(Translation.StatLore.growthlore) + pokemon.getGrowth());
            lore.add(regex(Translation.StatLore.IVslore));
            lore.add(TextFormatting.AQUA + "" + pokemon.getStats().ivs.hp + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().ivs.attack + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().ivs.defence + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().ivs.specialAttack + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().ivs.specialDefence + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().ivs.speed + TextFormatting.GRAY + " [" + TextFormatting.AQUA + pokemon.getStats().ivs.getPercentage(0) + "%" + TextFormatting.GRAY + "]");
            lore.add(regex(Translation.StatLore.EVslore));
            lore.add(TextFormatting.AQUA + "" + pokemon.getStats().evs.hp + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().evs.attack + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().evs.defence + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().evs.specialAttack + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().evs.specialDefence + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().evs.speed + TextFormatting.GRAY + " [" + TextFormatting.AQUA + evPercentage + TextFormatting.GRAY + "]");
            return lore;
        }
    }

    private static void noGender(Pokemon pokemon, List<String> lore) {
        if(pokemon.getGender() != Gender.None) {
            lore.add(Translation.StatLore.genderlore + pokemon.getGender());
        }
    }

    public static void getGSONContent(File json) {
        JsonParser parser = new JsonParser();
        try (FileReader fileReader = new FileReader(json)) {
            JsonObject jsonObject = parser.parse(fileReader).getAsJsonObject();
            for(Map.Entry<String, JsonElement> elem : jsonObject.entrySet()) {
                JsonObject data = elem.getValue().getAsJsonObject();
                JsonArray names = data.get("names").getAsJsonArray();
                data.remove("names"); // might not be necessary

                SellData selldata = gson.fromJson(data, SellData.class);
                for (JsonElement s : names) {
                    EnumSpecies species = EnumSpecies.getFromNameAnyCase(s.getAsString());
                    if (species != null)
                        prices.put(species, selldata);
                }
            }
        } catch (IOException | IllegalStateException exception) {
            exception.printStackTrace();
        }
    }

    public static void writeGSONContent(File json) throws IOException {
        FileWriter fileWriter = new FileWriter(json);
        JsonObject jsonObject = new JsonObject();
        JsonObject number1 = new JsonObject();
        JsonObject number2 = new JsonObject();

        JsonArray json1 = new JsonArray();
        for(String s : EnumSpecies.legendaries) {
            json1.add(s);
        }
        jsonObject.add("1", number1);
        number1.add("names", json1);
        number1.addProperty("base", Prices.Base * 2);
        number1.addProperty("egg", Prices.Egg * 2);
        number1.addProperty("shiny", Prices.Shiny * 2);
        number1.addProperty("customtexture", Prices.CustomTexture * 2);
        number1.addProperty("legendary", Prices.Legendary * 2);
        number1.addProperty("maxIV", Prices.MaxIV * 2);
        number1.addProperty("maxEV", Prices.MaxEV * 2);
        number1.addProperty("level", Prices.Level * 2);
        number1.addProperty("maxlevel", Prices.MaxLevel * 2);
        number1.addProperty("HA", Prices.HA * 2);


        JsonArray json2 = new JsonArray();
        for(String s : EnumSpecies.ultrabeasts) {
            json2.add(s);
        }
        jsonObject.add("2", number2);
        number2.add("names", json2);
        number2.addProperty("base", (int) (Prices.Base * 1.5));
        number2.addProperty("egg", (int)(Prices.Egg * 1.5));
        number2.addProperty("shiny", (int)(Prices.Shiny * 1.5));
        number2.addProperty("customtexture", (int)(Prices.CustomTexture * 1.5));
        number2.addProperty("ub", (int)(Prices.UltraBeast * 1.5));
        number2.addProperty("maxIV", (int)(Prices.MaxIV * 1.5));
        number2.addProperty("maxEV", (int)(Prices.MaxEV * 1.5));
        number2.addProperty("level", (int)(Prices.Level * 1.5));
        number2.addProperty("maxlevel", (int)(Prices.MaxLevel * 1.5));
        number2.addProperty("HA", (int)(Prices.HA * 1.5));
        fileWriter.write(gson.toJson(jsonObject));
        fileWriter.flush();
    }

    private static List<String> getPriceAsLore(Pokemon pokemon) {
        SellData sd = prices.get(pokemon.getSpecies());
        if (sd == null) {
            sd = new SellData();
        }
        List<String> lore = new ArrayList<>();
        if (pokemon.isEgg()) {
            if (sd.egg > 0) {
                lore.add(regex(Translation.PriceLore.egg + sd.egg));
            }
            return lore;
        }
        lore.add(regex(Translation.PriceLore.base + sd.base));

        if (sd.shiny > 0 && pokemon.isShiny())
            lore.add(regex(Translation.PriceLore.shiny + sd.shiny));

        if (sd.customtexture > 0 && !pokemon.getCustomTexture().isEmpty())
            lore.add(regex(Translation.PriceLore.customtexture + sd.customtexture));

        if (sd.legendary > 0 && pokemon.isLegendary())
            lore.add(regex(Translation.PriceLore.legendary + sd.legendary));

        if (sd.ub > 0 && EnumSpecies.ultrabeasts.contains(pokemon.getSpecies().getPokemonName()))
            lore.add(regex(Translation.PriceLore.ultrabeast + sd.ub));

        if (sd.maxIV > 0) {
            int max = 0;
            for (int x : pokemon.getIVs().getArray())
                if (x >= IVStore.MAX_IVS)
                    max++;
            if (max > 0)
                lore.add(regex(Translation.PriceLore.maxIVEVnumbercolor + max + " " + Translation.PriceLore.maxIVs + (sd.maxIV * max)));
        }
        if (sd.maxEV > 0) {
            int max = 0;
            for (int x : pokemon.getEVs().getArray())
                if (x >= EVStore.MAX_EVS)
                    max++;
            if (max > 0)
                lore.add(regex(Translation.PriceLore.maxIVEVnumbercolor + max + " " + Translation.PriceLore.maxEVs + (sd.maxEV * max)));
        }
        if (pokemon.getLevel() == PixelmonConfig.maxLevel) {
            if (sd.maxlevel > 0) {
                lore.add(regex(Translation.PriceLore.maxLevel + sd.maxlevel));
            }
        } else {
            if (sd.maxlevel > 0) {
                lore.add(regex(Translation.PriceLore.level + pokemon.getLevel() + Translation.PriceLore.levelpricecolor + (sd.level * pokemon.getLevel())));
            }
        }
        if (sd.HA > 0 && pokemon.getAbilitySlot() == 2) {
            lore.add(regex(Translation.PriceLore.HA + sd.HA));
        }
        lore.add(regex(Translation.PriceLore.totalprice + getPrice(pokemon)));
        return lore;
    }

    static int getPrice(Pokemon pokemon) {
        SellData sd = prices.get(pokemon.getSpecies());
        if (sd == null) {
            sd = new SellData();
        }

        if (pokemon.isEgg()) {
            return sd.egg;
        }
        int price = sd.base;

        if (pokemon.isShiny())
            price += sd.shiny;
        if (!pokemon.getCustomTexture().isEmpty())
            price += sd.customtexture;
        if (pokemon.isLegendary())
            price += sd.legendary;
        if (EnumSpecies.ultrabeasts.contains(pokemon.getSpecies().getPokemonName()))
            price += sd.ub;
        if (sd.maxIV > 0) {
            int max = 0;
            for (int x : pokemon.getIVs().getArray())
                if (x >= IVStore.MAX_IVS)
                    max++;
            if (max > 0) {
                price += sd.maxIV * max;
            }
        }
        if (sd.maxEV > 0) {
            int max = 0;
            for (int x : pokemon.getEVs().getArray())
                if (x >= EVStore.MAX_EVS)
                    max++;
            if (max > 0) {
                price += sd.maxEV * max;
            }
        }
        if (pokemon.getLevel() == PixelmonConfig.maxLevel) {
            price += sd.maxlevel;
        } else {
            price += sd.level * pokemon.getLevel();
        }
        if (pokemon.getAbilitySlot() == 2) {
            price += sd.HA;
        }
        return price;
    }

    static List<Button> getBulkList(EntityPlayerMP player){
        ItemStack itemStackPhoto;
        List<Button> partyList = new ArrayList<>();
        for (Pokemon pokeStack : UIs.BulkList.get(player.getUniqueID())) {
            if (pokeStack.isEgg()) {
                itemStackPhoto = new ItemStack(PixelmonItems.itemPixelmonSprite);
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setString("SpriteName", egg(pokeStack));
                itemStackPhoto.setTagCompound(nbt);
                itemStackPhoto.setStackDisplayName(pokeStack.getDisplayName());
                List<String> lore;
                if (UIs.DescOrPrice.get(player.getUniqueID()) != null) {
                    lore = getDesc(pokeStack);
                } else {
                    lore = getPriceAsLore(pokeStack);
                }
                Button nullPokes = Button.builder()
                        .item(itemStackPhoto)
                        .displayName(regex(Translation.eggButton))
                        .onClick(action -> {
                            playerButton.put(player.getUniqueID(), action.getButton());
                            playerPokemon.put(player.getUniqueID(), pokeStack);
                            UIs.bulkUI(player).forceOpenPage(player);
                        })
                        .lore(lore)
                        .build();
                partyList.add(nullPokes);
            } else {
                itemStackPhoto = getPokemonPhoto(pokeStack);
                if (pokeStack.isShiny()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName() + regex(Translation.shinyInName));
                } else if (!pokeStack.getCustomTexture().isEmpty()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName() + TextFormatting.GRAY + " [" + TextFormatting.AQUA + pokeStack.getCustomTexture() + TextFormatting.GRAY + "]");
                } else if (!pokeStack.isShiny()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName());
                }
                List<String> lore;
                if (UIs.DescOrPrice.get(player.getUniqueID()) != null) {
                    lore = getDesc(pokeStack);
                } else {
                    lore = getPriceAsLore(pokeStack);
                }
                Button pokes = Button.builder()
                        .item(itemStackPhoto)
                        .onClick(action -> {
                            playerButton.put(player.getUniqueID(), action.getButton());
                            playerPokemon.put(player.getUniqueID(), pokeStack);
                            UIs.bulkUI(player).forceOpenPage(player);
                        })
                        .lore(lore)
                        .build();
                partyList.add(pokes);
            }
        }
        return partyList;
    }

    static List<Button> getPartyPokemonList(EntityPlayerMP player){
        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player);
        ItemStack itemStackPhoto;
        List<Button> partyList = new ArrayList<>();
        for (Pokemon pokeStack : party.getAll()) {
            if (pokeStack == null) {
                itemStackPhoto = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 7);
                Button nullPokes = Button.builder()
                        .item(itemStackPhoto)
                        .build();
                partyList.add(nullPokes);
                itemStackPhoto.setStackDisplayName(regex(Translation.emptyPartySlot));
            } else if (pokeStack.isEgg()) {
                itemStackPhoto = new ItemStack(PixelmonItems.itemPixelmonSprite);
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setString("SpriteName", egg(pokeStack));
                itemStackPhoto.setTagCompound(nbt);
                itemStackPhoto.setStackDisplayName(pokeStack.getDisplayName());
                List<String> lore;
                if (UIs.DescOrPrice.get(player.getUniqueID()) != null) {
                    lore = getDesc(pokeStack);
                } else {
                    lore = getPriceAsLore(pokeStack);
                }
                if (!Config.EggBool) {
                    Button nullPokes = Button.builder()
                            .item(itemStackPhoto)
                            .displayName(regex(Translation.eggButton))
                            .lore(lore)
                            .build();
                    partyList.add(nullPokes);
                } else {
                    Button nullPokes = Button.builder()
                            .item(itemStackPhoto)
                            .displayName(regex(Translation.eggButton))
                            .onClick((action) -> {
                                playerButton.put(player.getUniqueID(), action.getButton());
                                playerPokemon.put(player.getUniqueID(), pokeStack);
                                UIs.confirmationUI(player).forceOpenPage(player);
                            })
                            .lore(lore)
                            .build();
                    partyList.add(nullPokes);
                }
            } else {
                itemStackPhoto = getPokemonPhoto(pokeStack);
                if (pokeStack.isShiny()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName() + regex(Translation.shinyInName));
                } else if (!pokeStack.getCustomTexture().isEmpty()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName() + TextFormatting.GRAY + " [" + TextFormatting.AQUA + pokeStack.getCustomTexture() + TextFormatting.GRAY + "]");
                } else if (!pokeStack.isShiny()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName());
                }
                List<String> lore;
                if (UIs.DescOrPrice.get(player.getUniqueID()) != null) {
                    lore = getDesc(pokeStack);
                } else {
                    lore = getPriceAsLore(pokeStack);
                }
                Button pokes = Button.builder()
                        .item(itemStackPhoto)
                        .onClick((action) -> {
                            playerButton.put(action.getPlayer().getUniqueID(), action.getButton());
                            playerPokemon.put(action.getPlayer().getUniqueID(), pokeStack);
                            UIs.confirmationUI(player).forceOpenPage(player);
                        })
                        .lore(lore)
                        .build();
                partyList.add(pokes);
            }
        }
        return partyList;
    }

    static List<Button> getPCPokemonList(EntityPlayerMP player){
        PCStorage pc = Pixelmon.storageManager.getPCForPlayer(player);
        ItemStack itemStackPhoto;
        List<Button> pcList = new ArrayList<>();
        for (Pokemon pokeStack : pc.getAll()) {
            if (pokeStack == null) {
                itemStackPhoto = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 7);
                itemStackPhoto.setStackDisplayName(regex(Translation.emptyPCSlot));
                Button nullPokes = Button.builder()
                        .item(itemStackPhoto)
                        .build();
                pcList.add(nullPokes);
            } else if (pokeStack.isEgg()) {
                itemStackPhoto = new ItemStack(PixelmonItems.itemPixelmonSprite);
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setString("SpriteName", egg(pokeStack));
                itemStackPhoto.setTagCompound(nbt);
                itemStackPhoto.setStackDisplayName(pokeStack.getDisplayName());
                List<String> lore;
                if (UIs.DescOrPrice.get(player.getUniqueID()) != null) {
                    lore = getDesc(pokeStack);
                } else {
                    lore = getPriceAsLore(pokeStack);
                }
                if (!Config.EggBool) {
                    Button nullPokes = Button.builder()
                            .item(itemStackPhoto)
                            .displayName(regex(Translation.eggButton))
                            .lore(lore)
                            .build();
                    pcList.add(nullPokes);
                } else {
                    Button nullPokes = Button.builder()
                            .item(itemStackPhoto)
                            .displayName(regex(Translation.eggButton))
                            .onClick((action) -> {
                                playerButton.put(action.getPlayer().getUniqueID(), action.getButton());
                                playerPokemon.put(action.getPlayer().getUniqueID(), pokeStack);
                                if (UIs.bulkOrNot.get(player.getUniqueID()) != null) {
                                    UIs.bulkUI(player).forceOpenPage(player);
                                } else {
                                    UIs.confirmationUIPC(player).forceOpenPage(player);
                                }
                            })
                            .lore(lore)
                            .build();
                    pcList.add(nullPokes);
                }
            } else {
                itemStackPhoto = getPokemonPhoto(pokeStack);
                if (pokeStack.isShiny()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName() + regex(Translation.shinyInName));
                } else if (!pokeStack.getCustomTexture().isEmpty()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName() + TextFormatting.GRAY + " [" + TextFormatting.AQUA + pokeStack.getCustomTexture() + TextFormatting.GRAY + "]");
                } else if (!pokeStack.isShiny()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName());
                }
                List<String> lore;
                if (UIs.DescOrPrice.get(player.getUniqueID()) != null) {
                    lore = getDesc(pokeStack);
                } else {
                    lore = getPriceAsLore(pokeStack);
                }
                Button pokes = Button.builder()
                        .item(itemStackPhoto)
                        .onClick((action) -> {
                            playerButton.put(action.getPlayer().getUniqueID(), action.getButton());
                            playerPokemon.put(action.getPlayer().getUniqueID(), pokeStack);
                            if (UIs.bulkOrNot.get(player.getUniqueID()) != null) {
                                UIs.bulkUI(player).forceOpenPage(player);
                            } else {
                                UIs.confirmationUIPC(player).forceOpenPage(player);
                            }
                        })
                        .lore(lore)
                        .build();
                pcList.add(pokes);
            }
        }
        return pcList;
    }

    private static ItemStack getPokemonPhoto(Pokemon pokemon){
        ItemStack itemStack = new ItemStack(PixelmonItems.itemPixelmonSprite);
        NBTTagCompound tagCompound = new NBTTagCompound();
        itemStack.setTagCompound(tagCompound);
        tagCompound.setShort("ndex", (short) pokemon.getSpecies().getNationalPokedexInteger());
        tagCompound.setByte("form", (byte) pokemon.getForm());
        tagCompound.setByte("gender", pokemon.getGender().getForm());
        tagCompound.setBoolean("Shiny", pokemon.isShiny());
        return itemStack;
    }

    static void logger(EntityPlayerMP player, Pokemon pokemon) throws IOException {
        FileWriter writer = new FileWriter(PixelmonSTS.stsLog, true);
        if (PixelmonSTS.stsLog.length() == 0) {
            writer.write(dtf.format(now) + " " + player.getName() + " has traded a " + pokemon.getSpecies().getPokemonName());
        } else {
            writer.write("\n" + dtf.format(now) + " " + player.getName() + " has traded a " + pokemon.getSpecies().getPokemonName());
        }
        writer.close();
    }

    static void logger(EntityPlayerMP player) throws IOException {
        FileWriter writer = new FileWriter(PixelmonSTS.stsLog, true);
        if (PixelmonSTS.stsLog.length() == 0) {
            writer.write(dtf.format(now) + " " + player.getName() + " has traded in these pokemon: " + "(" + String.join(", ", UIs.BulkList.get(player.getUniqueID()).toString()) + ")");
        } else {
            writer.write("\n" + dtf.format(now) + " " + player.getName() + " has traded in these pokemon: " + "(" + String.join(", ", UIs.BulkList.get(player.getUniqueID()).toString()) + ")");
        }
        writer.close();
    }

    static long secondsleft(EntityPlayerMP player) {
        User user = PixelmonSTS.api.getUserManager().getUser(player.getUniqueID());
        CachedMetaData metaData = null;
        if (user != null) {
            metaData = user.getCachedData().getMetaData(PixelmonSTS.api.getContextManager().getQueryOptions(user).orElse(PixelmonSTS.api.getContextManager().getStaticQueryOptions()));

        }
        List<String> defaultValues = new ArrayList<>();
        defaultValues.add(String.valueOf(Config.CooldownTime));
        List<String> metaResult = null;
        if (metaData != null) {
            metaResult = metaData.getMeta().getOrDefault("sts.cooldown", defaultValues);
        }
        if (metaResult.contains(String.valueOf(Config.CooldownTime))) {
            return ((UIs.cooldownMap.get(player.getUniqueID()) / 1000) + Config.CooldownTime) - (System.currentTimeMillis() / 1000);
        } else {
            return ((UIs.cooldownMap.get(player.getUniqueID()) / 1000) + Integer.parseInt(metaResult.get(0)) - (System.currentTimeMillis() / 1000));
        }
    }

    static void enoughMoney(EntityPlayerMP player) {
        Optional<UniqueAccount> uOpt = PixelmonSTS.economyService.getOrCreateAccount(player.getUniqueID());
        if (uOpt.isPresent()) {
            UniqueAccount acc = uOpt.get();
            BigDecimal requiredamount = BigDecimal.valueOf(Config.CooldownPaymentPrice);
            TransactionResult result = acc.withdraw(PixelmonSTS.currency, requiredamount, Sponge.getCauseStackManager().getCurrentCause());
            if (result.getResult() == ResultType.SUCCESS) {
                UIs.cooldownMap.remove(player.getUniqueID());
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex(Translation.cooldownResetMessage)));
            } else if (result.getResult() == ResultType.FAILED || result.getResult() == ResultType.ACCOUNT_NO_FUNDS || result.getResult() == ResultType.ACCOUNT_NO_SPACE) {
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex(Translation.notEnoughMoneyMessage)));
            } else {
                System.out.println("Error, contact mod author");
            }
        } else {
            System.out.println("Account is not present!");
        }
    }

    private static String egg(Pokemon pokemon) {
        EnumSpecies species = pokemon.getSpecies();
        int cycles = pokemon.getEggCycles();
        return "pixelmon:sprites/eggs/"
                + (species == EnumSpecies.Togepi ? "togepi" : species == EnumSpecies.Manaphy ? "manaphy" : "egg")
                + (cycles > 10 ? "1" : cycles > 5 ? "2" : "3");
    }

    static void trade(EntityPlayerMP player, Pokemon pokemon) {
        Optional<UniqueAccount> uOpt = PixelmonSTS.economyService.getOrCreateAccount(player.getUniqueID());
        if (uOpt.isPresent()) {
            UniqueAccount acc = uOpt.get();
            BigDecimal requiredamount = BigDecimal.valueOf(getPrice(pokemon));
            TransactionResult result = acc.deposit(PixelmonSTS.currency, requiredamount, Sponge.getCauseStackManager().getCurrentCause());
            if (result.getResult() == ResultType.SUCCESS) {
                Pixelmon.storageManager.getParty(player).set(Pixelmon.storageManager.getParty(player).getSlot(pokemon), null);
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex(Translation.soldPokemonForMessage + getPrice(pokemon) + Translation.exclamationPointcolor)));
            } else {
                System.out.println("Error, contact mod author");
            }
        } else {
            System.out.println("Account is not present!");
        }
    }

    static void bulkTrade(EntityPlayerMP player) {
        Optional<UniqueAccount> uOpt = PixelmonSTS.economyService.getOrCreateAccount(player.getUniqueID());
        if (uOpt.isPresent()) {
            UniqueAccount acc = uOpt.get();
            BigDecimal requiredamount = BigDecimal.valueOf(0);
            for (Pokemon pokemon1 :  UIs.BulkList.get(player.getUniqueID())) {
                requiredamount = requiredamount.add(BigDecimal.valueOf(getPrice(pokemon1)));
            }
            TransactionResult result = acc.deposit(PixelmonSTS.currency, requiredamount, Sponge.getCauseStackManager().getCurrentCause());
            if (result.getResult() == ResultType.SUCCESS) {
                for (Pokemon pokemon : UIs.BulkList.get(player.getUniqueID())) {
                    Pixelmon.storageManager.getPCForPlayer(player).set(Pixelmon.storageManager.getPCForPlayer(player).getPosition(pokemon), null);
                }
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex(Translation.soldAllPokemonForMessage + requiredamount + Translation.exclamationPointcolor)));
            } else {
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex("&cError, contact mod author")));
            }
        } else {
            System.out.println("Account is not present!");
        }
    }

    static void tradePC(EntityPlayerMP player, Pokemon pokemon) {
        Optional<UniqueAccount> uOpt = PixelmonSTS.economyService.getOrCreateAccount(player.getUniqueID());
        if (uOpt.isPresent()) {
            UniqueAccount acc = uOpt.get();
            BigDecimal requiredamount = BigDecimal.valueOf(getPrice(pokemon));
            TransactionResult result = acc.deposit(PixelmonSTS.currency, requiredamount, Sponge.getCauseStackManager().getCurrentCause());
            if (result.getResult() == ResultType.SUCCESS) {
                Pixelmon.storageManager.getPCForPlayer(player).set(Pixelmon.storageManager.getPCForPlayer(player).getPosition(pokemon), null);
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex(Translation.soldPokemonForMessage + getPrice(pokemon) + Translation.exclamationPointcolor)));
            } else {
                System.out.println("Error, contact mod author");
            }
        } else {
            System.out.println("Account is not present!");
        }
    }

    static String regex(String line) {
        String regex = "&(?=[123456789abcdefklmnor])";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            line = line.replaceAll(regex, "§");
        }
        return line;
    }
}
