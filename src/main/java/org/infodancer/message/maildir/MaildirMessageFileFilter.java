package org.infodancer.message.maildir;

import java.io.File;
import java.io.FileFilter;

public class MaildirMessageFileFilter implements FileFilter
{
	public boolean accept(File file)
	{
		if (file.isDirectory()) return false;
		if (file.getName().startsWith(".")) return false;
		else return true;
	}
}
