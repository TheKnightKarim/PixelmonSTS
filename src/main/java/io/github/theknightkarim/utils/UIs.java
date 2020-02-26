package io.github.theknightkarim.utils;

import ca.landonjw.gooeylibs.inventory.api.*;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.config.PixelmonBlocks;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.config.PixelmonItemsHeld;
import io.github.theknightkarim.PixelmonSTS;
import io.github.theknightkarim.configs.Config;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UIs {

    public static HashMap<UUID, Boolean> bulkOrNot = new HashMap<>();
    public static HashMap<UUID, Long> cooldownMap = new HashMap<>();
    public static HashMap<UUID, Boolean> DescOrPrice = new HashMap<>();
    public static HashMap<UUID, List<Pokemon>> BulkList = new HashMap<>();
    public static List<Pokemon> pokemonList = new ArrayList<>();

    public static Page menuUI(EntityPlayerMP player) {
        Button pc = Button.builder()
                .item(new ItemStack(PixelmonBlocks.pc))
                .displayName(Utils.regex("&cPC Menu"))
                .onClick(action -> pcUI(player).forceOpenPage(player))
                .build();

        Template template = Template.builder(3)
                .border(0,0,3, 9, filler())
                .set(1,7, pc)
                .set(2,8, PokeLore(player))
                .build();

        Page page = Page.builder()
                .title(Utils.regex("&6&lSTS"))
                .template(template)
                .dynamicContents(Utils.getPartyPokemonList(player))
                .dynamicContentArea(1,1,1,6)
                .build();

        return page;
    }

    public static Page pcUI(EntityPlayerMP player) {

        Button bulklist;
        if (bulkOrNot.get(player.getUniqueID()) != null && BulkList.get(player.getUniqueID()) != null) {
            List<String> lore = new ArrayList<>();
            if (BulkList.get(player.getUniqueID()).size() > 0) {
                lore.add(Utils.regex("&cAmount: &f" + BulkList.get(player.getUniqueID()).size()));
            } else {
                lore.add(Utils.regex("&cYour Bulk List is empty! Add pokemon to view list."));
            }
            bulklist = Button.builder()
                    .item(new ItemStack(PixelmonItemsHeld.spellTag))
                    .displayName(Utils.regex("&e&lBulk List"))
                    .onClick(action -> {
                        if (BulkList.get(player.getUniqueID()).size() > 0) {
                            bulkList(player).forceOpenPage(player);
                        }
                    })
                    .lore(lore)
                    .build();
        } else {
            bulklist = filler();
        }

        Button back = Button.builder()
                .item(new ItemStack(PixelmonItems.LtradeHolderLeft))
                .displayName(Utils.regex("&cBack to main menu"))
                .onClick(action -> menuUI(player).forceOpenPage(player))
                .build();

        Button first = Button.builder()
                .item(new ItemStack(PixelmonItems.LtradeHolderLeft))
                .displayName(Utils.regex("&cFirst Page"))
                .onClick(action -> pcUI(player).getPage(1).get().forceOpenPage(player))
                .build();

        Button previous = Button.builder()
                .item(new ItemStack(PixelmonItems.LtradeHolderLeft))
                .displayName(Utils.regex("&cPrevious Page"))
                .type(ButtonType.PreviousPage)
                .build();

        Button next = Button.builder()
                .item(new ItemStack(PixelmonItems.tradeHolderRight))
                .displayName(Utils.regex("&cNext Page"))
                .type(ButtonType.NextPage)
                .build();

        Button last = Button.builder()
                .item(new ItemStack(PixelmonItems.tradeHolderRight))
                .displayName(Utils.regex("&cLast Page"))
                .onClick(action -> pcUI(player).getPage(pcUI(player).getTotalPages()).get().forceOpenPage(player))
                .build();

        Template template;
        if (Config.Bulk) {
            template = Template.builder(6)
                    .line(LineType.Vertical, 0, 0, 5, filler())
                    .line(LineType.Vertical, 0, 7, 5, filler())
                    .line(LineType.Horizontal, 5, 1, 7, filler())
                    .set(5, 0, back)
                    .set(0, 8, first)
                    .set(1,8, previous)
                    .set(2,8, bulkPC(player))
                    .set(3, 8, PokeLorePC(player))
                    .set(5, 4, bulklist)
                    .set(4, 8, next)
                    .set(5, 8, last)
                    .build();
        } else {
            template = Template.builder(5)
                    .line(LineType.Vertical, 0, 0, 5, filler())
                    .line(LineType.Vertical, 0, 7, 5, filler())
                    .line(LineType.Horizontal, 5, 1, 7, filler())
                    .set(5, 0, back)
                    .set(0, 8, first)
                    .set(1,8, previous)
                    .set(2, 8, PokeLorePC(player))
                    .set(3, 8, next)
                    .set(4, 8, last)
                    .build();
        }
        Page page = null;
        page = Page.builder()
                .title(Utils.regex("&e&lSTS &7&l[&6&l" + page.CURRENT_PAGE_PLACEHOLDER + "&7&l/&6&l" + page.TOTAL_PAGES_PLACEHOLDER + "&7]"))
                .template(template)
                .dynamicContents(Utils.getPCPokemonList(player))
                .dynamicContentArea(0,1,5,6)
                .build();
        return page;
    }

    public static Page confirmationUI(EntityPlayerMP player) {
        Button confirmOrCooldownButton;
        if (Config.Cooldown && Config.CooldownPayment) {
            if (cooldownMap.containsKey(player.getUniqueID())) {
                List<String> lore = new ArrayList<>();
                lore.add(Utils.regex("&7Click to bypass for &7&l" + Config.CooldownPaymentPrice + " " + PixelmonSTS.currency.getName()));
                if (Utils.secondsleft(player) > 0) {
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(PixelmonItems.hourglassGold))
                            .displayName(Utils.regex("&7Cooldown Duration&f: &b" + Utils.secondsleft(player) + " seconds"))
                            .onClick((action) -> {
                                if (Utils.secondsleft(player) > 0) {
                                    Utils.enoughMoney(player);
                                } else {
                                    confirmationUI(player).forceOpenPage(player);
                                }
                            })
                            .lore(lore)
                            .build();
                } else {
                    cooldownMap.remove(player.getUniqueID());
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(Items.DYE, 1 , 10))
                            .displayName(Utils.regex("&aConfirm"))
                            .onClick(action -> {
                                if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                    player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                                } else {
                                    cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                    Utils.trade(player, Utils.playerPokemon.get(player.getUniqueID()));
                                    try {
                                        Utils.logger(player, Utils.playerPokemon.get(player.getUniqueID()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                InventoryAPI.getInstance().closePlayerInventory(player);
                            })
                            .build();
                }
            } else {
                confirmOrCooldownButton = Button.builder()
                        .item(new ItemStack(Items.DYE, 1 , 10))
                        .displayName(Utils.regex("&aConfirm"))
                        .onClick(action -> {
                            if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                            } else {
                                cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                Utils.trade(player, Utils.playerPokemon.get(player.getUniqueID()));
                                try {
                                    Utils.logger(player, Utils.playerPokemon.get(player.getUniqueID()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            InventoryAPI.getInstance().closePlayerInventory(player);
                        })
                        .build();
            }
        } else if (Config.Cooldown) {
            if (cooldownMap.containsKey(player.getUniqueID())) {
                if (Utils.secondsleft(player) > 0) {
                    List<String> lore = new ArrayList<>();
                    lore.add("Duration&f: &b" + Utils.secondsleft(player) + " seconds");
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(PixelmonItems.hourglassGold))
                            .displayName(Utils.regex("&7Cooldown"))
                            .onClick((action) -> {
                                if (Utils.secondsleft(player) < 0) {
                                    confirmationUI(player).forceOpenPage(player);
                                }
                            })
                            .lore(lore)
                            .build();
                } else {
                    cooldownMap.remove(player.getUniqueID());
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(Items.DYE, 1 , 10))
                            .displayName(Utils.regex("&aConfirm"))
                            .onClick(action -> {
                                if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                    player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                                } else {
                                    cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                    Utils.trade(player, Utils.playerPokemon.get(player.getUniqueID()));
                                    try {
                                        Utils.logger(player, Utils.playerPokemon.get(player.getUniqueID()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                InventoryAPI.getInstance().closePlayerInventory(player);
                            })
                            .build();
                }
            } else {
                confirmOrCooldownButton = Button.builder()
                        .item(new ItemStack(Items.DYE, 1 , 10))
                        .displayName(Utils.regex("&aConfirm"))
                        .onClick(action -> {
                            if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                            } else {
                                cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                Utils.trade(player, Utils.playerPokemon.get(player.getUniqueID()));
                                try {
                                    Utils.logger(player, Utils.playerPokemon.get(player.getUniqueID()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            InventoryAPI.getInstance().closePlayerInventory(player);
                        })
                        .build();
            }
        } else {
            confirmOrCooldownButton = Button.builder()
                    .item(new ItemStack(Items.DYE, 1, 10))
                    .displayName(Utils.regex("&aConfirm"))
                    .onClick(action -> {
                        Utils.trade(player, Utils.playerPokemon.get(player.getUniqueID()));
                        try {
                            Utils.logger(player, Utils.playerPokemon.get(player.getUniqueID()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .build();
        }

        Button decline = Button.builder()
                .item(new ItemStack(Items.DYE, 1, 1))
                .displayName(Utils.regex("&cDecline"))
                .type(ButtonType.PreviousPage)
                .build();

        Template template = Template.builder(3)
                .set(1,2, confirmOrCooldownButton)
                .set(1,4, Utils.playerButton.get(player.getUniqueID()))
                .set(1, 6, decline)
                .border(0,0,3,9, filler())
                .build();

        return Page.builder()
                .title(Utils.regex("&b&lConfirmation"))
                .template(template)
                .previousPage(menuUI(player))
                .build();
    }

    public static Page confirmationUIPC(EntityPlayerMP player) {
        Button confirmOrCooldownButton;
        if (Config.Cooldown && Config.CooldownPayment) {
            if (cooldownMap.containsKey(player.getUniqueID())) {
                List<String> lore = new ArrayList<>();
                lore.add(Utils.regex("&7Click to bypass for &7&l" + Config.CooldownPaymentPrice + " " + PixelmonSTS.currency.getName()));
                if (Utils.secondsleft(player) > 0) {
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(PixelmonItems.hourglassGold))
                            .displayName(Utils.regex("&7Cooldown Duration&f: &b" + Utils.secondsleft(player) + " seconds"))
                            .onClick((action) -> {
                                if (Utils.secondsleft(player) > 0) {
                                    Utils.enoughMoney(player);
                                } else {
                                    confirmationUI(player).forceOpenPage(player);
                                }
                            })
                            .lore(lore)
                            .build();
                } else {
                    cooldownMap.remove(player.getUniqueID());
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(Items.DYE, 1 , 10))
                            .displayName(Utils.regex("&aConfirm"))
                            .onClick(action -> {
                                if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                    player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                                } else {
                                    cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                    Utils.tradePC(player, Utils.playerPokemon.get(player.getUniqueID()));
                                    try {
                                        Utils.logger(player, Utils.playerPokemon.get(player.getUniqueID()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                InventoryAPI.getInstance().closePlayerInventory(player);
                            })
                            .build();
                }
            } else {
                confirmOrCooldownButton = Button.builder()
                        .item(new ItemStack(Items.DYE, 1 , 10))
                        .displayName(Utils.regex("&aConfirm"))
                        .onClick(action -> {
                            if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                            } else {
                                cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                Utils.tradePC(player, Utils.playerPokemon.get(player.getUniqueID()));
                                try {
                                    Utils.logger(player, Utils.playerPokemon.get(player.getUniqueID()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            InventoryAPI.getInstance().closePlayerInventory(player);
                        })
                        .build();
            }
        } else if (Config.Cooldown) {
            if (cooldownMap.containsKey(player.getUniqueID())) {
                if (Utils.secondsleft(player) > 0) {
                    List<String> lore = new ArrayList<>();
                    lore.add("Duration&f: &b" + Utils.secondsleft(player) + " seconds");
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(PixelmonItems.hourglassGold))
                            .displayName(Utils.regex("&7Cooldown"))
                            .onClick((action) -> {
                                if (Utils.secondsleft(player) < 0) {
                                    confirmationUI(player).forceOpenPage(player);
                                }
                            })
                            .lore(lore)
                            .build();
                } else {
                    cooldownMap.remove(player.getUniqueID());
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(Items.DYE, 1 , 10))
                            .displayName(Utils.regex("&aConfirm"))
                            .onClick(action -> {
                                if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                    player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                                } else {
                                    cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                    Utils.tradePC(player, Utils.playerPokemon.get(player.getUniqueID()));
                                    try {
                                        Utils.logger(player, Utils.playerPokemon.get(player.getUniqueID()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                InventoryAPI.getInstance().closePlayerInventory(player);
                            })
                            .build();
                }
            } else {
                confirmOrCooldownButton = Button.builder()
                        .item(new ItemStack(Items.DYE, 1 , 10))
                        .displayName(Utils.regex("&aConfirm"))
                        .onClick(action -> {
                            if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                            } else {
                                cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                Utils.tradePC(player, Utils.playerPokemon.get(player.getUniqueID()));
                                try {
                                    Utils.logger(player, Utils.playerPokemon.get(player.getUniqueID()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            InventoryAPI.getInstance().closePlayerInventory(player);
                        })
                        .build();
            }
        } else {
            confirmOrCooldownButton = Button.builder()
                    .item(new ItemStack(Items.DYE, 1, 10))
                    .displayName(Utils.regex("&aConfirm"))
                    .onClick(action -> {
                        Utils.tradePC(player, Utils.playerPokemon.get(player.getUniqueID()));
                        try {
                            Utils.logger(player, Utils.playerPokemon.get(player.getUniqueID()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .build();
        }

        Button decline = Button.builder()
                .item(new ItemStack(Items.DYE, 1, 1))
                .displayName(Utils.regex("&cDecline"))
                .type(ButtonType.PreviousPage)
                .build();

        Template template = Template.builder(3)
                .set(1,2, confirmOrCooldownButton)
                .set(1,4, Utils.playerButton.get(player.getUniqueID()))
                .set(1, 6, decline)
                .border(0,0,3,9, filler())
                .build();

        return Page.builder()
                .title(Utils.regex("&b&lConfirmation"))
                .template(template)
                .previousPage(pcUI(player))
                .build();
    }

    public static Page bulkUI(EntityPlayerMP player) {
        Button addOrRemove;
        if (BulkList.get(player.getUniqueID()) != null) {
            if (BulkList.get(player.getUniqueID()).contains(Utils.playerPokemon.get(player.getUniqueID())) || BulkList.get(player.getUniqueID()).size() == Config.MaxPokemoninBulk) {
                List<String> lore = new ArrayList<>();
                if (BulkList.get(player.getUniqueID()).size() == Config.MaxPokemoninBulk) {
                    lore.add(Utils.regex("&eLimit reached! You can't add more pokemon."));
                }
                addOrRemove = Button.builder()
                        .item(new ItemStack(Items.DYE, 1 , 1))
                        .displayName(Utils.regex("&cRemove pokemon from Bulk List"))
                        .onClick(action -> {
                            BulkList.get(player.getUniqueID()).remove(Utils.playerPokemon.get(player.getUniqueID()));
                            menuUI(player).forceOpenPage(player);
                        })
                        .lore(lore)
                        .build();
            } else {
                addOrRemove = Button.builder()
                        .item(new ItemStack(Items.DYE, 1 , 10))
                        .displayName(Utils.regex("&aAdd pokemon to Bulk List"))
                        .onClick(action -> {
                            pokemonList.add(Utils.playerPokemon.get(player.getUniqueID()));
                            BulkList.put(player.getUniqueID(), pokemonList);
                            menuUI(player).forceOpenPage(player);
                        })
                        .build();
            }
        } else {
            addOrRemove = Button.builder()
                    .item(new ItemStack(Items.DYE, 1 , 10))
                    .displayName(Utils.regex("&aAdd pokemon to Bulk List"))
                    .onClick(action -> {
                        pokemonList.add(Utils.playerPokemon.get(player.getUniqueID()));
                        BulkList.put(player.getUniqueID(), pokemonList);
                        menuUI(player).forceOpenPage(player);
                    })
                    .build();
        }

        Button decline = Button.builder()
                .item(new ItemStack(Items.DYE, 1, 1))
                .displayName(Utils.regex("&cDecline"))
                .type(ButtonType.PreviousPage)
                .build();

        Template template = Template.builder(3)
                .set(1,2, addOrRemove)
                .set(1,4, Utils.playerButton.get(player.getUniqueID()))
                .set(1, 6, decline)
                .border(0,0,3,9, filler())
                .build();

        Page page = Page.builder()
                .title(Utils.regex("&b&lConfirmation"))
                .template(template)
                .previousPage(pcUI(player))
                .build();

        return page;
    }

    public static Page bulkList(EntityPlayerMP player) {
        Button decline = Button.builder()
                .item(new ItemStack(Items.DYE, 1, 1))
                .displayName(Utils.regex("&cDecline"))
                .type(ButtonType.PreviousPage)
                .build();

        Button confirmOrCooldownButton;
        if (Config.Cooldown && Config.CooldownPayment) {
            if (cooldownMap.containsKey(player.getUniqueID())) {
                List<String> lore = new ArrayList<>();
                lore.add(Utils.regex("&7Click to bypass for &7&l" + Config.CooldownPaymentPrice + " " + PixelmonSTS.currency.getName()));
                if (Utils.secondsleft(player) > 0) {
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(PixelmonItems.hourglassGold))
                            .displayName(Utils.regex("&7Cooldown Duration&f: &b" + Utils.secondsleft(player) + " seconds"))
                            .onClick((action) -> {
                                if (Utils.secondsleft(player) > 0) { //FIX
                                    Utils.enoughMoney(player);
                                } else {
                                    Utils.bulkTrade(player);
                                    try {
                                        Utils.logger(player, UIs.BulkList.get(player.getUniqueID()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .lore(lore)
                            .build();
                } else {
                    cooldownMap.remove(player.getUniqueID());
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(Items.DYE, 1 , 10))
                            .displayName(Utils.regex("&aConfirm"))
                            .onClick(action -> {
                                if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                    player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                                } else {
                                    cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                    Utils.bulkTrade(player);
                                    try {
                                        Utils.logger(player, UIs.BulkList.get(player.getUniqueID()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                InventoryAPI.getInstance().closePlayerInventory(player);
                            })
                            .build();
                }
            } else {
                confirmOrCooldownButton = Button.builder()
                        .item(new ItemStack(Items.DYE, 1 , 10))
                        .displayName(Utils.regex("&aConfirm"))
                        .onClick(action -> {
                            if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                            } else {
                                cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                Utils.bulkTrade(player);
                                try {
                                    Utils.logger(player, UIs.BulkList.get(player.getUniqueID()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            InventoryAPI.getInstance().closePlayerInventory(player);
                        })
                        .build();
            }
        } else if (Config.Cooldown) {
            if (cooldownMap.containsKey(player.getUniqueID())) {
                if (Utils.secondsleft(player) > 0) {
                    List<String> lore = new ArrayList<>();
                    lore.add("Duration&f: &b" + Utils.secondsleft(player) + " seconds");
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(PixelmonItems.hourglassGold))
                            .displayName(Utils.regex("&7Cooldown"))
                            .onClick((action) -> {
                                if (Utils.secondsleft(player) > 0) {
                                    Utils.enoughMoney(player);
                                } else {
                                    Utils.bulkTrade(player);
                                    try {
                                        Utils.logger(player, BulkList.get(player.getUniqueID()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .lore(lore)
                            .build();
                } else {
                    cooldownMap.remove(player.getUniqueID());
                    confirmOrCooldownButton = Button.builder()
                            .item(new ItemStack(Items.DYE, 1 , 10))
                            .displayName(Utils.regex("&aConfirm"))
                            .onClick(action -> {
                                if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                    player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                                } else {
                                    cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                    Utils.bulkTrade(player);
                                    try {
                                        Utils.logger(player, BulkList.get(player.getUniqueID()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                InventoryAPI.getInstance().closePlayerInventory(player);
                            })
                            .build();
                }
            } else {
                confirmOrCooldownButton = Button.builder()
                        .item(new ItemStack(Items.DYE, 1 , 10))
                        .displayName(Utils.regex("&aConfirm"))
                        .onClick(action -> {
                            if (Utils.playerPokemon.get(action.getPlayer().getUniqueID()).hasSpecFlag("untradeable")) {
                                player.sendMessage(new TextComponentString(Utils.regex("&cYour pokemon is untradeable!")));
                            } else {
                                cooldownMap.put(player.getUniqueID(), System.currentTimeMillis());
                                Utils.bulkTrade(player);
                                try {
                                    Utils.logger(player, BulkList.get(player.getUniqueID()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            InventoryAPI.getInstance().closePlayerInventory(player);
                        })
                        .build();
            }
        } else {
            confirmOrCooldownButton = Button.builder()
                    .item(new ItemStack(Items.DYE, 1, 10))
                    .displayName(Utils.regex("&aConfirm"))
                    .onClick(action -> {
                        Utils.bulkTrade(player);
                        try {
                            Utils.logger(player, BulkList.get(player.getUniqueID()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .build();
        }

        Button nextpage;
        Button previouspage;
        if (BulkList.get(player.getUniqueID()).size() > 28) {
            nextpage = Button.builder()
                    .item(new ItemStack(PixelmonItems.tradeHolderRight))
                    .displayName(Utils.regex("&cNext page"))
                    .build();
            previouspage = Button.builder()
                    .item(new ItemStack(PixelmonItems.LtradeHolderLeft))
                    .displayName(Utils.regex("&cPrevious page"))
                    .build();
        } else {
            nextpage = filler();
            previouspage = filler();
        }

        int fullprice = 0;
        List<String> lore = new ArrayList<>();
        lore.add(Utils.regex("&bAmount: &f" + BulkList.get(player.getUniqueID()).size()));
        for(Pokemon pokemon : BulkList.get(player.getUniqueID())) {
            fullprice += Utils.getPrice(pokemon);
        }
        lore.add(Utils.regex("&eTotal Price: &f" + fullprice));
        Button info = Button.builder()
                .item(new ItemStack(Items.BOOK))
                .displayName(Utils.regex("&eInfo:"))
                .lore(lore)
                .build();

        Template template = Template.builder(6)
                .border(0,0,6,9, filler())
                .set(5, 0, previouspage)
                .set(5,8, nextpage)
                .set(5,2,confirmOrCooldownButton)
                .set(5,4, info)
                .set(5,6,decline)
                .build();

        Page page = Page.builder()
                .title(Utils.regex("&b&lConfirmation"))
                .template(template)
                .previousPage(menuUI(player))
                .dynamicContents(Utils.getBulkList(player))
                .dynamicContentArea(1,1,4,7)
                .build();
        return page;
    }

    //Buttons

    private static Button filler() {
        return Button.builder()
                .item(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.CYAN.getMetadata()))
                .displayName("")
                .build();
    }

    private static Button bulk(EntityPlayerMP player) {
        boolean bulkOrNotbool = bulkOrNot.get(player.getUniqueID()) != null;
        String line;
        if (bulkOrNotbool) {
            line = "&bBulk: &aTrue";
        } else {
            line = "&bBulk: &cFalse";
        }

        return Button.builder()
                .item(new ItemStack(PixelmonBlocks.blackVendingMachineBlock))
                .displayName(Utils.regex(line))
                .onClick(action -> {
                    if (bulkOrNotbool) {
                        bulkOrNot.remove(player.getUniqueID()); // True - 1
                    } else {
                        bulkOrNot.remove(player.getUniqueID()); // False - 2
                        bulkOrNot.put(player.getUniqueID(), false);
                    }
                    menuUI(player).forceOpenPage(player);
                })
                .build();
    }

    private static Button bulkPC(EntityPlayerMP player) {
        boolean bulkOrNotbool = bulkOrNot.get(player.getUniqueID()) != null;
        String line;
        if (bulkOrNotbool) {
            line = "&bBulk: &aTrue";
        } else {
            line = "&bBulk: &cFalse";
        }

        return Button.builder()
                .item(new ItemStack(PixelmonBlocks.blackVendingMachineBlock))
                .displayName(Utils.regex(line))
                .onClick(action -> {
                    if (bulkOrNotbool) {
                        bulkOrNot.remove(player.getUniqueID()); // True - 1
                    } else {
                        bulkOrNot.remove(player.getUniqueID()); // False - 2
                        bulkOrNot.put(player.getUniqueID(), false);
                    }
                    pcUI(player).forceOpenPage(player);
                })
                .build();
    }

    public static Button PokeLore(EntityPlayerMP player) {
        boolean DescOrPriceBool = DescOrPrice.get(player.getUniqueID()) != null;
        String line;
        int meta;
        if (DescOrPriceBool) {
            line = Utils.regex("&6Stats");
            meta = 375;
        } else {
            line = Utils.regex("&6Price");
            meta= 0;
        }
        ItemStack itemStack = new ItemStack(PixelmonItems.unoOrb, 1, meta);
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setInteger("Unbreakable", 1);
        nbtTagCompound.setInteger("HideFlags", 2);
        itemStack.setTagCompound(nbtTagCompound);

        return Button.builder()
                .item(itemStack)
                .displayName(Utils.regex(line))
                .onClick(action -> {
                    if (DescOrPriceBool) {
                        DescOrPrice.remove(player.getUniqueID()); // True - 1
                    } else {
                        DescOrPrice.remove(player.getUniqueID()); // False - 2
                        DescOrPrice.put(player.getUniqueID(), false);
                    }
                    menuUI(player).forceOpenPage(player);
                })
                .build();
    }

    public static Button PokeLorePC(EntityPlayerMP player) {
        boolean DescOrPriceBool = DescOrPrice.get(player.getUniqueID()) != null;
        String line;
        int meta;
        if (DescOrPriceBool) {
            line = Utils.regex("&6Stats");
            meta = 375;
        } else {
            line = Utils.regex("&6Price");
            meta= 0;
        }
        ItemStack itemStack = new ItemStack(PixelmonItems.unoOrb, 1, meta);
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setInteger("Unbreakable", 1);
        nbtTagCompound.setInteger("HideFlags", 2);
        itemStack.setTagCompound(nbtTagCompound);

        return Button.builder()
                .item(itemStack)
                .displayName(Utils.regex(line))
                .onClick(action -> {
                    if (DescOrPriceBool) {
                        DescOrPrice.remove(player.getUniqueID()); // True - 1
                    } else {
                        DescOrPrice.remove(player.getUniqueID()); // False - 2
                        DescOrPrice.put(player.getUniqueID(), false);
                    }
                    pcUI(player).forceOpenPage(player);
                })
                .build();
    }
}
