package subside.plugins.mcbans.exceptions;

public class NoPermissionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1726359907577897440L;
	
	public NoPermissionException(){
		super("You do net have the permissions for this command!");
	}

}
