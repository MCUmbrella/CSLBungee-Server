package vip.floatationdevice.mcumbrella.cslbungee.server;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class CSLBServerCommand extends Command
{

    public CSLBServerCommand(String name)
    {
        super("cslbs");
    }

    @Override
    public void execute(CommandSender s, String[] args)
    {
        if(s.hasPermission("cslbs.main") && args.length != 0)
        {
            if(args[0].equalsIgnoreCase("t")) // print hello world
                s.sendMessage(new ComponentBuilder("Hello world!").color(ChatColor.GREEN).create());
            else if(args[0].equalsIgnoreCase("l")) // list not logged in players
                s.sendMessage(new ComponentBuilder("List of recorded players: " + CSLBServerMain.unloggedPlayers).color(ChatColor.GREEN).create());
            else if(args[0].equalsIgnoreCase("s") && args.length == 2) // remove player from unlogged list
            {
                CSLBServerMain.unloggedPlayers.remove(args[1]);
                s.sendMessage(new ComponentBuilder("Set '" + args[1] + "'s status to 'logged in'").color(ChatColor.GREEN).create());
            }
            else if(args[0].equalsIgnoreCase("u") && args.length == 2) // add player to unlogged list
            {
                CSLBServerMain.unloggedPlayers.add(args[1]);
                s.sendMessage(new ComponentBuilder("Set '" + args[1] + "'s status to 'not logged in'").color(ChatColor.GREEN).create());
            }
            else // print help
                s.sendMessage(new ComponentBuilder("Available subcommands: t(test command), l(list unlogged players), s <PLAYERNAME>(set a player's status to logged in), u <PLAYERNAME>(set a player's status to not logged in)").color(ChatColor.GREEN).create());
        }
    }
}
