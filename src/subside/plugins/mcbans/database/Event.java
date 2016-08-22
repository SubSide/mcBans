package subside.plugins.mcbans.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import subside.plugins.mcbans.ConfigHandler;
import subside.plugins.mcbans.MessageBuilder;
import subside.plugins.mcbans.exceptions.AlreadyEventException;
import subside.plugins.mcbans.exceptions.NeverJoinedException;
import subside.plugins.mcbans.exceptions.NotEventException;
import subside.plugins.mcbans.playerapi.PlayerAPI;
import subside.plugins.mcbans.utils.Mute;
import subside.plugins.mcbans.utils.Utils;

public enum Event {
	MUTE, BAN;
	
	Event(){
	}
	
	
	public void add(UUID user_id, UUID handler_id, int until, String reason) throws Exception {
		if(!is(user_id)){
			Connection con = Utils.mySQL.openConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO events(user_id, handler_id, server, type, date_applied, date_expiring, reason) VALUES(?,?,?,?,?,?,?);");
			ps.setString(1, user_id.toString());
			ps.setString(2, handler_id.toString());
			ps.setString(3, ConfigHandler.getInstance().getServer());
			ps.setString(4, this.toString());
			ps.setInt(5, Utils.getTimestamp());
			ps.setObject(6, until==-1?null:until);
			ps.setString(7, reason);
			
			ps.execute();
		} else {
			throw new AlreadyEventException(this);
		}
	}
	
	public void remove(UUID user_id, UUID handler_id, String reason) throws Exception {
		if(is(user_id)){
			Connection con = Utils.mySQL.openConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE events SET release_date=?, releaser_id=?, release_reason=? WHERE user_id=? AND release_date IS NULL AND server=? AND type=? AND (date_expiring>? OR date_expiring IS NULL);");
			ps.setInt(1, Utils.getTimestamp());
			ps.setString(2, handler_id.toString());
			ps.setString(3, reason);

			ps.setString(4, user_id.toString());
			ps.setString(5, ConfigHandler.getInstance().getServer());
			ps.setString(6, this.toString());
			ps.setInt(7, Utils.getTimestamp());
			
			
			ps.execute();
		} else {
			throw new NotEventException(this);
		}
	}
	
	public boolean is(UUID uuid) throws ClassNotFoundException, SQLException {
		Connection con = Utils.mySQL.openConnection();
		PreparedStatement ps = con.prepareStatement("SELECT id FROM events WHERE user_id=? AND release_date IS NULL AND server=? AND type=? AND (date_expiring>? OR date_expiring IS NULL);");
		ps.setString(1, uuid.toString());
		ps.setString(2, ConfigHandler.getInstance().getServer());
		ps.setString(3, this.toString());
		ps.setInt(4, Utils.getTimestamp());
		
		ResultSet res = ps.executeQuery();
			
		return res.next();
	}
	
	
	public static String getBanMessage(UUID user_id) throws SQLException, NeverJoinedException, ClassNotFoundException {
		Connection con = Utils.mySQL.openConnection();
		//PreparedStatement ps = con.prepareStatement("SELECT u.last_playername AS handler_name, date_applied,date_expiring,reason FROM events e LEFT JOIN users u ON e.handler_id=u.user_id WHERE user_id=? AND release_date IS NULL AND server=? AND type=? AND (date_expiring>? OR date_expiring IS NULL);");
		PreparedStatement ps = con.prepareStatement("SELECT handler_id, date_applied, date_expiring, reason FROM events WHERE user_id=? AND release_date IS NULL AND server=? AND type=? AND (date_expiring>? OR date_expiring IS NULL)");
		ps.setString(1, user_id.toString());
		ps.setString(2, ConfigHandler.getInstance().getServer());
		ps.setString(3, "ban");
		ps.setInt(4, Utils.getTimestamp());
		
		ResultSet result = ps.executeQuery();
		
		if(result.next()){
			
			String handler = PlayerAPI.getInstance().getProfile(PlayerAPI.getInstance().toUUID(result.getString("handler_id"))).getName();
			String from = Utils.formatTime(result.getInt("date_applied"));
			String till = (result.getObject("date_expiring") != null ? Utils.formatTime(result.getInt("date_expiring")) : "NEVER");
			String reason = result.getString("reason");
			
			List<String> list = ConfigHandler.getInstance().getBanMessage();
			String[] newList = new MessageBuilder(list).handler(handler).from(from).till(till).reason(reason).buildArray();
			
			//return ChatColor.DARK_RED + "You have been banned from this server!\n" + ChatColor.WHITE + "By: " + ChatColor.RED + handler + " " + ChatColor.WHITE + "Date: " + ChatColor.RED + from + "\n" + ChatColor.WHITE + "Expires at: " + ChatColor.RED + till + "\n" + ChatColor.WHITE + "Reason: " + ChatColor.RED + reason;
			return StringUtils.join(newList, ConfigHandler.getInstance().isBanUseNewLines()?"\n":"  ");
		}
		return null;
	}
	
	public static void setMuteData(Player player) {
		try {
			Connection con = Utils.mySQL.openConnection();
			PreparedStatement ps = con.prepareStatement("SELECT handler_id,date_expiring,reason FROM events WHERE user_id=? AND release_date IS NULL AND server=? AND type=? AND (date_expiring>? OR date_expiring IS NULL);");
			ps.setString(1, PlayerAPI.getInstance().getProfile(player.getName()).getUUID().toString());
			ps.setString(2, ConfigHandler.getInstance().getServer());
			ps.setString(3, "mute");
			ps.setInt(4, Utils.getTimestamp());
			
			ResultSet res = ps.executeQuery();
			
			if(res.next()){
				int expiring = 0;
				if(res.getObject("date_expiring") == null){
					expiring = -1;
				} else {
					expiring = res.getInt("date_expiring");
				}
				
				Mute.setMutedData(player, PlayerAPI.getInstance().getProfile(PlayerAPI.getInstance().toUUID(res.getString("handler_id"))).getName(), expiring, res.getString("reason"));
				
			}
		} catch(Exception e){
			
		}
	}
}
