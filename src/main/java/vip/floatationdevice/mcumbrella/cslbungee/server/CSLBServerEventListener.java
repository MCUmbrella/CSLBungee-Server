package vip.floatationdevice.mcumbrella.cslbungee.server;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CSLBServerEventListener implements Listener
{
    @EventHandler
    public void onChat(ChatEvent e)
    {
        if(CSLBServerMain.unloggedPlayers.contains(e.getSender().toString()))
        {
            String[] cmd = e.getMessage().split(" ");
            if(!CSLBServerMain.cmdWhitelist.contains(cmd[0])) // check if command is not whitelisted
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChangeServer(ServerSwitchEvent e)
    {
        if(e.getFrom() != null) // check if this is not the initial connection
            if(CSLBServerMain.unloggedPlayers.contains(e.getPlayer().getName())) // check if player is unlogged
                e.getPlayer().disconnect(new ComponentBuilder("You are not logged in!").create()); // fuck him off. he's possibly exploiting
    }

    @EventHandler
    public void onJoin(PostLoginEvent e)
    {
        CSLBServerMain.unloggedPlayers.add(e.getPlayer().getName()); // add player to unlogged list
        BungeeCord.getInstance().getConsole().sendMessage(new ComponentBuilder("Added '" + e.getPlayer().getName() + "' to unlogged player list").color(ChatColor.AQUA).create());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e)
    {
        if(CSLBServerMain.unloggedPlayers.remove(e.getPlayer().getName())) // remove player from unlogged list
            BungeeCord.getInstance().getConsole().sendMessage(new ComponentBuilder("Removed '" + e.getPlayer().getName() + "' from unlogged player list").color(ChatColor.AQUA).create());
    }

}
