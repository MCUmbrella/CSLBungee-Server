package vip.floatationdevice.mcumbrella.cslbungee.server;
import net.md_5.bungee.api.plugin.*;
import java.net.*;
import java.io.*;
public class CSLBS extends Plugin
{
	int port=14514;
	public static CSLBS main;
	long round;
	boolean valid=false;
	public void onEnable()
	{
		CSLBS.main=this;
		getLogger().info("Enabling.");
		new Thread("CSLBungee Server")
		{
			public void run()
			{
				try{
					ServerSocket server = new ServerSocket(port);
					getLogger().info("CSLBungee server started.");
					System.out.println("CSLBungee server listening on port:"+port);
				    for(;;)
				    {
				    	round++;
				    	getLogger().info("Round "+round);
				    	Socket socket = server.accept();
				    	valid=true;
					    InputStream inputStream = socket.getInputStream();
					    byte[] bytes = new byte[1024];
					    int len;
					    StringBuilder sb = new StringBuilder();
					    while ((len = inputStream.read(bytes)) != -1) {
					      //指定编码格式UTF-8
					      sb.append(new String(bytes, 0, len,"UTF-8"));
						    if(sb.toString().startsWith("GET "))
						    {
						    	valid=false;
						    	getLogger().warning("Got HTTP request");
						    	try
						    	{
							    	OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8");
							    	osw.write("HTTP/1.1 200 OK\r\n");
							    	osw.write("Server: CSLBungee-Server/1.1\r\n");
						            osw.write("Content-Type: text/html;charset=UTF-8\r\n");
						            osw.write("Transfer-Encoding: chunked\r\n");
						            osw.write("Date: Thu, 7 Jan 2021 00:00:00 GMT\r\n");
						            osw.write("\r\n");
						            osw.write("c9\r\n");
						            osw.write("<!DOCTYPE HTML>\r\n");
						            osw.write("<html><body><center><h1>HTTP REQUEST NOT ALLOWED</h1><hr>CSLBungee-Server Version 1.0</center></body></html>\r\n");
						            osw.write("\r\n");
						            osw.write("\r\n");
						            osw.write("\r\n");
							    	osw.flush();
							    	osw.close();
							    	socket.close();
						    	}catch(Throwable e) {getLogger().warning("ERROR:");socket.close();server.close();}
						    	break;
						    }
						    else if(sb.toString().startsWith("DEBUGSHUTDOWN"))
						    {
						    	valid=false;
						    	getLogger().warning("REMOTE SHUTDOWN!");
						    	socket.close();
						    	System.exit(0);
						    	break;
						    }
						    else if(!sb.toString().startsWith("CSLBungee-Client-1.0"))
						    {
						    	valid=false;
						    	getLogger().warning("Bad data received: \n"+sb+"\n================================");
						    	socket.close();
						    	break;
						    }
					    }
					    if(valid)
					    {
					    	getLogger().info("Valid data received");
						    String[] data=sb.toString().split(",");
						    socket.close();
						    continue;
					    }
				    }
				}catch(Throwable e) {getLogger().warning("ERROR:");e.printStackTrace();}
			}
		}.start();
		getLogger().info("Enabled.");
	}
	public void onDisable()
	{
		getLogger().info("Disabled");
	}
}
