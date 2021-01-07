package vip.floatationdevice.mcumbrella.cslbungee.server;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.*;

public class CSLBSC extends Command {

	public CSLBSC(String name) {
		super("cslbs");
	}

	@Override
	public void execute(CommandSender s, String[] args)
	{
		if(s.hasPermission("cslbs.main")&&args.length!=0)
		{
			if(args[0].toLowerCase().equals("t"))
			{s.sendMessage(new ComponentBuilder("Hello world!").color(ChatColor.GREEN).create());}
			else if(args[0].equals("l"))
			{
				s.sendMessage(new ComponentBuilder("List of recorded players:\n\t"+CSLBS.P).color(ChatColor.GREEN).create());
			}
			else if(args[0].toLowerCase().equals("s")&&args.length==2)
			{
				CSLBS.P.put(args[1],true);
				s.sendMessage(new ComponentBuilder("Set '"+args[1]+"'s status to 'logged in'").color(ChatColor.GREEN).create());
			}
			else if(args[0].toLowerCase().equals("u")&&args.length==2)
			{
				CSLBS.P.put(args[1],false);
				s.sendMessage(new ComponentBuilder("Set '"+args[1]+"'s status to 'not logged in'").color(ChatColor.GREEN).create());
			}
			else
			{
				s.sendMessage(new ComponentBuilder("Available subcommands: t(test command), l(get a list of recorded players), s <PLAYERNAME>(set a player's status to logged in), u <PLAYERNAME>(set a player's status to not logged in)").color(ChatColor.GREEN).create());
			}

		}else {s.sendMessage(new ComponentBuilder("Error executing command").color(ChatColor.RED).create());}
	}

}
