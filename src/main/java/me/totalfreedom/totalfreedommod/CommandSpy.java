package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * <b>CommandSpy</b>
 * <p>Allows admins to see other players' commands.</p>
 */
public class CommandSpy extends FreedomService
{
    @Override
    public void onStart()
    {
        // Do nothing
    }

    @Override
    public void onStop()
    {
        // Do nothing

    }

    /**
     * <p>Sends a message containing the player's name and the command they used to all admins online with CommandSpy
     * enabled and to the console.</p>
     * @param event PlayerCommandPreprocessEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        Player sender = event.getPlayer();
        String command = event.getMessage();

        // Sends a message to the console if enabled
        if (ConfigEntry.ENABLE_PREPROCESS_LOG.getBoolean())
        {
            FLog.info(String.format("[PREPROCESS_COMMAND] %s (%s): %s", sender.getName(),
                    ChatColor.stripColor(sender.getDisplayName()), command), true);
        }

        // Sends a message to all players on the server
        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player) && plugin.al.getAdmin(player).getCommandSpy())
            {
                if (player != event.getPlayer())
                {
                    FUtil.playerMsg(player, event.getPlayer().getName() + ": " + event.getMessage());
                }
            }
        }
    }
}