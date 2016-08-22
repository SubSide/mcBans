package subside.plugins.mcbans;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import subside.plugins.mcbans.database.Event;
import subside.plugins.mcbans.exceptions.NeverJoinedException;
import subside.plugins.mcbans.playerapi.PlayerAPI;
import subside.plugins.mcbans.playerapi.Profile;
import subside.plugins.mcbans.runnables.MuteCheckRunnable;
import subside.plugins.mcbans.utils.IPUtils;
import subside.plugins.mcbans.utils.Mute;

public class EventListener implements Listener {
	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if(!event.isAsynchronous())
			throw new RuntimeException("Event is ran Synchronous!");
		
		try {
			Profile profile = PlayerAPI.getInstance().getProfile(event.getName());
			if(IPUtils.isIPBanned(profile.getIp())){
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, IPUtils.getBanMessage());
				return;
			}
			if (Event.BAN.is(profile.getUUID())) {
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Event.getBanMessage(profile.getUUID())/*.replaceAll("\n", "  ")*/);
				return;
			}
			/*
			HashMap<String, String> alts = IPUtils.isAlt(profile);
			if (alts.size() > 0){
				String[] msg = new String[alts.size()+2];
				msg[0] = ChatColor.AQUA+profile.getName()+" "+ChatColor.GRAY+"is possibly alting!";
				msg[1] = ChatColor.DARK_GRAY+"----------------------";
				Set<Entry<String, String>> set = alts.entrySet();
				
				int x = 2;
				for(Entry<String, String> entry : set){
					msg[x++] = ChatColor.AQUA+entry.getKey()+ChatColor.GRAY+" banned for: "+ChatColor.DARK_GRAY+entry.getValue();
				}
				Utils.sendGlobalBanMessage(msg);
			}*/
		} catch(SQLException | NeverJoinedException | ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event){
		Mute.setProcessing(event.getPlayer());
		Bukkit.getScheduler().runTaskAsynchronously(mcBans.getPlugin(), new MuteCheckRunnable(event.getPlayer()));
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) throws SQLException{
		if(event.isAsynchronous()){
			try {
				if(Mute.isMuted(event.getPlayer())){
					String[] msg = Mute.getMutedStrings(event.getPlayer());
					if(msg != null){
						event.getPlayer().sendMessage(msg);
						event.setCancelled(true);
					}
				}
			} catch(Exception e){
			    event.setCancelled(true);
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		try {
			String msg = event.getMessage().toLowerCase();
			if(msg.startsWith("/")) msg = msg.substring(1);
			
			boolean hasColon = false;
			if(msg.contains(" ")){
				String[] spl = msg.split(" ");
				if(spl[0].contains(":")){
					hasColon = true;
				}
			}
			
			
			
			if(hasColon || msg.startsWith("f create") || msg.startsWith("f title") || msg.startsWith("f disband") || msg.startsWith("w ") || msg.startsWith("whisper ") || msg.startsWith("m ") || msg.startsWith("msg ") || msg.startsWith("t ") || msg.startsWith("tell ") || msg.startsWith("r ") || msg.startsWith("reply ")){
				if(Mute.isMuted(event.getPlayer())){
					
					String[] msg2 = Mute.getMutedStrings(event.getPlayer());
					if(msg2 != null){
						event.getPlayer().sendMessage(msg2);
						event.setCancelled(true);
					}
				}
			}
		} catch(Exception e){
            event.setCancelled(true);
			event.getPlayer().sendMessage(e.getMessage());
		}

	}
	
    // PlayerAPI events
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
        PlayerAPI.getInstance().putPlayerOnline(e.getPlayer());
	}
	
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        PlayerAPI.getInstance().removeFromPlayersOnline(e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerKickEvent e){
        PlayerAPI.getInstance().removeFromPlayersOnline(e.getPlayer());
    }
}
