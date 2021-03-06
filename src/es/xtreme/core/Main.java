package es.xtreme.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import es.xtreme.core.admins.LastLocationAdmin;
import es.xtreme.core.admins.ScoreboardAdmin;
import es.xtreme.core.admins.TablistAdmin;
import es.xtreme.core.commands.AdventureCommand;
import es.xtreme.core.commands.ClearChatCommand;
import es.xtreme.core.commands.CreativeCommand;
import es.xtreme.core.commands.DayCommand;
import es.xtreme.core.commands.DeleteWarpCommand;
import es.xtreme.core.commands.EnderChestCommand;
import es.xtreme.core.commands.FeedCommand;
import es.xtreme.core.commands.FlyCommand;
import es.xtreme.core.commands.GMCommand;
import es.xtreme.core.commands.GodCommand;
import es.xtreme.core.commands.HealCommand;
import es.xtreme.core.commands.MidnightCommand;
import es.xtreme.core.commands.CoreCommand;
import es.xtreme.core.commands.NightCommand;
import es.xtreme.core.commands.NoonCommand;
import es.xtreme.core.commands.SetSpawnCommand;
import es.xtreme.core.commands.SetWarpCommand;
import es.xtreme.core.commands.SpawnCommand;
import es.xtreme.core.commands.SpectatorCommand;
import es.xtreme.core.commands.SunCommand;
import es.xtreme.core.commands.SurvivalCommand;
import es.xtreme.core.commands.UpdateWarpCommand;
import es.xtreme.core.commands.WarpCommand;
import es.xtreme.core.commands.WorkbenchCommand;
import es.xtreme.core.commands.WorldManagerCommand;
import es.xtreme.core.events.OnBreakBlock;
import es.xtreme.core.events.OnEntityDamage;
import es.xtreme.core.events.OnEntitySpawn;
import es.xtreme.core.events.OnPlaceBlock;
import es.xtreme.core.events.OnPlayerChatAsync;
import es.xtreme.core.events.OnPlayerCommand;
import es.xtreme.core.events.OnPlayerDeath;
import es.xtreme.core.events.OnPlayerDropItem;
import es.xtreme.core.events.OnPlayerInteract;
import es.xtreme.core.events.OnPlayerJoin;
import es.xtreme.core.events.OnPlayerQuit;
import es.xtreme.core.metrics.Metrics;

public final class Main extends JavaPlugin {

	private FileConfiguration commands = null;
	private File commandsFile = null;
	private FileConfiguration kits = null;
	private File kitsFile = null;
	private FileConfiguration warps = null;
	private File warpsFile = null;
	private FileConfiguration languages = null;
	private File languagesFile = null;
	private FileConfiguration userdata = null;
	private File userdataFile = null;
	private FileConfiguration worlds = null;
	private File worldsFile = null;

	//languages
	private FileConfiguration es = null;
	private File esFile = null;

	//placeholderapi
	public boolean placeholderapi = false;

	//instance
	private static Main instance;
	public static Main getInstance() { return instance; }

