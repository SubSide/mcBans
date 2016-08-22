package subside.plugins.mcbans.runnables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.database.Event;
import subside.plugins.mcbans.playerapi.PlayerAPI;
import subside.plugins.mcbans.playerapi.Profile;
import subside.plugins.mcbans.utils.Mute;
import subside.plugins.mcbans.utils.Perm;
import subside.plugins.mcbans.utils.TimeConvert;
import subside.plugins.mcbans.utils.Utils;

public class AddEventRunnable implements Runnable {
	private String playername;
	private CommandSender handler;
	private String reason;
	private int date_expiring;
	Event event;

	public AddEventRunnable(String player, CommandSender handler, String time, String reason, Event event) throws Exception {
		playername = player;
		this.handler = handler;

		this.event = event;

		if (time != null) {
			date_expiring = Utils.getTimestamp() + TimeConvert.convert(time);
		} else {
			date_expiring = -1;
		}
		this.reason = reason;
	}

	@Override
	public void run() {
		try {
			Perm.canBe(playername, handler, event);
			Profile p = PlayerAPI.getInstance().getProfile(playername);
			if (p == null) {
				Utils.sendMessage(handler, "The player \"" + playername + "\" could not be found!");
				return;
			}
			event.add(p.getUUID(), PlayerAPI.getInstance().getProfile(handler.getName()).getUUID(), date_expiring, reason);
			if (event == Event.BAN) {
				if (Bukkit.getPlayer(playername) != null) {
					final String msg = Event.getBanMessage(PlayerAPI.getInstance().getProfile(playername).getUUID());
					Bukkit.getScheduler().runTask(mcBans.getPlugin(), new Runnable() {
						public void run() {
							Bukkit.getPlayer(playername).kickPlayer(msg);
						}
					});
				}
			} else {
				if (Bukkit.getPlayer(playername) != null) {
					Bukkit.getPlayer(playername).sendMessage(ChatColor.GOLD + "You have been muted!");
					Mute.setProcessing(Bukkit.getPlayer(playername));
					Bukkit.getScheduler().runTask(mcBans.getPlugin(), new MuteCheckRunnable(Bukkit.getPlayer(playername)));
				}

			}
			String what = (event == Event.BAN) ? "banned" : "muted";
			String expiring = (date_expiring != -1) ? Utils.formatTime(date_expiring) : "FOREVER";

			Utils.sendGlobalBanMessage(new String[] {
					ChatColor.AQUA + handler.getName() + ChatColor.GRAY + " has " + what + " " + ChatColor.AQUA + playername + ChatColor.GRAY + " till " + expiring,
					"Reason: " + ChatColor.GOLD + reason
			});
			Utils.sendMessage(handler, "You have " + what + " " + ChatColor.AQUA + playername + ChatColor.GRAY + "!");

		}
		catch (Exception e) {
			Utils.sendMessage(handler, e.getMessage());
		}

	}
}
