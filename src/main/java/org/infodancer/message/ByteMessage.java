package org.infodancer.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteMessage extends Message
{
	byte[] data;
	
	public ByteMessage(byte[] data)
	{
		this.data = data;
	}
	
	@Override
	public long size()
	{
		return data.length;
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return new ByteArrayInputStream(data);
	}

	/** 
	 * Deleting a ByteMessage is meaningless.
	 * Try deleting from a folder instead.
	 */
	@Override
	public void delete()
	{
		
	}
}
