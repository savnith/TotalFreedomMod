package me.totalfreedom.totalfreedommod.event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerReportEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CommandSender reporter;
    private Player offender;
    private String reason;

    public PlayerReportEvent(CommandSender reporter, Player offender, String reason)
    {
        this.reporter = reporter;
        this.offender = offender;
        this.reason = reason;
    }

    public CommandSender getReporter()
    {
        return reporter;
    }

    public Player getOffender()
    {
        return offender;
    }

    public String getReason()
    {
        return reason;
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
}
