package subside.plugins.mcbans.commandhandlers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.exceptions.MissingArgumentsException;
import subside.plugins.mcbans.playerapi.PlayerAPI;
import subside.plugins.mcbans.playerapi.Profile;
import subside.plugins.mcbans.runnables.IPEventRunnable;
import subside.plugins.mcbans.utils.IPerm;
import subside.plugins.mcbans.utils.Perm;
import subside.plugins.mcbans.utils.Utils;

public class BanIPCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length > 0){
			IPEventRunnable runnable = null;
			if(args[0].matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+")){
				runnable = new IPEventRunnable(args[0], null, sender, IPEventRunnable.what.ADD);
			} else {
				Profile p = PlayerAPI.getInstance().getProfile(args[0]);
				if(p == null){
					Utils.sendMessage(sender, "The player \""+args[0]+"\" could not be found!");
					return;
				}
				runnable = new IPEventRunnable(p.getIp(), p.getUUID(), sender, IPEventRunnable.what.ADD);
			}
			Bukkit.getScheduler().runTaskAsynchronously(mcBans.getPlugin(), runnable);
		} else {
			throw new MissingArgumentsException("/banip <username/IP>");
		}
	}

    @Override
    public IPerm getPerm() {
        return Perm.Ban.BANIP;
    }

	@Override
	public String getCommand() {
		return "banip";
	}
}
