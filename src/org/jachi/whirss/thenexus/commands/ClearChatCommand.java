package org.jachi.whirss.thenexus.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import org.jachi.whirss.thenexus.Main;
import org.jachi.whirss.thenexus.MessageUtil;

public class ClearChatCommand implements CommandExecutor {

    private Main main;

    public ClearChatCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if((sender instanceof Player)) {
            if(args.length > 0){
                if(sender.hasPermission("thenexus.*") ||
                        sender.hasPermission("thenexus.clearchat.*") ||
                        sender.hasPermission("thenexus.clearchat.others")){
                    if(args.length == 1){
                        Player target = Bukkit.getPlayer(args[0]);
                        if(target != null){
                            for(int i=0;i<200;i++) {
                                target.sendMessage("");
                            }
                            target.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.success.chat_cleared"), target));
                            sender.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.success.chat_cleared_others").replace("%player%", target.getName()), target));
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getLanguages().getString("console.error.player_offline")));
                        }
                    }
                } else {
                    sender.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.error.no_perms"), ((Player) sender)));
                }
            }else{
                if(sender.hasPermission("thenexus.*") ||
                        sender.hasPermission("thenexus.clearchat.*") ||
                        sender.hasPermission("thenexus.clearchat") ||
                        sender.hasPermission("thenexus.clearchat.others")){
                    for(int i=0;i<200;i++) {
                        Bukkit.broadcastMessage("");
                    }
                    sender.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.success.chat_cleared"), ((Player) sender)));
                } else {
                    sender.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.error.no_perms"), ((Player) sender)));
                }
            }
        } else {
            if(args.length == 1){
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null){
                    for(int i=0;i<200;i++) {
                        target.sendMessage("");
                    }
                    target.sendMessage(MessageUtil.getColorMessage(main.getLanguages().getString("messages.success.chat_cleared"), target));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getLanguages().getString("console.success.chat_cleared_others")).replace("%player%", target.getName()));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getLanguages().getString("console.error.player_offline")));
                }
            } else {
                for(int i=0;i<200;i++) {
                    Bukkit.broadcastMessage("");
                }
            }
        }
        return true;
    }
}