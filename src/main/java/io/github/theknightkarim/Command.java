package io.github.theknightkarim;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class Command extends CommandBase {

    private static int requiredlevel;

    @Override
    public String getName() {
        return "sts";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/sts: Base command for " + PixelmonSTS.MOD_NAME;
    }

    public int getRequiredPermissionLevel() {
        return requiredlevel;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(getRequiredPermissionLevel(), "sts.command.base");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            UIs.menuUI(player).forceOpenPage(player);
        }
    }

    /*@Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        List<String> possibleArgs = new ArrayList<>();
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            boolean isAdmin = player.canUseCommand(getRequiredPermissionLevel(), "sts.admin");

            if (args.length == 1) {
                if (isAdmin) {
                    possibleArgs.add(CommandConfig.WonderTradePool);
                    possibleArgs.add(CommandConfig.WonderTradeAdd);
                }
                possibleArgs.add(CommandConfig.WonderTradeMenu);
            }
            if (args.length == 2) {
                if (isAdmin && args[0].equalsIgnoreCase(CommandConfig.WonderTradeMenu)) {
                    possibleArgs.addAll(Arrays.asList(server.getOnlinePlayerNames()));
                }
            }
        }
        return getListOfStringsMatchingLastWord(args, possibleArgs);
    }*/
}
