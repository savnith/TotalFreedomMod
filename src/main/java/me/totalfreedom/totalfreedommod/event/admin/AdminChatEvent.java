package me.totalfreedom.totalfreedommod.event.admin;

import com.google.common.base.Strings;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AdminChatEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private boolean fromInGame;
    private String prefix = null;

    private String name;
    private Displayable display;
    private String message;

    public AdminChatEvent(String prefix, CommandSender sender, String message, boolean async)
    {
        this.prefix = prefix;
        this.name = sender.getName();
    }

    public AdminChatEvent(CommandSender sender, String message, boolean async)
    {
        super(async);
        this.name = sender.getName();
        this.display = TotalFreedomMod.getPlugin().rm.getDisplay(sender);
        this.message = message;
    }

    public AdminChatEvent(CommandSender sender, String message)
    {
        this(sender, message, false);
    }

    public AdminChatEvent(String name, Displayable display, String message, boolean fromInGame)
    {
        this(name, display, message, fromInGame, false);
    }

    public AdminChatEvent(String name, Displayable display, String message, boolean fromInGame, boolean async)
    {
        super(async);
        this.name = name;
        this.display = display;
        this.message = message;
        this.fromInGame = fromInGame;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public String getSenderName()
    {
        return name;
    }

    public String getMessage()
    {
        return message;
    }

    public Displayable getDisplay()
    {
        return display;
    }

    public boolean isFromInGame()
    {
        return fromInGame;
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
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        if (prefix != null)
        {
            builder.append(prefix).append(" ");
        }

        builder.append("[ADMIN] ")
                .append(name)
                .append(" [")
                .append(display.getAbbr());

        return builder.toString();
    }
}
