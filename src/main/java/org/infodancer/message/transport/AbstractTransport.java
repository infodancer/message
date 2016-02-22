package org.infodancer.message.transport;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.infodancer.message.DeliveryException;
import org.infodancer.message.EmailAddress;
import org.infodancer.message.InvalidAddressException;
import org.infodancer.message.Message;

/** 
 * Provides some utilities for an org.infodancer.mail.Transport implementation,
 * mainly allowing the Transport to closely emulate the behavior of the javax.mail.Transport
 * interface while still only implementing the MessageTransport interface
 * itself.
 * 
 * The attributes "smarthost" and "port" can be used.
 * "Port" refers to the remote port to which this transport will attempt to connect;
 * "smarthost" refers to the remote host itself, which is assumed to be able to handle
 * messages in a relay fashion.  If the smarthost will not relay, delivery failures
 * or bounces will result.
 * 
 * The default smarthost is null; the default port is -1.  Subclasses should check
 * for the default values and use defaults more appropriate for their protocol.   
 **/

public abstract class AbstractTransport implements MessageTransport
{
	private static Logger log = Logger.getLogger(AbstractTransport.class.getName());
	protected int port;
	protected String smarthost;
	
	public AbstractTransport()
	{
		this.port = -1;
		this.smarthost = null;
	}
	
	public void send(Message message) throws DeliveryException, InvalidAddressException, IOException
	{
		EmailAddress sender = message.getFrom();
		java.util.Collection<EmailAddress> recipients = message.getAllRecipients();
		send(sender, recipients, message.getInputStream());
	}

	public void send(EmailAddress sender, java.util.Collection<EmailAddress> recipients, InputStream input) throws DeliveryException
	{
		DeliveryException result = new DeliveryException("Combined DeliveryException");
		
		java.util.Iterator i = recipients.iterator();
		while (i.hasNext())
		{
			EmailAddress recipient = (EmailAddress) i.next();
			
			try
			{
				send(sender, recipient, input);
			}
			
			catch (DeliveryException e)
			{
				log.warning("Error delivering message to " + recipient + "!"); 
			}
		}
	}
}
