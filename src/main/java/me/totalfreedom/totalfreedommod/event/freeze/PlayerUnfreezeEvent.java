package me.totalfreedom.totalfreedommod.event.freeze;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerUnfreezeEvent extends FreezeEvent
{
    private static HandlerList handlerList = new HandlerList();
    //--
    private final FPlayer player;
    //--
    private boolean cancelled;

    public PlayerUnfreezeEvent(FPlayer player)
    {
        this.player = player;
    }

    @NotNull
    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b)
    {
        cancelled = b;
    }

    @Override
    public Player getPlayer()
    {
        return player.getPlayer();
    }

    @Override
    public FPlayer getFPlayer()
    {
        return player;
    }
}
