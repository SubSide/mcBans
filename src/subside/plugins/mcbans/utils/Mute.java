package subside.plugins.mcbans.utils;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import subside.plugins.mcbans.ConfigHandler;
import subside.plugins.mcbans.MessageBuilder;
import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.exceptions.NotProcessedException;

public enum Mute {
	PROCESSING("processing"),
	MUTED("muted"),
	NOTMUTED("notmuted");
	
	String state;
	
	Mute(String state){
		this.state = state;
	}
	
	public String getState(){
		return state;
	}
	
	public static Mute fromString(String state){
		for(Mute m : values()){
			if(state.equalsIgnoreCase(m.getState())){
				return m;
			}
		}
		return null;
	}
	
	public static void setMuted(Player player){
		player.setMetadata("mcBans_Muted", new FixedMetadataValue((Plugin) mcBans.getPlugin(), Mute.MUTED.getState()));
	}
	
	public static void setNotmuted(Player player){
		player.setMetadata("mcBans_Muted", new FixedMetadataValue((Plugin) mcBans.getPlugin(), Mute.NOTMUTED.getState()));
	}
	
	public static void setProcessing(Player player){
		player.setMetadata("mcBans_Muted", new FixedMetadataValue((Plugin) mcBans.getPlugin(), Mute.PROCESSING.getState()));
	}
	
	public static void setMutedData(Player player, String by, int expires, String reason){
		player.setMetadata("mcBans_MutedBy", new FixedMetadataValue((Plugin) mcBans.getPlugin(), by));
		player.setMetadata("mcBans_ExpiresAt", new FixedMetadataValue((Plugin) mcBans.getPlugin(), expires));
		player.setMetadata("mcBans_Reason", new FixedMetadataValue((Plugin) mcBans.getPlugin(), reason));
	}
	
	public static String[] getMutedStrings(Player player){
	    List<String> list = ConfigHandler.getInstance().getMuteMessage();
	    String[] newList = new MessageBuilder(list).handler(mutedBy(player)).till(mutedTill(player)).reason(mutedReason(player)).buildArray();
//		String[] ret = new String[3];
//		ret[0] = ChatColor.GRAY+"You have been muted by: "+ChatColor.AQUA+mutedBy(player);
//		ret[1] = ChatColor.GRAY+"Expires in: "+ChatColor.GOLD+mutedTill(player);
//		ret[2] = ChatColor.GRAY+"Reason: "+ChatColor.WHITE+mutedReason(player);
		
//		return ret;
	    return newList;
	}
	
	public static boolean isMuted(Player player) throws Exception {
		List<MetadataValue> sdf = player.getMetadata("mcBans_Muted");
		if(sdf.size() > 0){
			if(mutedTill(player) == null){
				return false;
			}

			Mute val = Mute.fromString(sdf.get(0).value().toString());
			
			if(val == Mute.MUTED){
				return true;
			} else if(val == Mute.PROCESSING){
				throw new NotProcessedException();
			} else if(val == Mute.NOTMUTED){
				return false;
			}
		} else {
			throw new Exception("Something went terribly wrong! Please contact an admin!");
		}
		return true;
	}
	
	public static String mutedBy(Player player){
		List<MetadataValue> sdf = player.getMetadata("mcBans_MutedBy");
		if(sdf.size() > 0){
			return sdf.get(0).asString();
		}
		return null;
	}
	
	public static String mutedTill(Player player){
		List<MetadataValue> sdf = player.getMetadata("mcBans_ExpiresAt");
		
		if(sdf.size() > 0){
			if(sdf.get(0).asInt() == -1){
				return "NEVER";
			}
			return TimeConvert.timeTillExpiringMills(sdf.get(0).asInt());
		}
		return null;
	}
	
	public static String mutedReason(Player player){
		List<MetadataValue> sdf = player.getMetadata("mcBans_Reason");
		if(sdf.size() > 0){
			return sdf.get(0).asString();
		}
		return null;
	}
}
