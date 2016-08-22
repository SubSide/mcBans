package subside.plugins.mcbans.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.mcbans.database.Event;
import subside.plugins.mcbans.exceptions.PlayerExcludedException;
import subside.plugins.mcbans.exceptions.PlayerOfflineException;

public enum Perm implements IPerm {
    
    HISTORYPERM("mcbans.history"), EVENTLOG("mcbans.eventlog"), CANSEEBROADCASTS("mcbans.seebroadcasts");
    
    private String perm;
    
    Perm(String perm){
        this.perm = "mcbans."+perm;
    }
    
    public boolean has(CommandSender sender){
        return sender.hasPermission(perm);
    }
    
    public enum Ban implements IPerm {
        TEMPBAN("tempban"), PERMBAN("ban"), UNBAN("unban"), OFFLINE("offline"), EXCLUDED("excluded"), REASONOPTIONAL("reasonoptional"), BANIP("banip"), UNBANIP("unbanip");
        
        private String perm;
        Ban(String perm){
            this.perm = "mcbans.ban."+perm;
        }
        
        public boolean has(CommandSender sender){
            return sender.hasPermission(perm);
        }
    }
    
    public enum Mute implements IPerm {
        TEMPMUTE("tempmute"), PERMMUTE("mute"), UNMUTE("unmute"), OFFLINE("offline"), EXCLUDED("excluded"), REASONOPTIONAL("reasonoptional");
        
        private String perm;
        Mute(String perm){
            this.perm = "mcbans.ban."+perm;
        }
        
        public boolean has(CommandSender sender){
            return sender.hasPermission(perm);
        }
    }
    

    
    public static boolean canBe(String player, CommandSender sender, Event e) throws Exception {
        if(e == Event.MUTE){
            return canBeMuted(player, sender);
        } else if(e == Event.BAN){
            return canBeBanned(player, sender);
        } else {
            return false;
        }
    }
    
    
    public static boolean canBeBanned(String player, CommandSender banner) throws Exception {
        if(banner.getName().equalsIgnoreCase(player)){
            throw new Exception("Why would you ban yourself!? O.o");
        }
        Player pl = Bukkit.getPlayer(player);
        if(pl != null){
            if(Ban.EXCLUDED.has(pl)){
                throw new PlayerExcludedException("ban");
            }
        } else if(!Ban.OFFLINE.has(banner)){
            throw new PlayerOfflineException("ban");
        }
        return true;
    }
    
    public static boolean canBeMuted(String player, CommandSender muter) throws Exception {
        if(muter.getName().equalsIgnoreCase(player)){
            throw new Exception("Why would you mute yourself!? O.o");
        }
        Player pl = Bukkit.getPlayer(player);
        if(pl != null){
            if(Mute.EXCLUDED.has(pl)){
                throw new PlayerExcludedException("mute");
            }
        } else if(!Mute.OFFLINE.has(muter)){
            throw new PlayerOfflineException("mute");
        }
        return true;
    }
}
