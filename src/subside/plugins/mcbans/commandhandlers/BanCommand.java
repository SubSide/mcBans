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

public class BanCommand extends AbstractCommand {

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length > 1 || (args.length > 0 && Perm.Ban.REASONOPTIONAL.has(sender))) {
            String reason;
            if (args.length > 1) {
                reason = Utils.getFromArray(args, 1);
            } else {
                reason = ConfigHandler.getInstance().getDefaultBanReason();
            }
            Bukkit.getScheduler().runTaskAsynchronously(mcBans.getPlugin(), new AddEventRunnable(args[0], sender, null, reason, Event.BAN));
            return;
        } else {
            throw new MissingArgumentsException("/ban <username> <reason>");
        }

    }

    @Override
    public IPerm getPerm() {
        return Perm.Ban.PERMBAN;
    }

    @Override
    public String getCommand() {
        return "ban";
    }
}
