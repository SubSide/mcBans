package subside.plugins.mcbans.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.mcbans.ConfigHandler;
import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.database.MySQL;

public class Utils {
	mcBans plugin;

	public static MySQL mySQL;

	public Utils(mcBans plugin) {
		try {
			this.plugin = plugin;
			mySQL = new MySQL(plugin, getConfigAttribute("hostname"), getConfigAttribute("port"), getConfigAttribute("database"), getConfigAttribute("username"), getConfigAttribute("password"));
			mySQL.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getConfigAttribute(String str) {
		return plugin.getConfig().getString(str);
	}

	public static String getFromArray(String[] args, int from) {
		String build = "";
		for (int x = from; x < args.length; x++) {
			build += args[x]+" ";
		}
		return build.trim();
	}
	
	public static void sendMessage(CommandSender sender, String e){
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigHandler.getInstance().getPrefix()+e));
	}
	
	public static void sendGlobalBanMessage(String[] msg){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(Perm.CANSEEBROADCASTS.has(player)){
				for(String ms : msg){
					player.sendMessage(ConfigHandler.getInstance().getPrefix()+ms);
				}
			}
		}
	}
	
	public static String formatTime(int time){
		return new SimpleDateFormat().format(new Date((long)time*1000));
	}
	
	
	public static int getTimestamp(){
		return (int)(System.currentTimeMillis()/1000);
	}
}
