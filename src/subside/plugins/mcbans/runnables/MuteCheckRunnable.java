package subside.plugins.mcbans.runnables;

import org.bukkit.entity.Player;

import subside.plugins.mcbans.database.Event;
import subside.plugins.mcbans.playerapi.PlayerAPI;
import subside.plugins.mcbans.utils.Mute;

public class MuteCheckRunnable implements Runnable {
	
	private Player player;

	public MuteCheckRunnable(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {
			if(Event.MUTE.is(PlayerAPI.getInstance().getProfile(player.getName()).getUUID())){
				Event.setMuteData(player);
				Mute.setMuted(player);
			} else {
				Mute.setNotmuted(player);
			}
		} catch(Exception e){
			
		}
	}
}
