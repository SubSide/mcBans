package subside.plugins.mcbans.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

import subside.plugins.mcbans.utils.Utils;


public class MySQL extends Database {
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	
	public MySQL(Plugin plugin, String hostname, String port, String database, String username, String password) {
		super(plugin);
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}

	@Override
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		if (checkConnection()) {
			return connection;
		}
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.user, this.password);
		return connection;
	}
	
	public void createDatabases(){
		String events = "CREATE TABLE IF NOT EXISTS `events` (`id` int(16) NOT NULL UNIQUE AUTO_INCREMENT, `user_id` varchar(36) NOT NULL, `handler_id` varchar(36) NOT NULL, `server` varchar(16) NOT NULL, `type` varchar(16) NOT NULL, `date_applied` bigint(16) NOT NULL, `date_expiring` bigint(16) DEFAULT NULL, `reason` varchar(128) NOT NULL, `release_date` bigint(16) DEFAULT NULL, `releaser_id` varchar(36) DEFAULT NULL, `release_reason` varchar(128) DEFAULT NULL, PRIMARY KEY(`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		String ipbans = "CREATE TABLE IF NOT EXISTS `IPBans` (`id` int(8) NOT NULL UNIQUE AUTO_INCREMENT, `ip` varchar(40) NOT NULL, `user` varchar(36) NOT NULL, `banner` varchar(36) NOT NULL, PRIMARY KEY(`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		String playerapi = "CREATE TABLE IF NOT EXISTS `player_db` (`id` int(16) NOT NULL UNIQUE AUTO_INCREMENT, `uuid` varchar(36) NOT NULL, `last_updated` bigint(16) NOT NULL, `last_playername` varchar(63) NOT NULL, `last_ip` varchar(31) NOT NULL DEFAULT '0', PRIMARY KEY(`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		        //SELECT uuid, last_updated, last_playername, last_ip FROM player_db WHERE last_playername = ?"
		try {
            Connection con = Utils.mySQL.openConnection();
            PreparedStatement ps = con.prepareStatement(events);
            ps.execute();
            
            ps = con.prepareStatement(ipbans);
            ps.execute();
            
            ps = con.prepareStatement(playerapi);
            ps.execute();
		} catch(Exception e){
		    e.printStackTrace();
		}
	}
}