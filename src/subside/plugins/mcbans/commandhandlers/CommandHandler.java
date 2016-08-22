package subside.plugins.mcbans.commandhandlers;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import subside.plugins.mcbans.mcBans;
import subside.plugins.mcbans.exceptions.NoPermissionException;
import subside.plugins.mcbans.utils.Utils;

public class CommandHandler implements CommandExecutor {
	public static ArrayList<AbstractCommand> commands = new ArrayList<AbstractCommand>();

	public void initialize(mcBans plugin) {
		commands.add(new BanCommand());
		commands.add(new TempbanCommand());
		commands.add(new UnbanCommand());
		commands.add(new MuteCommand());
		commands.add(new TempmuteCommand());
		commands.add(new UnmuteCommand());
		commands.add(new HistoryCommand());
		commands.add(new IPBanCommand());
		commands.add(new BanIPCommand());
		commands.add(new IPUnbanCommand());
		commands.add(new UnbanIPCommand());
		commands.add(new EventLogCommand());

		for (AbstractCommand cmd : commands) {
			plugin.getCommand(cmd.getCommand()).setExecutor(this);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		try {
			AbstractCommand command = null;
			for (AbstractCommand comm : commands) {
				if (cmd.getName().equalsIgnoreCase(comm.getCommand())) {
					command = comm;
				}
			}

			if (command.getPerm().has(sender)) {

				command.execute(sender, args);

			} else {
				throw new NoPermissionException();
			}

		}
		catch (Exception e) {
			// e.printStackTrace();
			Utils.sendMessage(sender, e.getMessage());
		}

		return true;
	}

}
