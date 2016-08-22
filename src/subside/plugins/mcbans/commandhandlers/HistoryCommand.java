package subside.plugins.mcbans.commandhandlers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.exceptions.MissingArgumentsException;
import subside.plugins.mcbans.runnables.HistoryRunnable;
import subside.plugins.mcbans.utils.IPerm;
import subside.plugins.mcbans.utils.Perm;


public class HistoryCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length > 0){
			Bukkit.getScheduler().runTaskAsynchronously(mcBans.getPlugin(), new HistoryRunnable(sender, args[0]));
		} else {
			throw new MissingArgumentsException("/history <username>");
		}
	}

    @Override
    public IPerm getPerm() {
        return Perm.EVENTLOG;
    }
    
	@Override
	public String getCommand() {
		return "history";
	}
}
