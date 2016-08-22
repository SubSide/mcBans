package subside.plugins.mcbans.commandhandlers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.exceptions.MissingArgumentsException;
import subside.plugins.mcbans.runnables.EventLogRunnable;
import subside.plugins.mcbans.utils.IPerm;
import subside.plugins.mcbans.utils.Perm;


public class EventLogCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length > 0){
			int page = 1;
			try {
				if(args.length > 1){
					page = Integer.parseInt(args[1]);
					if(page < 1){
						page = 1;
					}
				}
			} catch(Exception e){}
			Bukkit.getScheduler().runTaskAsynchronously(mcBans.getPlugin(), new EventLogRunnable(sender, args[0], page));
		} else {
			throw new MissingArgumentsException("/eventlog <username>");
		}
	}

    @Override
    public IPerm getPerm() {
        return Perm.EVENTLOG;
    }

	@Override
	public String getCommand() {
		return "eventlog";
	}
}
