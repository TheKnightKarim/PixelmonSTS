package io.github.theknightkarim.utils;

import ca.landonjw.gooeylibs.inventory.api.Button;
import ca.landonjw.gooeylibs.inventory.api.InventoryAPI;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.theknightkarim.configs.Boosters;
import io.github.theknightkarim.configs.Config;
import io.github.theknightkarim.PixelmonSTS;
import io.github.theknightkarim.UIs;
import io.github.theknightkarim.configs.Prices;
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
    private static String regex = "&(?=[123456789abcdefklmnor])";
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static LocalDateTime now = LocalDateTime.now();

    public static List<String> getDesc(Pokemon pokemon) {
        List<String> lore = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("###.##");
        String evPercentage = df.format(((pokemon.getStats().evs.hp + pokemon.getStats().evs.attack + pokemon.getStats().evs.defence + pokemon.getStats().evs.specialAttack + pokemon.getStats().evs.specialDefence + pokemon.getStats().evs.speed)*100)/510) + "%";
        if (pokemon.isEgg()) {
            lore.add(TextFormatting.DARK_AQUA + "Pokemon: " + "???");
            return lore;
        } else {
            lore.add(TextFormatting.GRAY + "Level" + TextFormatting.WHITE + ": " + TextFormatting.AQUA + pokemon.getLevel());
            lore.add(TextFormatting.GRAY + "Ability" + TextFormatting.WHITE + ": " + TextFormatting.AQUA + pokemon.getAbility().getLocalizedName());
            noGender(pokemon, lore);
            lore.add(TextFormatting.GRAY + "Nature" + TextFormatting.WHITE + ": " + TextFormatting.AQUA + pokemon.getNature());
            lore.add(TextFormatting.GRAY + "Growth" + TextFormatting.WHITE + ": " + TextFormatting.AQUA + pokemon.getGrowth());
            lore.add(TextFormatting.GRAY + "IVs" + TextFormatting.WHITE + ": ");
            lore.add(TextFormatting.AQUA + "" + pokemon.getStats().ivs.hp + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().ivs.attack + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().ivs.defence + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().ivs.specialAttack + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().ivs.specialDefence + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().ivs.speed + TextFormatting.GRAY + " [" + TextFormatting.AQUA + pokemon.getStats().ivs.getPercentage(0) + "%" + TextFormatting.GRAY + "]");
            lore.add(TextFormatting.GRAY + "EVs" + TextFormatting.WHITE + ": ");
            lore.add(TextFormatting.AQUA + "" + pokemon.getStats().evs.hp + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().evs.attack + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().evs.defence + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().evs.specialAttack + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().evs.specialDefence + TextFormatting.GRAY + "/" + TextFormatting.AQUA + pokemon.getStats().evs.speed + TextFormatting.GRAY + " [" + TextFormatting.AQUA + evPercentage + TextFormatting.GRAY + "]");
            return lore;
        }
    }

    private static void noGender(Pokemon pokemon, List<String> lore) {
        if(!pokemon.getGender().name().equals("None")) {
            lore.add(TextFormatting.GRAY + "Gender" + TextFormatting.WHITE + ": " + TextFormatting.AQUA + pokemon.getGender());
        }
    }

    public static List<String> getPriceAsLore(Pokemon pokemon) {
        List<String> lore = new ArrayList<>();
        if (!pokemon.isEgg()) {
            if (Boosters.Shiny) {
                if (pokemon.isShiny()) {
                    lore.add(Utils.regex("&bShiny: &f" + Prices.Shiny));
                }
            }
            if (Boosters.CustomTexture) {
                if (!pokemon.getCustomTexture().isEmpty()) {
                    lore.add(Utils.regex("&bCustom Texture: &f" + Prices.CustomTexture));
                }
            }
            if (Boosters.Legendary) {
                if (pokemon.isLegendary()) {
                    lore.add(Utils.regex("&bLegendary: &f" + Prices.Legendary));
                }
            }
            if (Boosters.UltraBeast) {
                if (EnumSpecies.ultrabeasts.contains(pokemon.getSpecies().getPokemonName())) {
                    lore.add(Utils.regex("&bUltra Beast: &f" + Prices.UltraBeast));
                }
            }
            if (Boosters.MaxIV) {
                List<Integer> maxivs = new ArrayList<>();
                for (int x : pokemon.getIVs().getArray())
                    if (x == 31) {
                        maxivs.add(x);
                    }
                if (maxivs.size() != 0) {
                    lore.add(Utils.regex("&d" + maxivs.size() + " &bMax IV(s): &f" + (Prices.MaxIV * maxivs.size())));
                }
            }
            if (Boosters.MaxEV) {
                List<Integer> maxevs = new ArrayList<>();
                for (int x : pokemon.getEVs().getArray())
                    if (x == 252) {
                        maxevs.add(x);
                    }
                if (maxevs.size() != 0) {
                    lore.add(Utils.regex("&d" + maxevs.size() + " &bMax EV(s): &f" + (Prices.MaxIV * maxevs.size())));
                }
            }
            if (Boosters.Level) {
                if (pokemon.getLevel() == 100) {
                    lore.add(Utils.regex("&bMax Level: &f" + Prices.MaxLevel));
                } else {
                    lore.add(Utils.regex("&bLevel &d" + pokemon.getLevel() + "&b: &f" + (Prices.Level * pokemon.getLevel())));
                }
            }
            if (Boosters.HA) {
                if (pokemon.getAbilitySlot() == 2) {
                    lore.add(Utils.regex("&bHidden Ability: &f" + Prices.HA));
                }
            }
        }
        if (pokemon.isEgg() && Config.EggBool) {
            lore.add(Utils.regex("&bEgg: &f" + Prices.Egg));
        } else if (pokemon.isEgg() && !Config.EggBool) {
        } else {
            lore.add(Utils.regex("&bBase price: &f" + Prices.Base));
            lore.add(Utils.regex("&bTotal price: &f" + getPrice(pokemon)));
        }
        return lore;
    }

    public static int getPrice(Pokemon pokemon) {
        int price = 0;
        if (!pokemon.isEgg()) {
            if (Boosters.Shiny) {
                if (pokemon.isShiny()) {
                    price += Prices.Shiny;
                }
            }
            if (Boosters.CustomTexture) {
                if (!pokemon.getCustomTexture().isEmpty()) {
                    price += Prices.CustomTexture;
                }
            }
            if (Boosters.Legendary) {
                if (pokemon.isLegendary()) {
                    price += Prices.Legendary;
                }
            }
            if (Boosters.UltraBeast) {
                if (EnumSpecies.ultrabeasts.contains(pokemon.getSpecies().getPokemonName())) {
                    price += Prices.UltraBeast;
                }
            }
            if (Boosters.MaxIV) {
                List<Integer> maxivs = new ArrayList<>();
                for (int x : pokemon.getIVs().getArray())
                    if (x == 31) {
                        maxivs.add(x);
                    }
                if (maxivs.size() != 0) {
                    price += Prices.MaxIV * maxivs.size();
                }
            }
            if (Boosters.MaxEV) {
                List<Integer> maxevs = new ArrayList<>();
                for (int x : pokemon.getEVs().getArray())
                    if (x == 252) {
                        maxevs.add(x);
                    }
                if (maxevs.size() != 0) {
                    price += Prices.MaxEV * maxevs.size();
                }
            }
            if (Boosters.Level) {
                if (pokemon.getLevel() == 100) {
                    price += Prices.MaxLevel;
                } else {
                    price += Prices.Level * pokemon.getLevel();
                }
            }
            if (Boosters.HA) {
                if (pokemon.getAbilitySlot() == 2) {
                    price += Prices.HA;
                }
            }
        }
        if (pokemon.isEgg() && Config.EggBool) {
            price += Prices.Egg;
        } else if (pokemon.isEgg() && !Config.EggBool) {
        } else {
            price += Prices.Base;
        }
        return price;
    }

    public static List<Button> getBulkList(EntityPlayerMP player){
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
                        .displayName(Utils.regex("&b&lEgg"))
                        .lore(lore)
                        .build();
                partyList.add(nullPokes);
            } else {
                itemStackPhoto = getPokemonPhoto(pokeStack);
                if (pokeStack.isShiny()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName() + TextFormatting.GRAY + " [" + TextFormatting.GOLD + "Shiny" + TextFormatting.GRAY + "]");
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
                        .lore(lore)
                        .build();
                partyList.add(pokes);
            }
        }
        return partyList;
    }

    public static List<Button> getPartyPokemonList(EntityPlayerMP player){
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
                itemStackPhoto.setStackDisplayName(TextFormatting.RED + "Empty Party Slot");
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
                            .displayName(Utils.regex("&b&lEgg"))
                            .lore(lore)
                            .build();
                    partyList.add(nullPokes);
                } else {
                    Button nullPokes = Button.builder()
                            .item(itemStackPhoto)
                            .displayName(Utils.regex("&b&lEgg"))
                            .onClick((action) -> {
                                playerButton.put(player.getUniqueID(), action.getButton());
                                playerPokemon.put(player.getUniqueID(), pokeStack);
                                if (UIs.bulkOrNot.get(player.getUniqueID()) != null) {
                                    UIs.bulkUI(player).forceOpenPage(player);
                                } else {
                                    UIs.confirmationUI(player).forceOpenPage(player);
                                }
                            })
                            .lore(lore)
                            .build();
                    partyList.add(nullPokes);
                }
            } else {
                itemStackPhoto = getPokemonPhoto(pokeStack);
                if (pokeStack.isShiny()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName() + TextFormatting.GRAY + " [" + TextFormatting.GOLD + "Shiny" + TextFormatting.GRAY + "]");
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
                                UIs.confirmationUI(player).forceOpenPage(player);
                            }
                        })
                        .lore(lore)
                        .build();
                partyList.add(pokes);
            }
        }
        return partyList;
    }

    public static List<Button> getPCPokemonList(EntityPlayerMP player){
        PCStorage pc = Pixelmon.storageManager.getPCForPlayer(player);
        ItemStack itemStackPhoto;
        List<Button> pcList = new ArrayList<>();
        for (Pokemon pokeStack : pc.getAll()) {
            if (pokeStack == null) {
                itemStackPhoto = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 7);
                itemStackPhoto.setStackDisplayName(TextFormatting.RED + "Empty PC Slot");
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
                            .displayName(Utils.regex("&b&lEgg"))
                            .lore(lore)
                            .build();
                    pcList.add(nullPokes);
                } else {
                    Button nullPokes = Button.builder()
                            .item(itemStackPhoto)
                            .onClick((action) -> {
                                playerButton.put(action.getPlayer().getUniqueID(), action.getButton());
                                playerPokemon.put(action.getPlayer().getUniqueID(), pokeStack);
                                /*UIs.confirmationUIPC(player).forceOpenPage(player);*/
                            })
                            .lore(lore)
                            .build();
                    pcList.add(nullPokes);
                }
            } else {
                itemStackPhoto = getPokemonPhoto(pokeStack);
                if (pokeStack.isShiny()) {
                    itemStackPhoto.setStackDisplayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + pokeStack.getDisplayName() + TextFormatting.GRAY + " [" + TextFormatting.GOLD + "Shiny" + TextFormatting.GRAY + "]");
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
                            /*UIs.confirmationUIPC(player).forceOpenPage(player);*/
                        })
                        .lore(lore)
                        .build();
                pcList.add(pokes);
            }
        }
        return pcList;
    }

    public static ItemStack getPokemonPhoto(Pokemon pokemon){
        ItemStack itemStack = new ItemStack(PixelmonItems.itemPixelmonSprite);
        NBTTagCompound tagCompound = new NBTTagCompound();
        itemStack.setTagCompound(tagCompound);
        tagCompound.setShort("ndex", (short) pokemon.getSpecies().getNationalPokedexInteger());
        tagCompound.setByte("form", (byte) pokemon.getForm());
        tagCompound.setByte("gender", pokemon.getGender().getForm());
        tagCompound.setBoolean("Shiny", pokemon.isShiny());
        return itemStack;
    }

    /*public static void logger(EntityPlayerMP player, Pokemon pokemon, Button button) throws IOException {
        FileWriter writer = new FileWriter(PixelmonSTS.poolactions, true);
        String evPercentage = ((pokemon.getStats().evs.hp + pokemon.getStats().evs.attack + pokemon.getStats().evs.defence + pokemon.getStats().evs.specialAttack + pokemon.getStats().evs.specialDefence + pokemon.getStats().evs.speed) / 510 * 100) + "%";
        String shinyCT = "";
        if (pokemon.isShiny() && !pokemon.getCustomTexture().isEmpty()) {
            shinyCT += " (s/ct)";
        } else if (pokemon.isShiny()) {
            shinyCT += " (s)";
        } else if (!pokemon.getCustomTexture().isEmpty()) {
            shinyCT += " (ct)";
        }
        String buttonPurge = "";
        if (button.getDisplay().getItem().equals(Item.getByNameOrId(GUIConfig.RemoveFromPoolID))) {
            buttonPurge += " (Removed Pokemon)";
        } else if (button.getDisplay().getItem().equals(Item.getByNameOrId(GUIConfig.RemoveFromPoolAndRefundID))){
            buttonPurge += " (Removed & Added Pokemon)";
        }
        if (PixelmonSTS.poolactions.length() == 0) {
            writer.write(dtf.format(now) + " " + player.getName() + "<- Pool: " + pokemon.getSpecies().getPokemonName() + shinyCT + ", lvl:" + pokemon.getLevel() + ", ab:" + pokemon.getAbilityName() + ", g:" + pokemon.getGender() + ", n:" + pokemon.getNature() + ", gr:" + pokemon.getGrowth() + ", IVs:" + pokemon.getIVs().getPercentage(0) + "%, EVs:" + evPercentage + " " + buttonPurge);
        } else {
            writer.write("\n" + dtf.format(now) + " " + player.getName() + "<- Pool: " + pokemon.getSpecies().getPokemonName() + shinyCT + ", lvl:" + pokemon.getLevel() + ", ab:" + pokemon.getAbilityName() + ", g:" + pokemon.getGender() + ", n:" + pokemon.getNature() + ", gr:" + pokemon.getGrowth() + ", IVs:" + pokemon.getIVs().getPercentage(0) + "%, EVs:" + evPercentage + " " + buttonPurge);
        }
        writer.close();
    }*/

    public static long secondsleft(EntityPlayerMP player) {
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

    public static void enoughMoney(EntityPlayerMP player) {
        Optional<UniqueAccount> uOpt = PixelmonSTS.economyService.getOrCreateAccount(player.getUniqueID());
        if (uOpt.isPresent()) {
            UniqueAccount acc = uOpt.get();
            BigDecimal requiredamount = BigDecimal.valueOf(Config.CooldownPaymentPrice);
            TransactionResult result = acc.withdraw(PixelmonSTS.currency, requiredamount, Sponge.getCauseStackManager().getCurrentCause());
            if (result.getResult() == ResultType.SUCCESS) {
                UIs.cooldownMap.remove(player.getUniqueID());
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex("&bYou have reset your cooldown!")));
            } else if (result.getResult() == ResultType.FAILED || result.getResult() == ResultType.ACCOUNT_NO_FUNDS || result.getResult() == ResultType.ACCOUNT_NO_SPACE) {
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex("&cYou don't have enough money to reset your cooldown!")));
            } else {
                System.out.println("Error, contact mod author");
            }
        } else {
            System.out.println("Account is not present!");
        }
    }

    public static String egg(Pokemon pokemon) {
        EnumSpecies species = pokemon.getSpecies();
        int cycles = pokemon.getEggCycles();
        return "pixelmon:sprites/eggs/"
                + (species == EnumSpecies.Togepi ? "togepi" : species == EnumSpecies.Manaphy ? "manaphy" : "egg")
                + (cycles > 10 ? "1" : cycles > 5 ? "2" : "3");
    }

    public static void trade(EntityPlayerMP player, Pokemon pokemon) {
        Optional<UniqueAccount> uOpt = PixelmonSTS.economyService.getOrCreateAccount(player.getUniqueID());
        if (uOpt.isPresent()) {
            UniqueAccount acc = uOpt.get();
            BigDecimal requiredamount = BigDecimal.valueOf(getPrice(pokemon));
            TransactionResult result = acc.withdraw(PixelmonSTS.currency, requiredamount, Sponge.getCauseStackManager().getCurrentCause());
            if (result.getResult() == ResultType.SUCCESS) {
                Pixelmon.storageManager.getParty(player).set(Pixelmon.storageManager.getParty(player).getSlot(pokemon), null);
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex("&bYou have reset your cooldown!")));
            } else if (result.getResult() == ResultType.FAILED || result.getResult() == ResultType.ACCOUNT_NO_FUNDS || result.getResult() == ResultType.ACCOUNT_NO_SPACE) {
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex("&cYou don't have enough money to reset your cooldown!")));
            } else {
                System.out.println("Error, contact mod author");
            }
        } else {
            System.out.println("Account is not present!");
        }
    }

    public static void bulkTrade(EntityPlayerMP player) {
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
                    Pixelmon.storageManager.getParty(player).set(Pixelmon.storageManager.getParty(player).getSlot(pokemon), null);
                }
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex("&bYou sold all your pokemon for &6" + requiredamount + "&b!")));
            } else {
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex("&cError, contact mod author")));
            }
        } else {
            System.out.println("Account is not present!");
        }
    }

    public static void tradePC(EntityPlayerMP player, Pokemon pokemon) {
        Optional<UniqueAccount> uOpt = PixelmonSTS.economyService.getOrCreateAccount(player.getUniqueID());
        if (uOpt.isPresent()) {
            UniqueAccount acc = uOpt.get();
            BigDecimal requiredamount = BigDecimal.valueOf(getPrice(pokemon));
            TransactionResult result = acc.withdraw(PixelmonSTS.currency, requiredamount, Sponge.getCauseStackManager().getCurrentCause());
            if (result.getResult() == ResultType.SUCCESS) {
                Pixelmon.storageManager.getPCForPlayer(player).set(Pixelmon.storageManager.getPCForPlayer(player).getPosition(pokemon), null);
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex("&bYou have reset your cooldown!")));
            } else if (result.getResult() == ResultType.FAILED || result.getResult() == ResultType.ACCOUNT_NO_FUNDS || result.getResult() == ResultType.ACCOUNT_NO_SPACE) {
                InventoryAPI.getInstance().closePlayerInventory(player);
                player.sendMessage(new TextComponentString(regex("&cYou don't have enough money to reset your cooldown!")));
            } else {
                System.out.println("Error, contact mod author");
            }
        } else {
            System.out.println("Account is not present!");
        }
    }

    public static String regex(String line) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            line = line.replaceAll(regex, "§");
        }
        return line;
    }

    public static String regexColorPlayerPokemon(String line, EntityPlayerMP player, Pokemon pokemon) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            line = line.replaceAll(regex, "§");
        }
        if(line.contains("%player%")) {
            line = line.replace("%player%", player.getName());
        }
        if(line.contains("%pokemon%")) {
            line = line.replace("%pokemon%", pokemon.getSpecies().getPokemonName());
        }
        return line;
    }

    private static String regexColorPlayer(String line, EntityPlayerMP player) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            line = line.replaceAll(regex, "§");
        }
        if(line.contains("%player%")) {
            line = line.replace("%player%", player.getName());
        }
        return line;
    }
}
