package me.totalfreedom.totalfreedommod.freeze;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.event.freeze.PlayerFreezeEvent;
import me.totalfreedom.totalfreedommod.event.freeze.PlayerUnfreezeEvent;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class Freezer extends FreedomService
{
    private boolean globalFreeze = false;

    @Override
    public void onStart()
    {
        globalFreeze = false;
    }

    @Override
    public void onStop()
    {
    }

    public void purge()
    {
        this.globalFreeze = false;

        for (Player player : server.getOnlinePlayers())
        {
            plugin.pl.getPlayer(player).getFreezeData().setFrozen(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        final Player player = event.getPlayer();

        if (plugin.al.isAdmin(player))
        {
            return;
        }

        final FreezeData fd = plugin.pl.getPlayer(player).getFreezeData();
        if (!fd.isFrozen() && !globalFreeze)
        {
            return;
        }

        FUtil.setFlying(player, true);

        Location loc = player.getLocation();

        event.setTo(loc);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFrozen(PlayerFreezeEvent event)
    {
        FUtil.playerMsg(event.getPlayer(), "You have been frozen.", ChatColor.AQUA);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerUnfrozen(PlayerUnfreezeEvent event)
    {
        Player player = event.getPlayer();
        if (player != null)
        {
            FUtil.playerMsg(player, "You have been unfrozen.", ChatColor.AQUA);
        }
    }

    public boolean isGlobalFreeze()
    {
        return globalFreeze;
    }

    public void setGlobalFreeze(boolean frozen)
    {
        this.globalFreeze = frozen;
    }
}