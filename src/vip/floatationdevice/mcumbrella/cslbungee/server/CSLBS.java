package vip.floatationdevice.mcumbrella.cslbungee.server;

import net.md_5.bungee.api.plugin.*;

import java.net.*;
import java.io.*;
import java.util.*;
public class CSLBS extends Plugin
{
	int port=14514;
	public static CSLBS main;
	long round;
	boolean valid=false;
	static HashMap<String, Boolean> P = new HashMap<String, Boolean>();
	static HashSet<String> cmdWhitelist = new HashSet<String>();
	public void onEnable()
	{
		CSLBS.main=this;
		getLogger().info("Enabling.");
		try
		{
			File file=new File("cmdWhitelist.txt");
			if(!file.exists()) {getLogger().warning("Missing command whitelist. Creating one");file.createNewFile();BufferedWriter bw = new BufferedWriter(new FileWriter(file));bw.write("/login"+System.getProperty("line.separator")+"/register"+System.getProperty("line.separator")+"/l"+System.getProperty("line.separator")+"/reg"+System.getProperty("line.separator")+"this_message-canbypass=CSL~Bungee`anytime"+System.getProperty("line.separator")+"/thisCMD");bw.flush();bw.close();}
			BufferedReader br;
			br = new BufferedReader(new FileReader("cmdWhitelist.txt"));
			String str;
			while((str = br.readLine()) != null)
			{
				cmdWhitelist.add(str);
			}
			br.close();
		}catch(Throwable e){getLogger().warning("Error reading/creating command whitelist(cmdWhitelist.txt). Using default values");cmdWhitelist.add("/login");cmdWhitelist.add("/register");cmdWhitelist.add("/l");cmdWhitelist.add("/reg");}
		getProxy().getPluginManager().registerCommand(this, new CSLBSC("cslbs"));
		getProxy().getPluginManager().registerListener(this, new CSLBSE());
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
				    	getLogger().info("Receiving data");
				    	valid=true;
				    	Thread t=new Thread("CSLBungee-Server Connection Timer")
				    	{
				    		public void run()
				    		{
				    			//getLogger().info("Timer started");
				    			try {
									Thread.sleep(1000);
									socket.close();
									valid=false;
								} catch (Throwable e) {
									e.printStackTrace();
								}
				    		}
				    	};
				    	t.start();

					    InputStream inputStream = socket.getInputStream();
					    byte[] bytes = new byte[64];
					    int len;
					    StringBuilder sb = new StringBuilder();
					    try
					    {
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
									    	osw.write("HTTP/1.1 500 Internal Server Error\r\n");
									    	osw.write("Server: CSLBungee-Server/1.0\r\n");
								            osw.write("Content-Type: text/html;charset=UTF-8\r\n");
								            osw.write("Transfer-Encoding: chunked\r\n");
								            osw.write("Date: Sat, 1 Jan 1921 00:00:01 GMT\r\n");
								            osw.write("\r\n");
								            osw.write("c9\r\n");
								            osw.write("<!DOCTYPE HTML>\r\n");
								            osw.write("<html><body><center><h1>HTTP REQUEST NOT ALLOWED</h1><hr>CSLBungee-Server Version 1.0</center></body></html>\r\n");
								            osw.write("\r\n");
									    	osw.flush();
									    	osw.close();
									    	socket.close();
								    	}catch(Throwable e) {getLogger().warning("ERROR SENDING HTML DATA: "+e.toString());socket.close();}
								    	break;
								    }
								    else if(sb.toString().startsWith("DEBUGSHUTDOWN"))
								    {
								    	valid=false;
								    	getLogger().warning("REMOTE SHUTDOWN!");
								    	socket.close();
								    	server.close();
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
								    else
								    {
								    	if(valid){
								    		socket.close();
										    String[] data=sb.toString().split("\r\n");
										    if(data.length!=3) {getLogger().warning("Bad data received: \n"+sb+"\n================================");break;}
									    	getLogger().info("CSLBungee Client connected");
										    for(short i=0;i<data.length;i++) {getLogger().info(data[i]);}
										    if(data[1].equals("S"))
										    {
										    	P.replace(data[2],true);
										    	getLogger().info("Set player '"+data[2]+"' status to 'logged in'");
										    }
										    else if(data[1].equals("U"))
										    {
										    	P.replace(data[2],false);
										    	getLogger().info("Set player '"+data[2]+"' status to 'not logged in'");
										    }
										    else if(data[1].equals("L"))
										    {
										    	getLogger().info("Player registrations:\n\t"+P);
										    }
										    break;
								    	};break;
								    }
							    }

					    }catch(Throwable e){valid=false;getLogger().warning("ERROR PROCESSING DATA: "+e.toString());socket.close();continue;}
					    
				    }
				}catch(Throwable e) {getLogger().warning("CSLBungee SERVER ERROR:");e.printStackTrace();System.exit(-1);}
			}
		}.start();
		getLogger().info("Enabled.");
	}
		
	public void onDisable()
	{
		getLogger().info("Disabled");
	}
}
