package subside.plugins.mcbans.runnables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.playerapi.PlayerAPI;
import subside.plugins.mcbans.playerapi.Profile;
import subside.plugins.mcbans.utils.HistoryHandler;
import subside.plugins.mcbans.utils.HistoryPart;
import subside.plugins.mcbans.utils.Utils;

public class HistoryRunnable implements Runnable {
	private CommandSender requester;
	private String requesting;

	public HistoryRunnable(CommandSender requester, String requesting) {
		this.requester = requester;
		this.requesting = requesting;
	}

	@Override
	public void run() {
		try {
			HistoryHandler handler = new HistoryHandler(requesting);
			
			Profile user = PlayerAPI.getInstance().getProfile(requesting);
			if(user == null){
				Utils.sendMessage(requester, "The player \""+requesting+"\" could not be found!");
				return;
			}
			
			Connection con = Utils.mySQL.openConnection();
			// 											select j.title, u.username from jobs j left join users u on u.id=j.added_by
			//PreparedStatement ps = con.prepareStatement("SELECT * FROM events_db WHERE uuid=? ORDER BY date_applied ASC");
			PreparedStatement ps = con.prepareStatement("SELECT handler_id, releaser_id, server, `type`, date_applied, date_expiring, reason, release_date, release_reason FROM events  WHERE user_id=? ORDER BY date_applied ASC");
			ps.setString(1, user.getUUID().toString());

			
			ResultSet res = ps.executeQuery();
			HashMap<String, String> uuid_conv = new HashMap<String, String>();
			while (res.next()) {
				
				if(!uuid_conv.containsKey(res.getString("handler_id"))) 
					uuid_conv.put(res.getString("handler_id"), PlayerAPI.getInstance().getProfile(PlayerAPI.getInstance().toUUID(res.getString("handler_id"))).getName());
				
				String handler_name = uuid_conv.get(res.getString("handler_id"));
				
				HistoryPart part = new HistoryPart(user.getName(), handler_name, res.getString("server"), res.getString("type"), res.getInt("date_applied"), (res.getObject("date_expiring") != null)?res.getInt("date_expiring") : -1, res.getString("reason"));
				if (res.getObject("release_date") != null) {
					if(!uuid_conv.containsKey(res.getString("releaser_id"))){
						uuid_conv.put(res.getString("releaser_id"), PlayerAPI.getInstance().getProfile(PlayerAPI.getInstance().toUUID(res.getString("releaser_id"))).getName());
					}
					String releaser_name = uuid_conv.get(res.getString("releaser_id"));
					
					part.addEarlyRelease(releaser_name, res.getInt("release_date"), res.getString("release_reason"));
				}
				handler.addHistory(part);
			}

			handler.buildAndSendMessage(requester);

		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
