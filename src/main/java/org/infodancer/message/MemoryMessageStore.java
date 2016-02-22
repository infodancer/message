package org.infodancer.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores mailboxes in memory, primarily for test purposes.
 * @author matthew
 */

public class MemoryMessageStore implements MessageStore 
{
	Map<String,Mailbox> map = new ConcurrentHashMap<String,Mailbox>();
	
	@Override
	public Mailbox getMailbox(String username) 
	{
		Mailbox result = map.get(username);
		if (result == null)
		{
			result = new MemoryMailbox();
			map.put(username, result);
		}
		return result;
	}
}
