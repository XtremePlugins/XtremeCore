package org.jachi.whirss.thenexus.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jachi.whirss.thenexus.Main;
import org.jachi.whirss.thenexus.MessageUtil;

public class DeleteWarpCommand implements CommandExecutor {

    private Main main;

    public DeleteWarpCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        final Player player = (Player)sender;

        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run this command.");
            return true;
        }

        if(args.length > 0){
            if(sender.hasPermission("thenexus.*") ||
                    sender.hasPermission("thenexus.updatewarp")) {
                if(args.length == 1){
                    if(main.getWarps().isSet("warps."+ args[1])) {
                        main.getWarps().set("warps", null);
                        player.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.success.warp_deleted").replace("%warp%", args[1]), player));
                    } else {
                        player.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.error.unknown_warp"), player));
                    }
                } else {
                    player.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.other.use_deletewarp_command"), player));
                }
            }
        }
        return true;
    }
}
