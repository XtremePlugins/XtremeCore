package es.xtreme.core.events;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;

import es.xtreme.core.Main;
import es.xtreme.core.utils.MessageUtil;

public class OnPlayerJoin implements Listener {

	private Main main;

	public OnPlayerJoin(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		main.registerUserdata(event.getPlayer().getUniqueId());

		if(main.getConfig().getBoolean("welcome.message.enabled")) {
			List<String> description = main.getConfig().getStringList("welcome.message.list");
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
				public void run() {
					for(int i=0;i<description.size();i++) {
						String message = description.get(i);
						event.getPlayer().sendMessage(MessageUtil.getColorMessage(message, event.getPlayer()));
					}
				}
			}, main.getConfig().getInt("welcome.message.wait"));
		}

		if(main.getConfig().getBoolean("welcome.title.enabled")) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
				public void run() {
					event.getPlayer().sendTitle(MessageUtil.getColorMessage(main.getConfig().getString("welcome.title.title"), event.getPlayer()), MessageUtil.getColorMessage(main.getConfig().getString("welcome.title.subtitle"), event.getPlayer()), main.getConfig().getInt("welcome.title.fade-in"), main.getConfig().getInt("welcome.title.stay"), main.getConfig().getInt("welcome.title.fade-out"));
				}
			}, main.getConfig().getInt("welcome.title.wait"));

		}

		if(main.getConfig().getBoolean("welcome.actionbar.enabled")) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
				public void run() {
					TextComponent text_component = new TextComponent(MessageUtil.getColorMessage(main.getConfig().getString("welcome.actionbar.message"), event.getPlayer()));
					event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, text_component);
				}
			}, main.getConfig().getInt("welcome.actionbar.wait"));
		}

		if(main.getConfig().getBoolean("welcome.broadcast.join.enabled")) {
			event.setJoinMessage(MessageUtil.getColorMessage(main.getConfig().getString("welcome.broadcast.join.message"), event.getPlayer()).replace("%player_name%", event.getPlayer().getDisplayName()));
		} else {
			event.setJoinMessage("");
		}

		if(main.getConfig().getBoolean("welcome.broadcast.first_join.enabled")) {
			if(!main.getUserdata(event.getPlayer().getUniqueId()).isSet("logoutlocation")) {
				Bukkit.broadcastMessage(MessageUtil.getColorMessage(main.getConfig().getString("welcome.broadcast.first_join.message"), event.getPlayer()));

			}
		}

		if(event.getPlayer().hasPermission("xtremecore.fly.safelogin")) {
			event.getPlayer().setAllowFlight(true);
			event.getPlayer().setFlying(true);
		}
	}

}