package vip.floatationdevice.mcumbrella.cslbungee.server;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSLBServerMain extends Plugin
{
    int port = 14514;
    public static CSLBServerMain main;
    static HashMap<String, Boolean> playerLoginStatusMap = new HashMap<String, Boolean>();
    static HashSet<String> cmdWhitelist = new HashSet<String>();

    public void onEnable()
    {
        CSLBServerMain.main = this;
        getLogger().info("Enabling.");
        try
        {
            File file = new File(this.getDataFolder().toPath() + File.separator + "cmdWhitelist.txt");
            if(!file.exists())
            {
                getLogger().warning("Missing command whitelist. Creating one");
                new File(this.getDataFolder().toPath().toString()).mkdir();
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write("/login" + System.getProperty("line.separator")
                        + "/register" + System.getProperty("line.separator")
                        + "/l" + System.getProperty("line.separator")
                        + "/reg" + System.getProperty("line.separator")
                        + "/bindemail" + System.getProperty("line.separator")
                        + "/bindmail" + System.getProperty("line.separator")
                        + "/resetpassword" + System.getProperty("line.separator")
                        + "/repw" + System.getProperty("line.separator")
                        + "this_message-canbypass=CSL~Bungee`anytime" + System.getProperty("line.separator")
                        + "/thisCMD" + System.getProperty("line.separator")
                );
                bw.flush();
                bw.close();
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            while((str = br.readLine()) != null)
            {
                cmdWhitelist.add(str);
            }
            br.close();
        }
        catch(Throwable e)
        {
            getLogger().severe("Error reading/creating command whitelist(" + this.getDataFolder().toPath() + File.separator + "cmdWhitelist.txt" + "). Using default values. (" + e.toString() + ")");
            cmdWhitelist.add("/login");
            cmdWhitelist.add("/register");
            cmdWhitelist.add("/l");
            cmdWhitelist.add("/reg");
            cmdWhitelist.add("/bindemail");
            cmdWhitelist.add("/bdmail");
            cmdWhitelist.add("/resetpassword");
            cmdWhitelist.add("/repw");
        }
        getProxy().getPluginManager().registerCommand(this, new CSLBServerCommand("cslbs"));
        getProxy().getPluginManager().registerListener(this, new CSLBServerEventListener());
        new Thread("CSLBungee Server")
        {
            public void run()
            {
                try
                {
                    @SuppressWarnings("resource")
                    ServerSocket server = new ServerSocket(port);
                    getLogger().info("CSLBungee server started.");
                    System.out.println("CSLBungee server listening on port " + port);
                    for(; ; )
                    {
                        Socket socket = server.accept();
                        new Thread("CSLBungee-Server Connection Processor")
                        {
                            public void run()
                            {
                                InputStream inputStream;
                                try
                                {
                                    inputStream = socket.getInputStream();
                                }
                                catch(Throwable e)
                                {
                                    e.printStackTrace();
                                    return;
                                }
                                byte[] bytes = new byte[64];
                                int len;
                                StringBuilder sb = new StringBuilder();
                                try
                                {
                                    if((len = inputStream.read(bytes)) != -1)
                                    {
                                        sb.append(new String(bytes, 0, len, StandardCharsets.UTF_8));
                                        if(sb.toString().startsWith("CSLBungee-Client-1.0"))
                                        {
                                            socket.close();
                                            String[] data = sb.toString().split("\r\n");
                                            if(data.length != 3)
                                            {
                                                getLogger().warning("Bad data received:" + sb);
                                                return;
                                            }
                                            if(data[1].equals("S"))
                                            {
                                                playerLoginStatusMap.replace(data[2], true);
                                                getLogger().info("Set player '" + data[2] + "' status to 'logged in'");
                                            }
                                            else if(data[1].equals("U"))
                                            {
                                                playerLoginStatusMap.replace(data[2], false);
                                                getLogger().info("Set player '" + data[2] + "' status to 'not logged in'");
                                            }
                                            else getLogger().warning("Bad data received:\n" + sb);
                                        }
                                        else socket.close();
                                    }
                                }
                                catch(Throwable e) {}
                            }
                        }.start();
                    }
                }
                catch(Throwable e)
                {
                    e.printStackTrace();
                    BungeeCord.getInstance().stop("CSLBungee Server thread error");
                }
            }
        }.start();
        getLogger().info("Enabled.");
    }

    public void onDisable()
    {
        getLogger().info("Disabled");
    }
}
