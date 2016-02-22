package org.infodancer.message;

import java.util.ArrayList;
import java.util.List;

public class MemoryMailbox implements Mailbox 
{
	ArrayList<MessageFolder> folders = new ArrayList<MessageFolder>();
	ArrayList<Message> messages = new ArrayList<Message>();
	
	@Override
	public void open() 
	{
	
	}

	@Override
	public void close() 
	{
	
	}

	@Override
	public List<Message> list() 
	{
		return messages;
	}

	@Override
	public List<MessageFolder> listFolders() 
	{
		return folders;
	}
}
