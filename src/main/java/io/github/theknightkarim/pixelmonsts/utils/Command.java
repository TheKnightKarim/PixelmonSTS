package io.github.theknightkarim.pixelmonsts.utils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command extends CommandBase {

    @Override
    public String getName() {
        return "sts";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return Utils.regex(sender.canUseCommand(0, "sts.admin") ? "&c/sts (player)" : "&c/sts");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            if (args.length == 1) {
                if (player.canUseCommand(0, "sts.admin")) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        Utils.reloadAllConfigs();
                        player.sendMessage(new TextComponentString(Utils.regex("&bPixelmonSTS has been reloaded")));
                        return;
                    }
                    EntityPlayerMP player2 = server.getPlayerList().getPlayerByUsername(args[0]);
                    if (player2 != null) {
                        if (UIs.BulkList.get(player2.getUniqueID()) != null) {
                            UIs.BulkList.get(player2.getUniqueID()).clear();
                        }
                        UIs.menuUI(player2).openPage(player2);
                    } else {
                        player.sendMessage(new TextComponentString(Utils.regex("&cInvalid Argument")));
                    }
                } else {
                    player.sendMessage(new TextComponentString(Utils.regex("&cYou don't have permission to use this command!")));
                }
            } else {
                if (UIs.BulkList.get(player.getUniqueID()) != null) {
                    UIs.BulkList.get(player.getUniqueID()).clear();
                }
                UIs.menuUI(player).forceOpenPage(player);
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> possibleArgs = new ArrayList<>();
        if (sender instanceof EntityPlayerMP) {
            if (sender.canUseCommand(0, "sts.admin") && args.length == 1) {
                possibleArgs.addAll(Arrays.asList(server.getOnlinePlayerNames()));
            }
        }
        return getListOfStringsMatchingLastWord(args, possibleArgs);
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList("pokests", "psts"));
    }
}

