package org.infodancer.message;

import java.io.*;
import java.nio.charset.*;
import java.nio.channels.*;
import java.util.logging.Logger;

/** 
 * Provides a file-based implementation of a Message.
 **/
 
public class FileMessage extends Message
{
	private static Logger log = Logger.getLogger(FileMessage.class.getName());
	protected Charset charset = Charset.forName("US-ASCII");
	protected java.io.File msgFile;
		
	/** 
	 * Provides a Message implementation using the specified file for storage.
	 * This is a reasonable implementation for messages of potentially-large
	 * size, as the message is never stored completely in memory but is processed
	 * only in streams.
	 **/

	public FileMessage(java.io.File msgFile)	
	{
		super();
		this.msgFile = msgFile;
	}

	public ReadableByteChannel loadFile() throws java.io.IOException
	{
		FileChannel readChannel = new java.io.FileInputStream(msgFile).getChannel();
		return readChannel;
	}
	
	/**
	 * Writes the message to the provided WritableByteChannel as efficiently as possible.
	 * @throws java.io.IOException
	 */

	public void writeTo(WritableByteChannel writeChannel) throws java.io.IOException
	{
		FileChannel readChannel = null;
		
		try
		{
			long position = 0;
			long length = msgFile.length();
			readChannel = new java.io.FileInputStream(msgFile).getChannel();
			while (position < length) position += readChannel.transferTo(position, length - position, writeChannel);
		}		
		
		finally
		{
			try { if (readChannel != null) readChannel.close(); } catch (Exception e) { } 
		}
	}
	
	public java.io.File getFile()
	{
		return msgFile;
	}
	
	public long size()
	{
		return msgFile.length();
	}

	public InputStream getInputStream() throws IOException 
	{
		return new FileInputStream(msgFile);
	}
	
	public void delete()
	{
		msgFile.delete();
	}
}
