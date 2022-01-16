package me.totalfreedom.totalfreedommod.freeze;

import java.util.Objects;
import me.totalfreedom.totalfreedommod.event.freeze.FreezeEvent;
import me.totalfreedom.totalfreedommod.event.freeze.PlayerFreezeEvent;
import me.totalfreedom.totalfreedommod.event.freeze.PlayerUnfreezeEvent;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import static me.totalfreedom.totalfreedommod.TotalFreedomMod.plugin;
import static me.totalfreedom.totalfreedommod.player.FPlayer.AUTO_PURGE_TICKS;

public class FreezeData
{

    private final FPlayer fPlayer;
    //
    private Location location = null;
    private BukkitTask unfreeze = null;

    public FreezeData(FPlayer fPlayer)
    {
        this.fPlayer = fPlayer;
    }

    public boolean isFrozen()
    {
        return unfreeze != null;
    }

    public void setFrozen(boolean freeze)
    {
        final Player player = fPlayer.getPlayer();
        if (player == null)
        {
            FLog.info("Could not freeze that player as they are not online!");
            return;
        }

        FreezeEvent event = freeze ? new PlayerFreezeEvent(fPlayer, location) : new PlayerUnfreezeEvent(fPlayer);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
        {
            return;
        }

        FUtil.cancel(unfreeze);
        unfreeze = null;
        location = null;

        if (!freeze)
        {
            if (fPlayer.getPlayer().getGameMode() != GameMode.CREATIVE)
            {
                FUtil.setFlying(player, false);
            }

            return;
        }

        location = player.getLocation(); // Blockify location
        FUtil.setFlying(player, true); // Avoid infinite falling

        unfreeze = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (!Objects.requireNonNull(plugin()).al.isAdminImpostor(player) && Objects.requireNonNull(plugin()).pl.isPlayerImpostor(player))
                {
                    FUtil.adminAction("TotalFreedom", "Unfreezing " + player.getName(), false);
                    setFrozen(false);
                }
            }

        }.runTaskLater(Objects.requireNonNull(plugin()), AUTO_PURGE_TICKS);
    }

    public Location getLocation()
    {
        return location;
    }
}