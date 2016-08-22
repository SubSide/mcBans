package subside.plugins.mcbans.runnables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.playerapi.PlayerAPI;
import subside.plugins.mcbans.playerapi.Profile;
import subside.plugins.mcbans.utils.Utils;

public class EventLogRunnable implements Runnable {
	private CommandSender requester;
	private String requesting;
	private int page;

	public EventLogRunnable(CommandSender requester, String requesting, int page) {
		this.requester = requester;
		this.requesting = requesting;
		this.page = page-1;
	}

	@Override
	public void run() {
		try {
			
			Profile user = PlayerAPI.getInstance().getProfile(requesting);
			if(user == null){
				Utils.sendMessage(requester, "The player \""+requesting+"\" could not be found!");
				return;
			}

			Connection con = Utils.mySQL.openConnection();
			

			PreparedStatement count = con.prepareStatement("SELECT count(*) as count FROM events WHERE handler_id=? OR releaser_id=?");
			count.setString(1, user.getUUID().toString());
			count.setString(2, user.getUUID().toString());
			ResultSet countRS = count.executeQuery();
			
			int cnt = 0;
			if(countRS.next()){
				cnt = countRS.getInt("count");
			}
			

			ArrayList<String> echo = new ArrayList<String>();
			echo.add(" ");
			if(cnt > 0){
				if(cnt > page*10){
					// 											select j.title, u.username from jobs j left join users u on u.id=j.added_by
					//PreparedStatement ps = con.prepareStatement("SELECT * FROM events_db WHERE uuid=? ORDER BY date_applied ASC");
					PreparedStatement ps = con.prepareStatement("SELECT user_id, handler_id, server, `type`, date_applied, date_expiring, reason, releaser_id, release_date, release_reason FROM events WHERE handler_id=? OR releaser_id=? ORDER BY date_applied DESC LIMIT ?, 10");
					ps.setString(1, user.getUUID().toString());
					ps.setString(2, user.getUUID().toString());
					ps.setInt(3, page*10);
					
		
					ResultSet res = ps.executeQuery();
					HashMap<String, String> uuid_conv = new HashMap<String, String>();
		
					echo.add(ChatColor.GRAY+"Events by: "+ChatColor.DARK_AQUA+user.getName());
					echo.add(ChatColor.DARK_GRAY+"===========================");
					while (res.next()) {
						
						if(!uuid_conv.containsKey(res.getString("user_id")))
							uuid_conv.put(res.getString("user_id"), PlayerAPI.getInstance().getProfile(PlayerAPI.getInstance().toUUID(res.getString("user_id"))).getName());
						if(!uuid_conv.containsKey(res.getString("handler_id")))
							uuid_conv.put(res.getString("handler_id"), PlayerAPI.getInstance().getProfile(PlayerAPI.getInstance().toUUID(res.getString("handler_id"))).getName());
						if(res.getString("releaser_id") != null && !uuid_conv.containsKey(res.getString("releaser_id")))
							uuid_conv.put(res.getString("releaser_id"), PlayerAPI.getInstance().getProfile(PlayerAPI.getInstance().toUUID(res.getString("releaser_id"))).getName());
						
						
		
						String username = uuid_conv.get(res.getString("user_id"));
						String handler = uuid_conv.get(res.getString("handler_id"));
						String what = (res.getString("type").equalsIgnoreCase("mute")?"muted":"banned");
						
						int date_expiring = res.getInt("date_expiring");
						
						String released_on = ((date_expiring != -1 && date_expiring != 0)?Utils.formatTime(date_expiring):"forever");
						echo.add(ChatColor.DARK_AQUA+handler+ChatColor.GRAY+" "+what+" "+ChatColor.DARK_AQUA+username+ChatColor.GRAY+" till "+ChatColor.DARK_AQUA+released_on);
						if(res.getString("releaser_id") != null){
							echo.add(ChatColor.GRAY+"  released by "+ChatColor.DARK_AQUA+uuid_conv.get(res.getString("releaser_id")));
						}
						
						
					}
					echo.add(ChatColor.DARK_GRAY+"===========================");
					echo.add(ChatColor.GRAY+"Page "+(page+1)+"/"+((int)((cnt+10)/10)));
				} else {
					echo.add(ChatColor.GRAY+"Events by: "+ChatColor.DARK_AQUA+user.getName());
					echo.add(ChatColor.DARK_GRAY+"===========================");
					echo.add(ChatColor.GRAY+"This page doesn't exist!");
					echo.add(ChatColor.DARK_GRAY+"===========================");
					echo.add(ChatColor.GRAY+"Page "+(page+1)+"/"+((int)((cnt+10)/10)));
					
				}
			} else {
				echo.add(ChatColor.GRAY+"Events by: "+ChatColor.DARK_AQUA+user.getName());
				echo.add(ChatColor.DARK_GRAY+"===========================");
				echo.add(ChatColor.GRAY+"This player has no events on his name!");
				echo.add(ChatColor.DARK_GRAY+"===========================");
			}
			echo.add(" ");
			requester.sendMessage(echo.toArray(new String[echo.size()]));
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
