package subside.plugins.mcbans.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.playerapi.PlayerAPI;

public class HistoryHandler {
	HashMap<String, ArrayList<HistoryPart>> list;
	String playername;

	public HistoryHandler(String playername) {
		this.playername = playername;
		list = new HashMap<String, ArrayList<HistoryPart>>();
	}
	
	public void addHistory(HistoryPart part) {
		if (list.containsKey(part.server)) {
			list.get(part.server).add(part);
		} else {
			list.put(part.server, new ArrayList<HistoryPart>());
			list.get(part.server).add(part);
		}
	}

	public void buildAndSendMessage(CommandSender player) {
		if(list.size() > 0){
			Iterator<Entry<String, ArrayList<HistoryPart>>> it = list.entrySet().iterator();
			player.sendMessage(" ");
			player.sendMessage(ChatColor.GRAY+"Info about: "+ChatColor.DARK_AQUA+playername);
			String ip;
			try {
				ip = PlayerAPI.getInstance().getProfile(playername).getIp();
				if(IPUtils.isIPBanned(ip)){
					player.sendMessage(ChatColor.GRAY+"========== IP Banned ==========");
					player.sendMessage(ChatColor.GRAY+"Banner: "+PlayerAPI.getInstance().getProfile(IPUtils.getBanner(ip)).getName());
					player.sendMessage(" ");
				}
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			while (it.hasNext()) {
				Map.Entry<String, ArrayList<HistoryPart>> pairs = (Map.Entry<String, ArrayList<HistoryPart>>) it.next();

				player.sendMessage(ChatColor.GRAY+"========== "+ChatColor.GREEN+"Server: "+pairs.getKey()+ChatColor.GRAY+" ==========");
				
				for(HistoryPart part : pairs.getValue()){
					player.sendMessage(part.genString());
				}
				player.sendMessage(" ");
				it.remove(); // avoids a ConcurrentModificationException
			}
		} else {
			Utils.sendMessage(player, "This player has a clean record!");
		}
	}
}
