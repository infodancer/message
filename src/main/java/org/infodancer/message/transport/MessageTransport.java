package org.infodancer.message.transport;

import java.io.IOException;
import java.io.InputStream;

import org.infodancer.message.DeliveryException;
import org.infodancer.message.EmailAddress;
import org.infodancer.message.InvalidAddressException;
import org.infodancer.message.Message;

/**
 * Provides a client-oriented transport layer for Messages.
 **/ 

public interface MessageTransport
{
	/** 
	 * Send a message using the addressing information within the message itself.
	 **/
	 
	public void send(Message message) throws DeliveryException,IOException,InvalidAddressException;
	
	/** 
	 * Send a message with the provided envelope information.
	 **/
	
	public void send(EmailAddress sender, EmailAddress recipient, InputStream msgdata)
	throws DeliveryException;	
}
