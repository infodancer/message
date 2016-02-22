package org.infodancer.message.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class TLSSocketFactory extends SSLSocketFactory
{
	private final javax.net.ssl.SSLSocketFactory socketfactory;
	private final SSLContext sslContext;
	
	public TLSSocketFactory(SSLContext sslContext) 
	{
		this.sslContext = sslContext;
	    this.socketfactory = sslContext.getSocketFactory();
	}

	public Socket createSocket() throws IOException 
	{
	    SSLSocket socket = (SSLSocket) super.createSocket();
	    socket.setEnabledProtocols(new String[] {"SSLv3, TLSv1"});
	    return socket;
	}

	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException 
	{
	    SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(socket, host, port, autoClose);
	    sslSocket.setEnabledProtocols(new String[] {"SSLv3", "TLSv1"});
	    return sslSocket;
	}

	@Override
	public String[] getDefaultCipherSuites()
	{
		SSLParameters params = sslContext.getDefaultSSLParameters();
		return params.getCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites()
	{
		SSLParameters params = sslContext.getDefaultSSLParameters();
		return params.getCipherSuites();
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException
	{
	    SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(host, port);
	    sslSocket.setEnabledProtocols(new String[] {"SSLv3", "TLSv1"});
	    return sslSocket;
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException
	{
	    SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(host, port);
	    sslSocket.setEnabledProtocols(new String[] {"SSLv3", "TLSv1"});
	    return sslSocket;
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException
	{
	    SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(host, port, localHost, localPort);
	    sslSocket.setEnabledProtocols(new String[] {"SSLv3", "TLSv1"});
	    return sslSocket;
	}

	@Override
	public Socket createSocket(InetAddress address, int port,
			InetAddress localAddress, int localPort) throws IOException
	{
	    SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(address, port, localAddress, localPort);
	    sslSocket.setEnabledProtocols(new String[] {"SSLv3", "TLSv1"});
	    return sslSocket;
	}
}
