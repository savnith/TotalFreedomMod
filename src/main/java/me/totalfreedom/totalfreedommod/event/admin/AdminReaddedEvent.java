package me.totalfreedom.totalfreedommod.event.admin;

import me.totalfreedom.totalfreedommod.admin.Admin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AdminReaddedEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private final Admin admin;
    private final CommandSender by;
    private final Player player;

    private boolean cancelled = false;

    public AdminReaddedEvent(@NotNull Admin admin, @NotNull CommandSender by, @NotNull Player player)
    {
        this.admin = admin;
        this.by = by;
        this.player = player;
    }

    public Admin getAdmin()
    {
        return admin;
    }

    public CommandSender getBy()
    {
        return by;
    }

    public Player getPlayer()
    {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }
}
