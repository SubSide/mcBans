package subside.plugins.mcbans.commandhandlers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.ConfigHandler;
import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.database.Event;
import subside.plugins.mcbans.exceptions.MissingArgumentsException;
import subside.plugins.mcbans.runnables.RemoveEventRunnable;
import subside.plugins.mcbans.utils.IPerm;
import subside.plugins.mcbans.utils.Perm;
import subside.plugins.mcbans.utils.Utils;


public class UnbanCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length > 1 || (args.length > 0 && Perm.Ban.REASONOPTIONAL.has(sender))) {
            String reason;
            if (args.length > 1) {
                reason = Utils.getFromArray(args, 1);
            } else {
                reason = ConfigHandler.getInstance().getDefaultUnbanReason();
            }
			Bukkit.getScheduler().runTaskAsynchronously(mcBans.getPlugin(), new RemoveEventRunnable(args[0], sender, reason, Event.BAN));
		} else {
			throw new MissingArgumentsException("/unban <username> <reason>");
		}
	}

    @Override
    public IPerm getPerm() {
        return Perm.Ban.UNBAN;
    }

	@Override
	public String getCommand() {
		return "unban";
	}
}
