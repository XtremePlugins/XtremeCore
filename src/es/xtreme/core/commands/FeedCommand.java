package es.xtreme.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import es.xtreme.core.Main;
import es.xtreme.core.utils.MessageUtil;

public class FeedCommand implements CommandExecutor {

    private Main main;

    public FeedCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        //player:
        if((sender instanceof Player)) {
            if(args.length > 0){
                if(sender.hasPermission("xtremecore.*") ||
                        sender.hasPermission("xtremecore.feed.*") ||
                        sender.hasPermission("xtremecore.feed") ||
                        sender.hasPermission("xtremecore.feed.others")){
                    if(args.length == 1){
                        Player target = Bukkit.getPlayer(args[0]);
                        if(target != null){
                            target.setFoodLevel(20);
                            target.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.success.satisfied_appetite"), target));
                            sender.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.success.satisfied_appetite_others").replace("%player%", target.getName()), target));
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getLanguages().getString("console.error.player_offline")));
                        }
                    }
                } else {
                    sender.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.error.no_perms"), ((Player) sender)));
                }
            }else{
                if(sender.hasPermission("xtremecore.*") ||
                        sender.hasPermission("xtremecore.feed.*") ||
                        sender.hasPermission("xtremecore.feed")){
                    ((Player) sender).setFoodLevel(20);
                    sender.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.success.satisfied_appetite"), ((Player) sender)));
                } else {
                    sender.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.error.no_perms"), ((Player) sender)));
                }
            }
        } else {

            //console:
            if(args.length > 0){
                if(args.length == 1) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        target.setFoodLevel(20);
                        target.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.success.satisfied_appetite"), target));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getLanguages().getString("console.success.satisfied_appetite_others")).replace("%player%", target.getName()));
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getLanguages().getString("console.error.player_offline")));
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getLanguages().getString("console.other.use_feed_command")));
            }
        }
        return true;
    }
}