package subside.plugins.mcbans.commandhandlers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.ConfigHandler;
import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.database.Event;
import subside.plugins.mcbans.exceptions.MissingArgumentsException;
import subside.plugins.mcbans.runnables.AddEventRunnable;
import subside.plugins.mcbans.utils.IPerm;
import subside.plugins.mcbans.utils.Perm;
import subside.plugins.mcbans.utils.Utils;


public class TempbanCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length > 2 || (args.length > 1 && Perm.Mute.REASONOPTIONAL.has(sender))) {
            String reason;
            if (args.length > 2) {
                reason = Utils.getFromArray(args, 2);
            } else {
                reason = ConfigHandler.getInstance().getDefaultBanReason();
            }
			Bukkit.getScheduler().runTaskAsynchronously(mcBans.getPlugin(), new AddEventRunnable(args[0], sender, args[1], reason, Event.BAN));
		} else {
			throw new MissingArgumentsException("/tempban <username> <time> <reason>");
		}
	}

    @Override
    public IPerm getPerm() {
        return Perm.Ban.TEMPBAN;
    }

	@Override
	public String getCommand() {
		return "tempban";
	}
}
