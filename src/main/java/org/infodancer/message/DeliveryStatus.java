package org.infodancer.message;

public class DeliveryStatus
{
	boolean permanent;
	String status;
	EmailAddress recipient;
	
	public DeliveryStatus(EmailAddress recipient, boolean permanent)
	{
		this.recipient = recipient;
		this.permanent = permanent;
	}

	public DeliveryStatus(EmailAddress recipient, boolean permanent, String status)
	{
		this.recipient = recipient;
		this.permanent = permanent;
	}
	
	public boolean isPermanent()
	{
		return permanent;
	}

}
