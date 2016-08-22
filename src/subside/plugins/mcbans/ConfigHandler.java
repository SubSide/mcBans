package subside.plugins.mcbans;

import java.util.List;

import lombok.Getter;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHandler {
    private @Getter String server = "";
    private static @Getter ConfigHandler instance;
    private @Getter List<String> banMessage;
    private @Getter List<String> muteMessage;
    private @Getter String defaultBanReason;
    private @Getter String defaultMuteReason;
    private @Getter String defaultUnbanReason;
    private @Getter String defaultUnmuteReason;
    private @Getter boolean banUseNewLines;
    private @Getter String prefix;
    
    public ConfigHandler(mcBans plugin){
        instance = this;
        FileConfiguration cfg = plugin.getConfig();
        server = cfg.getString("server");
        prefix = cfg.getString("prefix");
        banMessage = cfg.getStringList("ban.message");
        muteMessage = cfg.getStringList("mute.message");
        banUseNewLines = cfg.getBoolean("ban.use-newlines");
        defaultBanReason = cfg.getString("ban.default-reason");
        defaultMuteReason = cfg.getString("mute.default-reason");
        defaultUnbanReason = cfg.getString("ban.default-unban-reason");
        defaultUnmuteReason = cfg.getString("mute.default-unmute-reason");
        
    }
}
