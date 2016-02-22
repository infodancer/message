package org.infodancer.message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InternetMessageFactory
{
	/**
	 * Creates an in-memory Message from an inputstream containing an 
	 * RFC2822-compliant message.
	 * @return
	 * @throws IOException
	 */
	
	public Message createMessageFromRFC2822Stream(File file)
	throws IOException
	{
		return createMessageFromRFC2822Stream(new FileInputStream(file));
	}

	/**
	 * Creates an in-memory Message from an inputstream containing an 
	 * RFC2822-compliant message.
	 * @param input
	 * @return
	 * @throws IOException
	 */
	
	public Message createMessageFromRFC2822Stream(InputStream input)
	throws IOException
	{
		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new InputStreamReader(input));
			Message message = null;
			readMessageHeaders(message, reader);
			readMessageBody(message, reader);
			return message;
		}
				
		finally
		{
			try { if (reader != null) reader.close(); } catch (Exception e) { e.printStackTrace(); } 
		}
	}
	
	private static void readMessageHeaders(Message message, BufferedReader reader)
	throws IOException
	{
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			if (line.length() == 0) break;
			else
			{
				
			}
		}
	}

	private static void readMessageBody(Message message, BufferedReader reader)
	throws IOException
	{
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			
		}		
	}
}
