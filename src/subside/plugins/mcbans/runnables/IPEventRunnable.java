package subside.plugins.mcbans.runnables;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.database.Event;
import subside.plugins.mcbans.playerapi.PlayerAPI;
import subside.plugins.mcbans.playerapi.Profile;
import subside.plugins.mcbans.utils.IPUtils;
import subside.plugins.mcbans.utils.Perm;
import subside.plugins.mcbans.utils.Utils;

public class IPEventRunnable implements Runnable {
	public enum what {
		ADD, REMOVE
	};

	private String ip;
	private UUID uuid;
	private CommandSender banner;
	private what wht;

	public IPEventRunnable(String ip, UUID uuid, CommandSender banner, what wht) throws Exception {
		this.ip = ip;
		this.uuid = uuid;
		this.banner = banner;
		this.wht = wht;
	}

	@Override
	public void run() {
		try {
			Profile player = null;
			try {
				player = PlayerAPI.getInstance().getProfile(uuid);
			}
			catch (NullPointerException e) {}
			Profile bannr = PlayerAPI.getInstance().getProfile(banner.getName());

			if (wht == what.ADD) {
				if (IPUtils.isIPBanned(ip)) {
					throw new Exception("This IP is already banned!");
				}
				try {
					Perm.canBe(player.getName(), banner, Event.BAN);
				}
				catch (NullPointerException e) {

				}

				IPUtils.addIP(ip, uuid, bannr.getUUID());
				final ArrayList<Player> ipPlayers = new ArrayList<Player>();
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (pl.getAddress().getAddress().getHostAddress().equalsIgnoreCase(ip)) {
						ipPlayers.add(pl);
					}
				}

				Bukkit.getScheduler().runTask(mcBans.getPlugin(), new Runnable() {
					@Override
					public void run() {
						for (Player pl2 : ipPlayers) {
							pl2.kickPlayer(IPUtils.getBanMessage());
						}
					}
				});
				String banMessage = "You have banned the IP!";
				if(player != null)
					banMessage = "You have IP-banned "+player.getName();
				
				Utils.sendMessage(banner, banMessage);
			} else {

				if (!IPUtils.isIPBanned(ip)) {
					throw new Exception("This player is not IP banned!");
				}

				IPUtils.removeIP(ip);
				if (player != null) {
					Utils.sendGlobalBanMessage(new String[] {
						ChatColor.AQUA + banner.getName() + ChatColor.GRAY + " has unbanned the IP of " + ChatColor.AQUA + player.getName() + ChatColor.GRAY + "."
					});
					Utils.sendMessage(banner, "You have unbanned the IP of " + ChatColor.AQUA + player.getName() + "!");
				} else {
					Utils.sendGlobalBanMessage(new String[] {
						ChatColor.AQUA + banner.getName() + ChatColor.GRAY + " has unbanned an IP."
					});
					Utils.sendMessage(banner, "You have unbanned the IP!");

				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			Utils.sendMessage(banner, e.getMessage());
		}

	}
}
