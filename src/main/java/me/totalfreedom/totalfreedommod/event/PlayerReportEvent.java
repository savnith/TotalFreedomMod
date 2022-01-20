package me.totalfreedom.totalfreedommod.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * <b>PlayerReportEvent</b>
 * <p>Called when a player is reported by another player.</p>
 */
public class PlayerReportEvent extends Event
{
    private static final HandlerList handlerList = new HandlerList();
    private final Player sender;
    private final Player target;
    private final String reason;

    public PlayerReportEvent(Player sender, Player target, String reason)
    {
        this.sender = sender;
        this.target = target;
        this.reason = reason;
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

    public Player getSender()
    {
        return sender;
    }

    public Player getTarget()
    {
        return target;
    }

    public String getReason()
    {
        return reason;
    }

}
