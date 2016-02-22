package org.infodancer.message;

import java.util.*;

public abstract class Message
{
	public static enum RecipientType { ALL, TO, CC, BCC };
	public static enum Flags { ANSWERED, DELETED, DRAFT, FLAGGED, RECENT, SEEN, USER };
	protected java.util.Map<String,java.util.LinkedList<Header>> headers;
	private EnumSet<Flags> msgflags;
	
	public Message()
	{
		headers = new java.util.HashMap<String,java.util.LinkedList<Header>>();
		msgflags = EnumSet.noneOf(Flags.class);
	}
	
	protected void addHeader(Header header)
	{
		String name = header.getName().toLowerCase();
		java.util.LinkedList<Header> localheaders = headers.get(name);
		if (localheaders != null) localheaders.add(header); 
		else 
		{
			localheaders = new java.util.LinkedList<Header>();
			localheaders.add(header);
			headers.put(name, localheaders);
		}
	}

	protected void setHeader(Header header)
	{
		String name = header.getName().toLowerCase();
		java.util.LinkedList<Header> localheaders = headers.get(name);
		if (localheaders != null) localheaders.clear();
		else localheaders = new java.util.LinkedList<Header>();
		localheaders.add(header);
		headers.put(name, localheaders);
	}
	
		
	public Header getHeader(String name)
	{
		java.util.LinkedList<Header> result = headers.get(name.toLowerCase());
		if (result != null) 
		{
			Header header = (Header) result.getFirst();
			return header;
		}
		else return null;		
	}	

	public List<Header> getHeaders(String name)
	{
		java.util.LinkedList<Header> result = headers.get(name.toLowerCase());
		if (result != null) 
		{
			return Collections.unmodifiableList(result);
		}
		else return null;		
	}	
	
	/** 
	 * Provides the first header whose name matches the argument.
	 **/
	 
	public String getHeaderValue(String name)
	{
		java.util.LinkedList<Header> result = headers.get(name.toLowerCase());
		if (result != null) 
		{
			Header header = (Header) result.getFirst();
			return header.getValue();
		}
		else return null;
	}
	
	/** 
	 * Provides a list of all headers whose name matches the argument.
	 **/
	 
	public Collection<String> getHeaderValues(String name)
	{
		java.util.LinkedList<Header> temp = headers.get(name.toLowerCase());
		if (temp != null) 
		{
			ArrayList<String> result = new ArrayList<String>(temp.size());
			for (Header header : temp)
			{
				result.add(header.getValue());
			}
			return result;
		}
		return null;
	}
	
	public EmailAddress getFrom() throws InvalidAddressException
	{
		AddressHeader header = (AddressHeader) getHeader("From");
		if (header != null) return header.getAddress();
		else return null;
	}
	
	public EmailAddress getSender() throws InvalidAddressException
	{
		AddressHeader header = (AddressHeader) getHeader("Sender");
		if (header != null) return header.getAddress();
		else return null;
	}
	
	public EmailAddress getReplyTo() throws InvalidAddressException
	{
		AddressHeader header = (AddressHeader) getHeader("Reply-To");
		if (header != null) return header.getAddress();
		else return null;
	}
		
	protected void addRecipient(RecipientType type, String address)
	throws InvalidAddressException
	{
		addRecipient(type, new EmailAddress(address));
	}

	protected void addRecipient(RecipientType type, EmailAddress address)
	throws InvalidAddressException
	{
		AddressHeader header = (AddressHeader) getHeader(type.toString());
		if (header != null) header.addAddress(address); 
		else setHeader(new AddressHeader(type.toString(), address));
	}

	public java.util.LinkedList<EmailAddress> getAllRecipients()
	throws InvalidAddressException
	{
		java.util.LinkedList<EmailAddress> result = new java.util.LinkedList<EmailAddress>();
		result.addAll(getRecipients(RecipientType.TO));
		result.addAll(getRecipients(RecipientType.CC));
		result.addAll(getRecipients(RecipientType.BCC));
		return result;
	}
	
	public java.util.List<EmailAddress> getRecipients(RecipientType type)
	throws InvalidAddressException
	{
		switch (type)
		{
			case TO:
			{
				java.util.List<EmailAddress> result = new java.util.LinkedList<EmailAddress>(); 
				java.util.List<Header> headerlist = getHeaders("TO");
				for (Header header : headerlist)
				{
					AddressHeader addheader = (AddressHeader) header;
					result.addAll(addheader.getAddresses());
				}
				
				return result;
			}

			case CC:
			{
				java.util.List<EmailAddress> result = new java.util.LinkedList<EmailAddress>(); 
				java.util.List<Header> headerlist = getHeaders("CC");
				for (Header header : headerlist)
				{
					AddressHeader addheader = (AddressHeader) header;
					result.addAll(addheader.getAddresses());
				}
				
				return result;
			}

			case BCC:
			{
				java.util.List<EmailAddress> result = new java.util.LinkedList<EmailAddress>(); 
				java.util.List<Header> headerlist = getHeaders("BCC");
				for (Header header : headerlist)
				{
					AddressHeader addheader = (AddressHeader) header;
					result.addAll(addheader.getAddresses());
				}
				
				return result;
			}
		}
		
		return null;
	}

	/**
	 * Provides the size of the message in bytes.
	 * @return the size in bytes of the message.
	 */
	public abstract long size();
	
	// Content methods

	/**
	 * Provides an InputStream containing the message's content.
	 * This method may not be as efficient as the writeTo() method.
	 * @return java.io.InputStream of the message's content.
	 */
	public abstract java.io.InputStream getInputStream() throws java.io.IOException;
	
	// Add more MIME methods later
	
	public void setFlag(Flags flag)
	{
		msgflags.add(flag);
	}
	
	public boolean isFlag(Flags flag)
	{
		return msgflags.contains(flag);
	}

	/**
	 * Deletes the message permanently.
	 */
	public abstract void delete();
}
