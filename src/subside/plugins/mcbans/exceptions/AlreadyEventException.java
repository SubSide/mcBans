package subside.plugins.mcbans.exceptions;

import subside.plugins.mcbans.database.Event;

public class AlreadyEventException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -977909041941581373L;

	public AlreadyEventException(Event e){
		super("This player is already "+(e==Event.MUTE?"muted":(e==Event.BAN?"banned":"ipbanned"))+"!");
	}
}
