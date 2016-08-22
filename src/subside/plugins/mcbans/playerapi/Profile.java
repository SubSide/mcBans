package subside.plugins.mcbans.playerapi;

import java.util.UUID;

public class Profile {
	private UUID uuid;
	private String name;
	private String ip;
	
	public Profile(String name, UUID uuid, String ip){
		this.name = name;
		this.uuid = uuid;
		this.ip = ip;
	}
	
	public UUID getUUID(){
		return uuid;
	}
	
	public String getName(){
		return name;
	}
	
	public String getIp(){
		return ip;
	}
}
