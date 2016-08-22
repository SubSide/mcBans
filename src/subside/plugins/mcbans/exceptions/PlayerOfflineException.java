package subside.plugins.mcbans.exceptions;

public class PlayerOfflineException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4325493794620728595L;
	
	public PlayerOfflineException(String type){
		super("You cannot "+type+" offline players!");
	}
}
