package me.totalfreedom.totalfreedommod.blocking;

import io.papermc.paper.event.player.PlayerSignCommandPreprocessEvent;
import me.totalfreedom.totalfreedommod.FreedomService;
import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

//codebeat:disable[LOC,ABC]

public class SignBlocker extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerClickSign(PlayerSignCommandPreprocessEvent event)
    {
        event.setCancelled(true);
    }
}