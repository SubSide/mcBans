package subside.plugins.mcbans.exceptions;

public class NotProcessedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2383264317886017542L;
	
	public NotProcessedException(){
		super("Please wait a moment before saying something in chat, if this persist please contact SubSide or an admin!");
	}
}
