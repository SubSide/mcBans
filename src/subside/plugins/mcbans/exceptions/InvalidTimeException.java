package subside.plugins.mcbans.exceptions;

public class InvalidTimeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4702164742521800677L;
	
	public InvalidTimeException(){
		super("This is not a valid time format! (1w2d3h4m5s)");
	}

}
