package subside.plugins.mcbans;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import subside.plugins.mcbans.commandhandlers.CommandHandler;
import subside.plugins.mcbans.runnables.MuteCheckRunnable;
import subside.plugins.mcbans.utils.Mute;
import subside.plugins.mcbans.utils.Utils;

public class mcBans extends JavaPlugin {
	private static @Getter mcBans plugin;
	
	@Override
	public void onEnable(){
		plugin = this;
		new ConfigHandler(this);
		new CommandHandler().initialize(this);
		
		this.saveDefaultConfig();
		
		try {
	        new Utils(this);
		    Utils.mySQL.createDatabases();
		} catch(Exception e){
		    this.setEnabled(false);
		    this.getLogger().severe("Could not connect to the MySQL database!");
		    e.printStackTrace();
		}
		
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		
		loadSettings();
	}
	
	@Override
	public void onDisable(){
		
	}
	
	private void loadSettings(){
		for(Player player : Bukkit.getOnlinePlayers()){
			Mute.setProcessing(player);
			
			Bukkit.getScheduler().runTaskAsynchronously(this, new MuteCheckRunnable(player));
		}
	}
}
