package subside.plugins.mcbans.exceptions;

public class PlayerExcludedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7468990745118676764L;

	public PlayerExcludedException(String type){
		super("You cannot "+type+" this player!");
	}
}
