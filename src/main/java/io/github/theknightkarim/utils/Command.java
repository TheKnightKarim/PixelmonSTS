package io.github.theknightkarim.utils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class Command extends CommandBase {

    @Override
    public String getName() {
        return "sts";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/sts (player)";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws net.minecraft.command.CommandException {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            if (args.length == 1) {
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
                if (UIs.BulkList.get(player.getUniqueID()) != null) {
                    UIs.BulkList.get(player.getUniqueID()).clear();
                }
                UIs.menuUI(player).forceOpenPage(player);
            }
        }
    }
}

