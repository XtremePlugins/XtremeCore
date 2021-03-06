package es.xtreme.core.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import es.xtreme.core.Main;
import es.xtreme.core.utils.MessageUtil;

public class UpdateWarpCommand implements CommandExecutor {

    private Main main;

    public UpdateWarpCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run this command.");
            return true;
        }

        final Player player = (Player)sender;
        if(args.length > 0){
            if(sender.hasPermission("xtremecore.*") ||
                    sender.hasPermission("xtremecore.updatewarp")) {
                if(args.length == 1){
                    if(main.getWarps().isSet("warps."+ args[0])) {
                        Location l = player.getLocation();
                        String world = l.getWorld().getName();
                        double x = l.getX();
                        double y = l.getY();
                        double z = l.getZ();
                        float yaw = l.getYaw();
                        float pitch = l.getPitch();
                        main.getWarps().set("warps." + args[0] + ".world", world);
                        main.getWarps().set("warps." + args[0] + ".x", x);
                        main.getWarps().set("warps." + args[0] + ".y", y);
                        main.getWarps().set("warps." + args[0] + ".z", z);
                        main.getWarps().set("warps." + args[0] + ".yaw", yaw);
                        main.getWarps().set("warps." + args[0] + ".pitch", pitch);
                        main.saveWarps();
                        main.reloadWarps();
                        player.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.success.warp_updated").replace("%warp%", args[0]), player));
                    } else {
                        player.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.error.unknown_warp"), player));
                    }
                } else {
                    player.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.other.use_updatewarp_command"), player));
                }
            }
        }
        return true;
    }
}
