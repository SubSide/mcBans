package subside.plugins.mcbans.exceptions;

import subside.plugins.mcbans.database.Event;

public class NotEventException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3631923122477679006L;

	public NotEventException(Event e){
		super("This player is not "+((e == Event.MUTE)?"muted":"banned")+"!");
	}
}
