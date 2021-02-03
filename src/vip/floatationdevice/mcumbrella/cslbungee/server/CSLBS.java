package vip.floatationdevice.mcumbrella.cslbungee.server;

import net.md_5.bungee.api.plugin.*;

import java.net.*;
import java.io.*;
import java.util.*;
public class CSLBS extends Plugin
{
	int port=14514;
	public static CSLBS main;
	long round=0;
	static HashMap<String, Boolean> P = new HashMap<String, Boolean>();
	static HashSet<String> cmdWhitelist = new HashSet<String>();
	public void onEnable()
	{
		CSLBS.main=this;
		getLogger().info("Enabling.");
		try
		{
			File file=new File(this.getDataFolder().toPath()+File.separator+"cmdWhitelist.txt");
			if(!file.exists())
			{
				getLogger().warning("Missing command whitelist. Creating one");
				new File(this.getDataFolder().toPath().toString()).mkdir();
				file.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				bw.write("/login"+System.getProperty("line.separator")
					+"/register"+System.getProperty("line.separator")
					+"/l"+System.getProperty("line.separator")
					+"/reg"+System.getProperty("line.separator")
					+"/bindemail"+System.getProperty("line.separator")
					+"/bindmail"+System.getProperty("line.separator")
					+"/resetpassword"+System.getProperty("line.separator")
					+"/repw"+System.getProperty("line.separator")
					+"this_message-canbypass=CSL~Bungee`anytime"+System.getProperty("line.separator")
					+"/thisCMD"+System.getProperty("line.separator")
				);
				bw.flush();
				bw.close();
			}
			BufferedReader br = new BufferedReader(new FileReader("cmdWhitelist.txt"));
			String str;
			while((str = br.readLine()) != null)
			{
				cmdWhitelist.add(str);
			}
			br.close();
		}catch(Throwable e)
		{
			getLogger().warning("Error reading/creating command whitelist("+this.getDataFolder().toPath()+File.separator+"cmdWhitelist.txt"+"). Using default values. ("+e.toString()+")");
			cmdWhitelist.add("/login");
			cmdWhitelist.add("/register");
			cmdWhitelist.add("/l");
			cmdWhitelist.add("/reg");
			cmdWhitelist.add("/bindemail");
			cmdWhitelist.add("/bdmail");
			cmdWhitelist.add("/resetpassword");
			cmdWhitelist.add("/repw");
		}
		getProxy().getPluginManager().registerCommand(this, new CSLBSC("cslbs"));
		getProxy().getPluginManager().registerListener(this, new CSLBSE());
		new Thread("CSLBungee Server")
		{
			public void run()
			{
				try{
					@SuppressWarnings("resource")
					ServerSocket server = new ServerSocket(port);
					getLogger().info("CSLBungee server started.");
					System.out.println("CSLBungee server listening on port:"+port);
				    for(;;)
				    {
				    	//getLogger().info("Round "+round); DEBUG
				    	Socket socket = server.accept();
				    	
				    	
				    	new Thread("CSLBungee-Server Connection Processor")
				    	{
				    		boolean dataValid=true;
				    		public void run()
				    		{
				    			long thisRound=++round;
				    			getLogger().info("Round "+thisRound);
				    			
				    			/* Timer: used to detect if a round took too many time to process.
				    			 * Doesn't work as expected.
				    			 * Fortunately it doesn't seem to have much impact on this version of CSLBungee Server.
				    			 * Expected: Skip a round which took more than 1s to process.
				    			new Thread("CSLBungee-Server Connection Timer")
						    	{
						    		public void run()
						    		{
						    			//getLogger().info("Timer started"); DEBUG
						    			try {
											Thread.sleep(1000);
											if(!dataValid) {socket.close();getLogger().warning("Round "+thisRound+" took more 1s to process. Skipping");}
										} catch (Throwable e) {
											e.printStackTrace();
										}
						    		}
						    	}.start();
				    			*/
				    			
				    			InputStream inputStream = null;
								try {
									inputStream = socket.getInputStream();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							    byte[] bytes = new byte[64];
							    int len;
							    StringBuilder sb = new StringBuilder();
							    try
							    {
								    while ((len = inputStream.read(bytes)) != -1) {
									      sb.append(new String(bytes, 0, len,"UTF-8"));
										    if(sb.toString().startsWith("GET "))
										    {
										    	dataValid=false;
										    	try
										    	{
											    	OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8");
											    	osw.write("HTTP/1.1 400 Bad Request\r\n");
											    	osw.write("Server: CSLBungee-Server/1.0\r\n");
										            osw.write("Content-Type: text/html;charset=UTF-8\r\n");
										            osw.write("Transfer-Encoding: chunked\r\n");
										            osw.write("Date: Sat, 1 Jan 1921 00:00:01 GMT\r\n");
										            osw.write("\r\n");
										            osw.write("c9\r\n");
										            osw.write("<!DOCTYPE HTML>\r\n");
										            osw.write("<html><body><center><h1>CSLBungee Server V1.2 Running</h1><hr>"+new java.util.Date()+"</center></body></html>\r\n");
											    	osw.flush();
											    	osw.close();
											    	socket.close();
										    	}catch(Throwable e) {socket.close();}
										    	break;
										    }
										    else if(!sb.toString().startsWith("CSLBungee-Client-1.0"))
										    {
										    	//getLogger().warning("Bad data received: \n"+sb+"\n================================"); DEBUG
										    	socket.close();
										    	break;
										    }
										    else
										    {
										    	if(dataValid){
										    		socket.close();
												    String[] data=sb.toString().split("\r\n");
												    if(data.length!=3) {getLogger().warning("Bad data received:\n"+sb+"\n================================");break;}
											    	getLogger().info("CSLBungee Client connected");
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
												    else
												    {
												    	getLogger().warning("Bad data received:\n"+sb+"\n================================");
												    }
												    break;
										    	};break;
										    }
									    }

							    }catch(Throwable e) {}
							    getLogger().info("Round "+thisRound+" end");
				    		}
				    	}.start();
				    					    
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
