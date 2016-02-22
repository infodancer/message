package org.infodancer.message.maildir;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

import org.infodancer.message.DeliveryException;

public class MaildirDeliveryChannel
{
	private static Logger log = Logger.getLogger(MaildirDeliveryChannel.class.getName());
	protected MaildirFolder maildir;
	protected File tmpfile;
	protected File msgfile;
	protected String uniquePart;
	protected FileChannel channel;
	
	public MaildirDeliveryChannel(MaildirFolder maildir) throws java.io.IOException
	{
		this.maildir = maildir;
		this.tmpfile = maildir.createTempFile();
		this.msgfile = maildir.createMessageFile(tmpfile);
		this.channel = new FileOutputStream(tmpfile).getChannel();
	}
	
	public long transferFrom(java.nio.channels.ReadableByteChannel input, long size)
	throws java.io.IOException
	{
		long position = channel.position();
		long length = channel.transferFrom(input, position, size);
		channel.position(position + length);
		return length;
	}
	
	public int write(java.nio.ByteBuffer buffer) throws java.io.IOException
	{
		return channel.write(buffer);
	}
	
	public void abort()
	{
		tmpfile.delete();
		msgfile.delete();
	}

	/** 
	 * Provides the filename for the final, delivered file.  This method can be called 
	 * at any time after the output stream is created, but the file specified will not 
	 * actually exist until the close() method is called and should not be considered 
	 * as reliably existing in the presence of other processes or threads reading the
	 * maildir.  This method will not throw an exception if called subsequent to close().  
	 **/
	 
	public java.io.File getFile()
	{
		return msgfile;
	}
	
	public void commit() throws DeliveryException
	{
		try
		{
			channel.force(true);
			channel.close();
			tmpfile.renameTo(msgfile);
		}
		
		catch (Exception e)
		{
			log.warning("Exception commiting maildir delivery!");
			throw new DeliveryException(e);
		}
	}
	
	public void close() throws java.io.IOException
	{
		if (channel.isOpen()) channel.close();
	}
	
	public boolean isOpen()
	{
		return channel.isOpen();
	}
}
