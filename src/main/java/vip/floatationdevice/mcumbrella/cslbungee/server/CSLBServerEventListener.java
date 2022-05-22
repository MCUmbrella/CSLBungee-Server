package vip.floatationdevice.mcumbrella.cslbungee.server;

import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CSLBServerEventListener implements Listener
{
    @EventHandler
    public void onChat(ChatEvent e)
    {
        if(!CSLBServerMain.playerLoginStatusMap.get(e.getSender().toString()))
        {
            String[] cmd = e.getMessage().split(" ");
            if(!CSLBServerMain.cmdWhitelist.contains(cmd[0]))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PostLoginEvent e)
    {
        CSLBServerMain.playerLoginStatusMap.put(e.getPlayer().getName(), false);
        CSLBServerMain.main.getLogger().info("Added '" + e.getPlayer().getName() + "' to records");
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e)
    {
        CSLBServerMain.playerLoginStatusMap.remove(e.getPlayer().getName());
        CSLBServerMain.main.getLogger().info("Removed '" + e.getPlayer().getName() + "' from records");
    }

}
