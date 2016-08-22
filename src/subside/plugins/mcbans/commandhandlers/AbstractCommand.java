package subside.plugins.mcbans.commandhandlers;

import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.utils.IPerm;

public abstract class AbstractCommand {
	
	public abstract void execute(CommandSender sender, String[] args) throws Exception ;
	
	public abstract IPerm getPerm();
	
	public abstract String getCommand();
}
