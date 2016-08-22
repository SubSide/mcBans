package subside.plugins.mcbans.exceptions;

public class NeverJoinedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6536554596688905903L;
	
	public NeverJoinedException(){
		super("This player never joined!");
	}
}
