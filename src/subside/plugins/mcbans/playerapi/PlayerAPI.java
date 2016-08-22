package subside.plugins.mcbans.playerapi;

import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import subside.plugins.mcbans.playerapi.Mojang.profiles.HttpProfileRepository;
import subside.plugins.mcbans.utils.Utils;

public class PlayerAPI {
    private static PlayerAPI instance = new PlayerAPI();

	private HttpProfileRepository profileRepository = new HttpProfileRepository("minecraft");
	private HashMap<String, Profile> onlinePlayers = new HashMap<String, Profile>();
	private HashMap<UUID, Profile> uonlinePlayers = new HashMap<UUID, Profile>();

	public static PlayerAPI getInstance(){
	    return instance;
	}

	public Profile getProfile(String playerName) {
		Profile p = getPlayerOnline(playerName);
		if(p != null){
			return p;
		}
		try {
			PreparedStatement ps = Utils.mySQL.openConnection().prepareStatement("SELECT uuid, last_updated, last_playername, last_ip FROM player_db WHERE last_playername = ?");
			ps.setString(1, playerName);
			ResultSet rs = ps.executeQuery();
			boolean hasNext = false;
			if (rs.next()) {
				//hasNext = true;
				long lastupdate = rs.getLong("last_updated") + 60 * 60 * 1000L;

				if (lastupdate > System.currentTimeMillis()) {
					Profile pr = new Profile(rs.getString("last_playername"), UUID.fromString(rs.getString("uuid")), rs.getString("last_ip"));
					updateDatabase(pr);
					return pr;
				}
			}
			Profile pr = requestUUIDFromMojang(playerName);
			if(pr != null){
				updateDatabase(pr);
				return pr;
			} else if(hasNext){
				return new Profile(rs.getString("last_playername"), UUID.fromString(rs.getString("uuid")), rs.getString("last_ip"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Profile getProfile(UUID uuid) {
		Profile p = getPlayerOnline(uuid);
		if(p != null){
			return p;
		}
		try {
			PreparedStatement ps = Utils.mySQL.openConnection().prepareStatement("SELECT last_playername, last_updated, last_ip FROM player_db WHERE uuid=?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			boolean hasNext = false;
			if (rs.next()) {
				//hasNext = true;
				//System.out.println((rs.getLong("last_updated")+60*60*1000)+" "+(System.currentTimeMillis()));
				if (rs.getLong("last_updated") + 60 * 60 * 1000 > System.currentTimeMillis()) {
					String name = rs.getString("last_playername");
					Profile pr = new Profile(name, uuid, rs.getString("last_ip"));
					updateDatabase(pr);
					return pr;
				}
			}
			
			Profile profile = requestNameFromMojang(uuid);
			if(profile != null){
				updateDatabase(profile);
				return profile;
			} else if(hasNext){
				return new Profile(rs.getString("last_playername"), uuid, rs.getString("last_ip"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Profile[] getProfiles(String ip) {
		try {
			PreparedStatement ps = Utils.mySQL.openConnection().prepareStatement("SELECT last_playername, uuid FROM player_db WHERE last_ip=?");
			ps.setString(1, ip);
			ResultSet rs = ps.executeQuery();

			ArrayList<Profile> profiles = new ArrayList<Profile>();
			while (rs.next()) {
				profiles.add(new Profile(rs.getString("last_playername"), toUUID(rs.getString("uuid")), ip));
			}
			return profiles.toArray(new Profile[profiles.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Profile[0];
	}

	@Deprecated
	public Profile[] getProfiles(ArrayList<String> names) {
		subside.plugins.mcbans.playerapi.Mojang.profiles.Profile[] profiles = null;
		Profile[] pProfiles = null;
		try {
			profiles = profileRepository.findProfilesByNames(names);
			pProfiles = new Profile[profiles.length];
			int x = 0;
			for (subside.plugins.mcbans.playerapi.Mojang.profiles.Profile profile : profiles) {
				Profile pr = new Profile(profile.getName(), toUUID(profile.getId()), null);
				updateDatabase(pr);
				pProfiles[x++] = pr;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pProfiles;
	}

	private Profile requestNameFromMojang(UUID uuid) {
		try {
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replaceAll("-", ""));
			URLConnection connection = url.openConnection();
			Scanner jsonScanner = new Scanner(connection.getInputStream(), "UTF-8");
			String json = jsonScanner.next();
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(json);
			String name = (String) ((JSONObject) obj).get("name");
			jsonScanner.close();
			return new Profile(name, uuid, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Profile requestUUIDFromMojang(String name) {
		subside.plugins.mcbans.playerapi.Mojang.profiles.Profile[] profiles = profileRepository.findProfilesByNames(name);
		if (profiles.length > 0) {
			return new Profile(profiles[0].getName(), toUUID(profiles[0].getId()), null);
		}

		return null;
	}

	protected void updateDatabase(Profile profile) throws SQLException {
		Connection con;
		String ip = null;
		if(Bukkit.getPlayer(profile.getName()) != null){
			if(Bukkit.getPlayer(profile.getName()).isOnline()) {
				ip = Bukkit.getPlayer(profile.getName()).getAddress().getAddress().getHostAddress();
			}
		}
		try {
			con = Utils.mySQL.openConnection();

			PreparedStatement ps = con.prepareStatement("SELECT uuid FROM player_db WHERE uuid=?");
			ps.setString(1, profile.getUUID().toString());

			ResultSet set = ps.executeQuery();

			if (set.next()) {
				PreparedStatement ps2;
				if (ip != null) {
					ps2 = con.prepareStatement("UPDATE player_db SET last_playername=?, last_updated=?, last_ip=? WHERE UUID=?");
				} else {
					ps2 = con.prepareStatement("UPDATE player_db SET last_playername=?, last_updated=? WHERE UUID=?");
				}
				ps2.setString(1, profile.getName());
				ps2.setLong(2, System.currentTimeMillis());
				if (ip != null) {
					ps2.setString(3, ip);
					ps2.setString(4, profile.getUUID().toString());
				} else {
					ps2.setString(3, profile.getUUID().toString());
				}
				ps2.executeUpdate();
			} else {
				PreparedStatement ps2;
				if (ip != null) {
					ps2 = con.prepareStatement("INSERT INTO player_db (last_playername, last_updated, UUID, last_ip) VALUES (?, ?, ?, ?)");
				} else {
					ps2 = con.prepareStatement("INSERT INTO player_db (last_playername, last_updated, UUID) VALUES (?, ?, ?)");
				}

				ps2.setString(1, profile.getName());
				ps2.setLong(2, System.currentTimeMillis());
				ps2.setString(3, profile.getUUID().toString());
				if (ip != null) {
					ps2.setString(4, ip);
				}
				ps2.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Profile getPlayerOnline(String player){
		if(onlinePlayers.containsKey(player.toLowerCase())){
			return onlinePlayers.get(player);
		}
		return null;
	}
	
	private Profile getPlayerOnline(UUID uuid){
		if(uonlinePlayers.containsKey(uuid)){
			return uonlinePlayers.get(uuid);
		}
		return null;
	}
	
	public void putPlayerOnline(Player player){
		if(!onlinePlayers.containsKey(player.getName().toLowerCase())){
			Profile p = getProfile(player.getName());
			onlinePlayers.put(player.getName().toLowerCase(), p);
			uonlinePlayers.put(p.getUUID(), p);
		}
	}
	
	public void removeFromPlayersOnline(Player player){
		if(onlinePlayers.containsKey(player.getName().toLowerCase())){
			Profile p = onlinePlayers.get(player.getName().toLowerCase());
			onlinePlayers.remove(player.getName().toLowerCase());
			uonlinePlayers.remove(p.getUUID());
		}
	}

	public UUID toUUID(String uuid) {
		if (!uuid.contains("-")) {
			return UUID.fromString(uuid.toString().substring(0, 8) + "-" + uuid.toString().substring(8, 12) + "-" + uuid.toString().substring(12, 16) + "-" + uuid.toString().substring(16, 20) + "-" + uuid.toString().substring(20, 32));
		}

		return UUID.fromString(uuid);
	}
	
	public String toMojangId(UUID uuid){
		return uuid.toString().replaceAll("-", "");
	}
}
