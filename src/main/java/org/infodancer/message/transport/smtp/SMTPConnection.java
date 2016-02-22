package org.infodancer.message.transport.smtp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import org.infodancer.message.DeliveryException;
import org.infodancer.message.EmailAddress;

public class SMTPConnection
{
	Long size;	
	boolean extended;
	boolean starttls;
	boolean pipelining;
	boolean auth;
	boolean authPlain;
	boolean authLogin;
	boolean authCram;
	String localname;
	Socket socket;
	SSLSocket sslSocket;
	BufferedReader reader;
	BufferedWriter writer;
	SMTPTransport transport;
	
	public SMTPConnection(SMTPTransport transport, Socket socket) throws IOException
	{
		this.transport = transport;
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.localname = socket.getLocalAddress().getCanonicalHostName();
		parseGreeting(readLine());
	}

	public SMTPConnection(SMTPTransport transport, SSLSocket socket) throws IOException
	{
		this.transport = transport;
		this.sslSocket = socket;
		this.reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
		this.writer = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream()));
		this.localname = sslSocket.getLocalAddress().getCanonicalHostName();
		parseGreeting(readLine());
	}
	
	public SMTPConnection(SMTPTransport transport, InputStream input, OutputStream output) throws IOException
	{
		this.transport = transport;
		this.reader = new BufferedReader(new InputStreamReader(input));
		this.writer = new BufferedWriter(new OutputStreamWriter(output));
		this.localname = "localhost";
		parseGreeting(readLine());
	}

	public boolean isPipeliningSupported()
	{
		return pipelining;
	}

	public void setPipeliningSupported(boolean supported)
	{
		this.pipelining = supported;
	}

	public boolean isStartTLSSupported()
	{
		return starttls;
	}
	
	public void setStartTLSSupported(boolean supported)
	{
		this.starttls = supported;
	}

	public boolean isAuthSupported()
	{
		return auth;
	}

	public void setAuthSupported(boolean supported)
	{
		this.auth = supported;
	}

	public boolean isAuthPlainSupported()
	{
		return authPlain;
	}

	public void setAuthPlainSupported(boolean supported)
	{
		this.authPlain = supported;
	}

	public boolean isAuthLoginSupported()
	{
		return authLogin;
	}

	public void setAuthLoginSupported(boolean supported)
	{
		this.authLogin = supported;
	}
	
	public boolean isAuthCramSupported()
	{
		return authCram;
	}
	
	public void setAuthCramSupported(boolean supported)
	{
		this.authCram = supported;
	}
	
	public void close()
	{
		try { if (reader != null) reader.close(); } catch (Exception e) { } 
		try { if (writer != null) writer.close(); } catch (Exception e) { }
		try { if (socket != null) socket.close(); } catch (Exception e) { }
		try { if (sslSocket != null) sslSocket.close(); } catch (Exception e) { }
	}
	
	public boolean sendRecipient(EmailAddress recipient) throws IOException
	{
		writeLine("RCPT TO:<" + recipient + ">");
		return parseRCPTTO(reader.readLine());
	}

	public boolean parseRCPTTO(String line)
	{
		if (line.startsWith("250")) return true;
		else return false;
	}

	public boolean parseQUIT(String line)
	{
		if (line.startsWith("250")) return true;
		else return false;
	}

	public boolean sendSender(EmailAddress sender) throws IOException
	{
		writeLine("MAIL FROM:<" + sender + ">");
		return parseMAILFROM(reader.readLine());
	}
	
	public boolean parseMAILFROM(String line)
	{
		if (line.startsWith("250")) return true;
		else return false;
	}
	
	public boolean isExtended()
	{
		return extended;
	}

	public void setExtended(boolean extended)
	{
		this.extended = extended;
	}

	public BufferedReader getReader()
	{
		return reader;
	}

	public void setReader(BufferedReader reader)
	{
		this.reader = reader;
	}

	public BufferedWriter getWriter()
	{
		return writer;
	}

	public void setWriter(BufferedWriter writer)
	{
		this.writer = writer;
	}

	public void sendHelo() throws IOException
	{
		if (extended)
		{
			writeLine("EHLO " + localname);
			if (!parseEHLO())
			{
				sendQuit();
				close();
			}
		}
		else
		{
			writeLine("HELO " + localname);
			if (!parseHELO(readLine())) 
			{
				sendQuit();
				close();
			}
		}
		
	}
	
	boolean parseHELO(String line)
	{
		if (line.startsWith("250 ")) return true;
		else return false;
	} 

	boolean parseEHLO() throws IOException
	{
		String line = null;
		while ((line = readLine()) != null)
		{
			if (line.startsWith("250-")) 
			{
				parseEHLOLine(line);
			}
			else if (line.startsWith("250 "))
			{
				parseEHLOLine(line);
				return true;
			}
			else return false;
		}
		return false;
	}

	boolean parseEHLOLine(String line)
	{
		if ((line != null) && (line.length() >= 4))
		{
			String option = line.substring(4, line.length());
			if ("PIPELINING".equalsIgnoreCase(option))
			{
				pipelining = true;
			}
			else if ("STARTTLS".equalsIgnoreCase(option))
			{
				starttls = true;
			}
			else if (option.length() >= 4) 
			{	
				if ("SIZE".equalsIgnoreCase(option.substring(0, 4)))
				{
					String[] ssize = option.split(" ");
					if (ssize.length > 1)
					{
						String value = ssize[1];
						
					}
				}
				else if ("AUTH".equalsIgnoreCase(option.substring(0, 4)))
				{
					auth = true;
					String[] split = option.split(" ");
					for (String s : split)
					{
						if ("LOGIN".equalsIgnoreCase(s)) authLogin = true;
						else if ("PLAIN".equalsIgnoreCase(s)) authPlain = true;
						else if ("CRAM-MD5".equalsIgnoreCase(s)) authCram = true;
					}
				}
			}
		}
		return true;
	}
	
	public String readLine() throws IOException
	{
		return reader.readLine();
	}
	
	public void writeLine(String line) throws IOException
	{
		writer.write(line);
		writer.write("\r\n");
		writer.flush();
	}
	
	public boolean parseGreeting(String line)
	{
		if (line.startsWith("220 ")) return true;
		else return false;
	}

	public void parseDataBegin(String line) throws DeliveryException
	{
		if (!line.startsWith("354 "))
		{
			DeliveryException e = new DeliveryException(line);
			
			throw e;
		}
	}

	public void parseDataEnd(String line) throws DeliveryException
	{
		if (!line.startsWith("250 "))
		{
			DeliveryException e = new DeliveryException(line);
			
			throw e;			
		}
	}

	/**
	 * Sends the DATA command, followed by the message data and the end-of-message indicator.
	 * Failures, whether permanent or temporary, will produce a DeliveryException.
	 * @param data
	 * @throws IOException
	 * @throws DeliveryException
	 */
	public void sendData(byte[] data) throws IOException, DeliveryException
	{
		writeLine("DATA");
		parseDataBegin(readLine());
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			if (line.startsWith(".")) writeLine("." + line);
			else writeLine(line);
		}
		reader.close();
		writeLine(".");
		parseDataEnd(readLine());
	}

	/**
	 * Sends the DATA command, followed by the message data and the end-of-message indicator.
	 * Failures, whether permanent or temporary, will produce a DeliveryException.
	 * @throws IOException
	 * @throws DeliveryException
	 */
	public void sendData(InputStream input) throws IOException, DeliveryException
	{
		writeLine("DATA");
		parseDataBegin(readLine());
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			if (line.startsWith(".")) writeLine("." + line);
			else writeLine(line);
		}
		reader.close();
		writeLine("");
		writeLine(".");
		parseDataEnd(readLine());
	}

	public boolean sendQuit() throws IOException
	{
		writeLine("QUIT");
		return parseQUIT(reader.readLine());
	}
}
