package subside.plugins.mcbans.utils;

import org.bukkit.ChatColor;

public class HistoryPart {
	public final String user;
	public final String handler;
	public final String server;
	public final String type;
	public final int date_applied;
	public final int date_expiring;
	public final String reason;
	
	public boolean released = false;
	public String releaser = null;
	public int release_date = -1;
	public String release_reason = null;
	
	public HistoryPart(String user, String handler, String server, String type, int date_applied, int date_expiring, String reason){
		this.user = user;
		this.handler = handler;
		this.server = server;
		this.type = type;
		this.date_applied = date_applied;
		this.date_expiring = date_expiring;
		this.reason = reason;
	}
	
	public void addEarlyRelease(String releaser, int release_date, String release_reason){
		this.released = true;
		this.releaser = releaser;
		this.release_date = release_date;
		this.release_reason = release_reason;
	}
	
	public String[] genString(){
		boolean expired = false;
		if(released || (date_expiring != -1 && date_expiring != 0 && date_expiring < Utils.getTimestamp())){
			expired = true;
		}

		String applied = Utils.formatTime(date_applied);
		String released_on = ChatColor.GOLD+((date_expiring != -1 && date_expiring != 0)?Utils.formatTime(date_expiring):"forever");
		applied = ChatColor.GOLD+applied.split(" ", 2)[0]+ChatColor.GRAY+" "+applied.split(" ", 2)[1];
		if(released_on.contains(" ")){
			released_on = ChatColor.GOLD+released_on.split(" ", 2)[0]+ChatColor.GRAY+" "+released_on.split(" ", 2)[1];
		} else {
			released_on = ChatColor.GOLD+released_on;
		}
		String[] ret = new String[released?3:2];
		/*ret[0] = (expired?(released?ChatColor.GOLD.toString():ChatColor.COLOR_CHAR+"a"):ChatColor.COLOR_CHAR+"c")+"("+type+")"+ChatColor.RESET+" "
				+applied+" "+ChatColor.GRAY+"(till "+released_on+ChatColor.GRAY+") "
				+ChatColor.GRAY+"by "+ChatColor.AQUA+handler;*/
		ret[0] = (expired?(released?ChatColor.GOLD.toString():ChatColor.COLOR_CHAR+"a"):ChatColor.COLOR_CHAR+"c")+"("+type+")"+ChatColor.GRAY+" by "+ChatColor.DARK_AQUA+ChatColor.ITALIC+handler+ChatColor.RESET+" "
				+ChatColor.GRAY+"at "+applied+" "+ChatColor.GRAY+"(till "+released_on+ChatColor.GRAY+") ";
		ret[1] = ChatColor.GRAY+"  Reason: "+ChatColor.GOLD+reason;
		
		if(released){
			ret[2] = ChatColor.GRAY+"  Removed by "+releaser+": "+release_reason;
		}
		return ret;
	}
	
}
// (mute) 11-09-2014 11:49 (for 2h) by SubSide: being an asshole (removed)