package subside.plugins.mcbans.runnables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.database.Event;
import subside.plugins.mcbans.playerapi.PlayerAPI;
import subside.plugins.mcbans.playerapi.Profile;
import subside.plugins.mcbans.utils.Mute;
import subside.plugins.mcbans.utils.Utils;

public class RemoveEventRunnable implements Runnable {
	private String playername;
	
	private CommandSender handler;
	private String reason;
	private Event event;

	public RemoveEventRunnable(String player, CommandSender handler, String reason, Event event) {
		playername = player;
		this.handler = handler;
		this.reason = reason;
		this.event = event;
	}

	@Override
	public void run() {
		try {

			Profile p = PlayerAPI.getInstance().getProfile(playername);
			if(p == null){
				Utils.sendMessage(handler, "The player \""+playername+"\" could not be found!");
				return;
			}
			event.remove(p.getUUID(), PlayerAPI.getInstance().getProfile(handler.getName()).getUUID(), reason);

			String what = (event == Event.BAN)?"unbanned":"unmuted";
			

			if(event == Event.MUTE){
				if(Bukkit.getPlayer(playername) != null){
					Bukkit.getPlayer(playername).sendMessage(ChatColor.GOLD+"You have been unmuted!");
					Mute.setProcessing(Bukkit.getPlayer(playername));
					Bukkit.getScheduler().runTaskAsynchronously(mcBans.getPlugin(), new MuteCheckRunnable(Bukkit.getPlayer(playername)));
				}
			}
			
			Utils.sendGlobalBanMessage(new String[]{ChatColor.AQUA+handler.getName()+ChatColor.GRAY+" has "+what+" "+ChatColor.AQUA+playername});
			Utils.sendMessage(handler, "You have "+what+" "+ChatColor.AQUA+playername+ChatColor.GRAY+"!");
		} catch (Exception e) {
			Utils.sendMessage(handler, e.getMessage());
		}

	}
}
