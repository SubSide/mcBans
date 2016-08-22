package subside.plugins.mcbans.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import subside.plugins.mcbans.ConfigHandler;
import subside.plugins.mcbans.database.Event;
import subside.plugins.mcbans.playerapi.PlayerAPI;
import subside.plugins.mcbans.playerapi.Profile;

public class IPUtils {
	public static boolean isIPBanned(String ip) throws SQLException, ClassNotFoundException {
		Connection con = Utils.mySQL.openConnection();
		PreparedStatement stat = con.prepareStatement("SELECT * FROM IPBans WHERE ip=?");
		stat.setString(1, ip);
		ResultSet set = stat.executeQuery();
		if (set.next()) {
			return true;
		}
		return false;
	}

	public static boolean isIPBanned(UUID uuid) throws ClassNotFoundException, SQLException {
		return isIPBanned(PlayerAPI.getInstance().getProfile(uuid).getIp());
	}

	public static UUID getBanner(String ip) throws ClassNotFoundException, SQLException {
		Connection con = Utils.mySQL.openConnection();
		PreparedStatement stat = con.prepareStatement("SELECT banner FROM IPBans WHERE ip=?");
		stat.setString(1, ip);
		ResultSet set = stat.executeQuery();
		if (set.next()) {
			return PlayerAPI.getInstance().toUUID(set.getString("banner"));
		}
		return null;
	}

	public static void addIP(String ip, UUID uuid, UUID banner) throws SQLException, ClassNotFoundException {
		Connection con = Utils.mySQL.openConnection();
		PreparedStatement stat = con.prepareStatement("INSERT INTO IPBans (ip, user, banner) VALUES (?, ?, ?)");
		stat.setString(1, ip);
		if (uuid != null) {
			stat.setString(2, uuid.toString());
		} else {
			stat.setString(2, "");
		}
		stat.setString(3, banner.toString());
		stat.execute();
	}

	public static void addIP(UUID uuid, UUID banner) throws ClassNotFoundException, SQLException {
		addIP(PlayerAPI.getInstance().getProfile(uuid).getIp(), uuid, banner);
	}

	public static void removeIP(String ip) throws SQLException, ClassNotFoundException {
		Connection con = Utils.mySQL.openConnection();
		PreparedStatement stat = con.prepareStatement("DELETE FROM IPBans WHERE ip=?");
		stat.setString(1, ip);
		stat.execute();
	}

	public static void removeIP(UUID uuid) throws ClassNotFoundException, SQLException {
		removeIP(PlayerAPI.getInstance().getProfile(uuid).getIp());
	}

	public static String getBanMessage() {
		return "You have been IP banned from the server!";
	}

	public static HashMap<String, String> isAlt(Profile profile) {
		HashMap<String, String> players = new HashMap<String, String>();
		try {
			Profile[] prfl = PlayerAPI.getInstance().getProfiles(profile.getIp());
			for (Profile pr : prfl) {
				if (!profile.getName().equalsIgnoreCase(pr.getName())) {
					if (Event.BAN.is(pr.getUUID())) {
						try {
							Connection con = Utils.mySQL.openConnection();
							// PreparedStatement ps =
							// con.prepareStatement("SELECT u.last_playername AS handler_name, date_applied,date_expiring,reason FROM events e LEFT JOIN users u ON e.handler_id=u.user_id WHERE user_id=? AND release_date IS NULL AND server=? AND type=? AND (date_expiring>? OR date_expiring IS NULL);");
							PreparedStatement ps = con.prepareStatement("SELECT reason FROM events WHERE user_id=? AND release_date IS NULL AND server=? AND type=? AND (date_expiring>? OR date_expiring IS NULL)");
							ps.setString(1, pr.getUUID().toString());
							ps.setString(2, ConfigHandler.getInstance().getServer());
							ps.setString(3, "ban");
							ps.setInt(4, Utils.getTimestamp());

							ResultSet result = ps.executeQuery();

							if (result.next()) {
								players.put(pr.getName(), result.getString("reason"));
							}

						}
						catch (SQLException e) {}
					}
				}
			}
		}
		catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return players;
	}
}
