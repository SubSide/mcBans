package subside.plugins.mcbans.exceptions;

public class MissingArgumentsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5369260020257432145L;
	
	public MissingArgumentsException(String msg){
		super("Missing arguments! Usage: "+msg);
	}

}
