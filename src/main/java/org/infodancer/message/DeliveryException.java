package org.infodancer.message;

/** 
 * This class is designed to provide detailed information on the 
 * results of a delivery attempt, including individualized result status, 
 * temporary versus permanent failures, and explanatory error messages.
 **/
 
public class DeliveryException extends Exception
{
	boolean permanent;
	java.util.Map<EmailAddress,DeliveryStatus> statusmap;
	
	public DeliveryException(Throwable e)
	{
		super(e);
		this.permanent = false;
		statusmap = new java.util.HashMap<EmailAddress,DeliveryStatus>();
	}
	
	public DeliveryException(String msg)
	{
		super(msg);
		this.permanent = false;
		statusmap = new java.util.HashMap<EmailAddress,DeliveryStatus>();
	}
	
	public DeliveryException(DeliveryStatus status)
	{
		this.permanent = status.isPermanent();
		statusmap = new java.util.HashMap<EmailAddress,DeliveryStatus>();
		addDeliveryStatus(status);
	}
	
	public java.util.Collection<DeliveryStatus> getDeliveryStatus()
	{
		return statusmap.values();
	}
	
	public DeliveryStatus getDeliveryStatus(EmailAddress recipient)
	{
		return statusmap.get(recipient);
	}
	
	public void addDeliveryStatus(DeliveryStatus status)
	{
		statusmap.put(status.recipient, status);
	}
	
	public boolean isPermanent()
	{
		return permanent;
	}
}