	//version
	public String version = "1.0.0";

	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "__  ___                            ____");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "\\ \\/ | |_ _ __ ___ _ __ ___   ___ / ___|___  _ __ ___");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " \\  /| __| '__/ _ | '_ ` _ \\ / _ | |   / _ \\| '__/ _ \\");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " /  \\| |_| | |  __| | | | | |  __| |__| (_) | | |  __/");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "/_/\\_\\\\__|_|  \\___|_| |_| |_|\\___|\\____\\___/|_|  \\___|");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "");

		instance = this;

		RegisterEvents();
		RegisterPluginCommands();

		registerCommands();
		registerConfig();
		registerKits();
		registerWarps();
		registerWorlds();
		if(getConfig().getString("language").equals("en") || getConfig().getString("language").equals("es")) {
			registerLanguages();
			registerEs();
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] " + getLanguages().getString("console.success.loaded_language"));
		} else {
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] Error getting the language set in config.yml");
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] Disabling the plugin...");
			getServer().getPluginManager().disablePlugin(this);
		}

		if(getConfig().getBoolean("metrics")) {
			int pluginId = 12700;
			new Metrics(this, pluginId);
		}

		ScoreboardAdmin scoreboard = new ScoreboardAdmin(this);
		scoreboard.createScoreboard();

		TablistAdmin tablist = new TablistAdmin(this);
		tablist.createTablist();

		LastLocationAdmin lastlocation = new LastLocationAdmin(this);
		lastlocation.createLastLocation();

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			placeholderapi = true;
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run(){
				if(!Bukkit.getVersion().contains("1.17")) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[XtremeCore] The version of the plugin you have installed is not compatible with your server. Download the correct version here: https://survcraft.org/XtremeCore");
					Bukkit.shutdown();
				}
				LoadWorlds();
			}
		});
	}

	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "__  ___                            ____");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "\\ \\/ | |_ _ __ ___ _ __ ___   ___ / ___|___  _ __ ___");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " \\  /| __| '__/ _ | '_ ` _ \\ / _ | |   / _ \\| '__/ _ \\");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " /  \\| |_| | |  __| | | | | |  __| |__| (_) | | |  __/");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "/_/\\_\\\\__|_|  \\___|_| |_| |_|\\___|\\____\\___/|_|  \\___|");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "");
	}

	public void RegisterEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new OnBreakBlock(this), this);
		pm.registerEvents(new OnEntityDamage(this), this);
		pm.registerEvents(new OnEntitySpawn(this), this);
		pm.registerEvents(new OnPlaceBlock(this), this);
		pm.registerEvents(new OnPlayerChatAsync(this), this);
		pm.registerEvents(new OnPlayerCommand(this), this);
		pm.registerEvents(new OnPlayerDeath(this), this);
		pm.registerEvents(new OnPlayerDropItem(this), this);
		pm.registerEvents(new OnPlayerInteract(this), this);
		pm.registerEvents(new OnPlayerJoin(this), this);
		pm.registerEvents(new OnPlayerQuit(this), this);

	}

	public void RegisterPluginCommands() {
		this.getCommand("adventure").setExecutor(new AdventureCommand(this));
		this.getCommand("clearchat").setExecutor(new ClearChatCommand(this));
		this.getCommand("creative").setExecutor(new CreativeCommand(this));
		this.getCommand("day").setExecutor(new DayCommand(this));
		this.getCommand("deletewarp").setExecutor(new DeleteWarpCommand(this));
		this.getCommand("enderchest").setExecutor(new EnderChestCommand(this));
		this.getCommand("feed").setExecutor(new FeedCommand(this));
		this.getCommand("fly").setExecutor(new FlyCommand(this));
		this.getCommand("gm").setExecutor(new GMCommand(this));
		this.getCommand("god").setExecutor(new GodCommand(this));
		this.getCommand("heal").setExecutor(new HealCommand(this));
		this.getCommand("midnight").setExecutor(new MidnightCommand(this));
		this.getCommand("core").setExecutor(new CoreCommand(this));
		this.getCommand("night").setExecutor(new NightCommand(this));
		this.getCommand("noon").setExecutor(new NoonCommand(this));
		this.getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
		this.getCommand("setwarp").setExecutor(new SetWarpCommand(this));
		this.getCommand("spawn").setExecutor(new SpawnCommand(this));
		this.getCommand("spectator").setExecutor(new SpectatorCommand(this));
		this.getCommand("sun").setExecutor(new SunCommand(this));
		this.getCommand("survival").setExecutor(new SurvivalCommand(this));
		this.getCommand("updatewarp").setExecutor(new UpdateWarpCommand(this));
		this.getCommand("warp").setExecutor(new WarpCommand(this));
		this.getCommand("workbench").setExecutor(new WorkbenchCommand(this));
		this.getCommand("worldmanager").setExecutor(new WorldManagerCommand(this));
	}

	public void LoadWorlds() {
		if(getWorlds().isSet("worlds")) {
			Bukkit.getConsoleSender().sendMessage("[XtremeCore/WorldManager] Loading worlds from \\plugins\\XtremeCore\\worlds.yml");
			for (String key : getWorlds().getConfigurationSection("worlds").getKeys(false)) {
				Bukkit.getConsoleSender().sendMessage("[XtremeCore/WorldManager] Loading world " + key.toString());
				WorldCreator setupworld = new WorldCreator(key);
				setupworld.createWorld();
			}
		} else {
			Bukkit.getConsoleSender().sendMessage("[XtremeCore/WorldManager] Importing worlds from the server to XtremeCore World Manager");
			for (World world : Bukkit.getWorlds()) {
				getWorlds().set("worlds." + world.getName() + ".name", world.getName());
				getWorlds().set("worlds." + world.getName() + ".alias", "&b" + world.getName().replace("_", " "));
				getWorlds().set("worlds." + world.getName() + ".build", true);
				getWorlds().set("worlds." + world.getName() + ".break", true);
				getWorlds().set("worlds." + world.getName() + ".pvp", true);
				getWorlds().set("worlds." + world.getName() + ".fall_damage", true);
				getWorlds().set("worlds." + world.getName() + ".interact", true);
				getWorlds().set("worlds." + world.getName() + ".drop_items", true);
				getWorlds().set("worlds." + world.getName() + ".mob_spawning", true);
				getWorlds().set("worlds." + world.getName() + ".difficulty", "NORMAL");
				getWorlds().set("worlds." + world.getName() + ".respawnWorld", "");
				getWorlds().set("worlds." + world.getName() + ".allowWeather", true);
				getWorlds().set("worlds." + world.getName() + ".seed", world.getSeed());
				getWorlds().set("worlds." + world.getName() + ".generator", "");
				getWorlds().set("worlds." + world.getName() + ".environment", world.getWorldType().toString());
				getWorlds().set("worlds." + world.getName() + ".type", "");
				getWorlds().set("worlds." + world.getName() + ".spawnlocation.x", 0.0);
				getWorlds().set("worlds." + world.getName() + ".spawnlocation.y", 65.0);
				getWorlds().set("worlds." + world.getName() + ".spawnlocation.z", 0.0);
				getWorlds().set("worlds." + world.getName() + ".spawnlocation.pitch", 0.0);
				getWorlds().set("worlds." + world.getName() + ".spawnlocation.yaw", 0.0);
				saveWorlds();
				Bukkit.getConsoleSender().sendMessage("[XtremeCore/WorldManager] Imported world: " + world.getName());
			}
		}
	}

	//config.yml
	public void registerConfig(){
		File config = new File(this.getDataFolder(),"config.yml");
		if(!config.exists()){
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] Creating new file: \\plugins\\XtremeCore\\config.yml");
			this.getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
	}

	//kits.yml:
	public FileConfiguration getKits() {
		if(kits == null) {
			reloadKits();
		}
		return kits;
	}

	public void reloadKits(){
		if(kits == null){
			kitsFile = new File(getDataFolder(), "kits.yml");
		}
		kits = YamlConfiguration.loadConfiguration(kitsFile);
		Reader defConfigStream;
		try{
			defConfigStream = new InputStreamReader(this.getResource("kits.yml"),"UTF8");
			if(defConfigStream != null){
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				kits.setDefaults(defConfig);
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public void saveKits(){
		try{
			kits.save(kitsFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void registerKits(){
		kitsFile = new File(this.getDataFolder(), "kits.yml");
		if(!kitsFile.exists()){
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] Creating new file: \\plugins\\XtremeCore\\kits.yml");
			this.getKits().options().copyDefaults(true);
			saveKits();
		}
	}


	//warps.yml:
	public FileConfiguration getWarps() {
		if(warps == null) {
			reloadWarps();
		}
		return warps;
	}

	public void reloadWarps(){
		if(warps == null){
			warpsFile = new File(getDataFolder(), "warps.yml");
		}
		warps = YamlConfiguration.loadConfiguration(warpsFile);
		Reader defConfigStream;
		try{
			defConfigStream = new InputStreamReader(this.getResource("warps.yml"),"UTF8");
			if(defConfigStream != null){
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				warps.setDefaults(defConfig);
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public void saveWarps(){
		try{
			warps.save(warpsFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void registerWarps(){
		warpsFile = new File(this.getDataFolder(), "warps.yml");
		if(!warpsFile.exists()){
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] Creating new file: \\plugins\\XtremeCore\\warps.yml");
			this.getWarps().options().copyDefaults(true);
			saveWarps();
		}
	}

	//userdata.yml:
	public FileConfiguration getUserdata(UUID uuid) {
		if(userdata == null) {
			reloadUserdata(uuid);
		}
		return userdata;
	}

	public void reloadUserdata(UUID uuid){
		if(userdata == null){
			userdataFile = new File(getDataFolder()+"/userdata",uuid+".yml");
		}
		userdata = YamlConfiguration.loadConfiguration(userdataFile);
		Reader defConfigStream;
		try{
			defConfigStream = new InputStreamReader(this.getResource("userdata.yml"),"UTF8");
			if(defConfigStream != null){
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				userdata.setDefaults(defConfig);
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public void saveUserdata(){
		try{
			userdata.save(userdataFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void registerUserdata(UUID uuid){
		userdataFile = new File(this.getDataFolder()+"/userdata",uuid+".yml");
		if(!userdataFile.exists()){
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] Creating new file: \\plugins\\XtremeCore\\userdata\\"+uuid+".yml");
			Bukkit.getConsoleSender().sendMessage("");
			this.getUserdata(uuid).options().copyDefaults(true);
			saveUserdata();
		}
	}

	//commands.yml:
	public FileConfiguration getCommands() {
		if(commands == null) {
			reloadCommands();
		}
		return commands;
	}

	public void reloadCommands(){
		if(commands == null){
			commandsFile = new File(getDataFolder(), "commands.yml");
		}
		commands = YamlConfiguration.loadConfiguration(commandsFile);
		Reader defConfigStream;
		try{
			defConfigStream = new InputStreamReader(this.getResource("commands.yml"),"UTF8");
			if(defConfigStream != null){
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				commands.setDefaults(defConfig);
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public void saveCommands(){
		try{
			commands.save(commandsFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void registerCommands(){
		commandsFile = new File(this.getDataFolder(), "commands.yml");
		if(!commandsFile.exists()){
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] Creating new file: \\plugins\\XtremeCore\\commands.yml");
			this.getCommands().options().copyDefaults(true);
			saveCommands();
		}
	}

	//worlds.yml:
	public FileConfiguration getWorlds() {
		if(worlds == null) {
			reloadWorlds();
		}
		return worlds;
	}

	public void reloadWorlds(){
		if(worlds == null){
			worldsFile = new File(getDataFolder(), "worlds.yml");
		}
		worlds = YamlConfiguration.loadConfiguration(worldsFile);
		Reader defConfigStream;
		try{
			defConfigStream = new InputStreamReader(this.getResource("worlds.yml"),"UTF8");
			if(defConfigStream != null){
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				worlds.setDefaults(defConfig);
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public void saveWorlds(){
		try{
			worlds.save(worldsFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void registerWorlds(){
		worldsFile = new File(this.getDataFolder(), "worlds.yml");
		if(!worldsFile.exists()){
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] Creating new file: \\plugins\\XtremeCore\\worlds.yml");
			this.getWorlds().options().copyDefaults(true);
			saveWorlds();
		}
	}

	//language_--.yml:
	public FileConfiguration getLanguages() {
		if(languages == null) {
			reloadLanguages();
		}
		return languages;
	}

	public void reloadLanguages(){
		if(languages == null){
			languagesFile = new File(getDataFolder(), "/language/" + getConfig().getString("language") + ".yml");
		}
		languages = YamlConfiguration.loadConfiguration(languagesFile);
		Reader defConfigStream;
		try{
			defConfigStream = new InputStreamReader(this.getResource("language/" + getConfig().getString("language") + ".yml"),"UTF8");
			if(defConfigStream != null){
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				languages.setDefaults(defConfig);
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public void saveLanguages(){
		try{
			languages.save(languagesFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void registerLanguages(){
		languagesFile = new File(this.getDataFolder(), "/language/" + getConfig().getString("language") + ".yml");
		if(!languagesFile.exists()){
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] Creating new file: \\plugins\\XtremeCore\\language\\" + getConfig().getString("language") + ".yml");
			this.getLanguages().options().copyDefaults(true);
			saveLanguages();
		}
	}

	//es_--.yml:
	public FileConfiguration getEs() {
		if(es == null) {
			reloadEs();
		}
		return es;
	}

	public void reloadEs(){
		if(es == null){
			esFile = new File(getDataFolder(), "/language/es.yml");
		}
		es = YamlConfiguration.loadConfiguration(esFile);
		Reader defConfigStream;
		try{
			defConfigStream = new InputStreamReader(this.getResource("es.yml"),"UTF8");
			if(defConfigStream != null){
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				es.setDefaults(defConfig);
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public void saveEs(){
		try{
			es.save(esFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void registerEs(){
		esFile = new File(this.getDataFolder(), "/language/es.yml");
		if(!esFile.exists()){
			Bukkit.getConsoleSender().sendMessage("[XtremeCore] Creating new file: \\plugins\\XtremeCore\\language\\es.yml");
			this.getEs().options().copyDefaults(true);
			saveEs();
		}
	}

}
