package org.infodancer.message.transport.smtp;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.naming.NamingException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.infodancer.message.DeliveryException;
import org.infodancer.message.DeliveryStatus;
import org.infodancer.message.EmailAddress;
import org.infodancer.message.Message;
import org.infodancer.message.transport.AbstractTransport;
import org.infodancer.message.transport.MX;
import org.infodancer.message.transport.MessageTransport;
import org.infodancer.message.transport.TLSSocketFactory;

public class SMTPTransport extends AbstractTransport implements MessageTransport 
{
	private SocketFactory socketFactory;
	private SSLSocketFactory sslSocketFactory;
	private String resolver;
	
	/** 
	 * Determines the host to which email is delivered by default. 
	 * If a smarthost is defined, all mail will be delivered to the defined host.
	 * No MX lookups or other routing will be attempted. 
	 **/
	private String smarthost;
	
	/** 
	 * Username to use when authenticating via SMTP-AUTH.
	 */
	private String user;
	
	/**
	 * Password to use when authenticating via SMTP-AUTH.
	 */
	private String password;

	/** 
	 * The port number to use for SMTP; defaults to 25.
	 */
	private int port;
	
	X509TrustManager unsignedTrustManager = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain,
                String authType) throws CertificateException {
            // Don't do anything.
        }

        public void checkServerTrusted(X509Certificate[] chain,
                String authType) throws CertificateException {
            // Don't do anything.
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
        }
    };
	
	public SMTPTransport()
	{
		this.port = 25;
		this.user = null;
		this.password = null;
		this.smarthost = null;
		
		this.socketFactory = SocketFactory.getDefault();
		this.sslSocketFactory = initializeSSL();
	}
	
	private SSLSocketFactory initializeSSL()
	{
		try
		{
			java.lang.System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
			SSLContext sslContext = SSLContext.getInstance("SSLv3");
			sslContext.init(null, new TrustManager[] { unsignedTrustManager }, new SecureRandom());
			SSLSocketFactory factory = new TLSSocketFactory(sslContext);			
			return factory;
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String getResolver()
	{
		return resolver;
	}

	public void setResolver(String resolver)
	{
		this.resolver = resolver;
	}

	public String getUser() 
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}

	public int getPort() 
	{
		return port;
	}

	public void setPort(int port) 
	{
		this.port = port;
	}

	public String getSmarthost()
	{
		return smarthost;
	}

	public void setSmarthost(String smarthost) 
	{
		this.smarthost = smarthost;
	}
	
	public boolean send(SMTPConnection con, EmailAddress sender, EmailAddress recipient, InputStream input) 
	throws IOException, DeliveryException
	{
		con.sendHelo();
		con.sendSender(sender);
		con.sendRecipient(recipient);
		con.sendData(input);
		con.sendQuit();
		return true;
	}

	public boolean send(String hostname, EmailAddress sender, EmailAddress recipient, InputStream input, boolean requireSSL) 
	throws UnknownHostException, IOException, DeliveryException
	{
		Socket socket = null;
		SSLSocket sslsocket = null;
		SMTPConnection con;
		
		try
		{
			// Try SSL first
			sslsocket = (SSLSocket) sslSocketFactory.createSocket(hostname, 465);
			con = new SMTPConnection(this, sslsocket);
			send(con, sender, recipient, input);
			return true;
		}
		
		catch (Exception e)
		{
			if (!requireSSL)
			{
				// If SSL fails, try startTLS
				socket = socketFactory.createSocket(hostname, 25);
				con = new SMTPConnection(this, socket);
				send(con, sender, recipient, input);
				return true;
			}
			else
			{
				e.printStackTrace();
				DeliveryStatus status = new DeliveryStatus(recipient, false);
				throw new DeliveryException(status);
			}
		}

		finally
		{
			try { if (sslsocket != null) sslsocket.close(); } catch (Exception e) { }
			try { if (socket != null) socket.close(); } catch (Exception e) { } 
		}		
	}
		
	public void send(EmailAddress sender, EmailAddress recipient, InputStream input) throws DeliveryException 
	{
		if ((smarthost != null) && (smarthost.trim().length() > 0))
		{
			try
			{
				send(smarthost, sender, recipient, input, false);
			}
			
			catch (UnknownHostException e)
			{
				e.printStackTrace();
				DeliveryStatus status = new DeliveryStatus(recipient, false, e.getMessage());
				throw new DeliveryException(status);
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
				DeliveryStatus status = new DeliveryStatus(recipient, false, e.getMessage());
				throw new DeliveryException(status);
			}
		}
		else
		{
			try
			{
				// Do MX lookups here
				MX[] mxlist = MX.mxlookup(recipient);
				for (MX mx : mxlist)
				{
					boolean result = send(mx.host, sender, recipient, input, false);
					if (result) break;
				}
			}

			catch (IOException e)
			{
				e.printStackTrace();
				DeliveryStatus status = new DeliveryStatus(recipient, false, e.getMessage());
				throw new DeliveryException(status);
			}

			catch (NamingException e)
			{
				e.printStackTrace();
				DeliveryStatus status = new DeliveryStatus(recipient, false, e.getMessage());
				throw new DeliveryException(status);				
			}
		}
	}
}
