package me.totalfreedom.totalfreedommod.event;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerSanctionEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private final String sanctioned;
    private final String by;
    private final String ip;
    private final PunishmentType type;

    private String reason;

    public PlayerSanctionEvent(FPlayer player, CommandSender by, PunishmentType type)
    {
        this.sanctioned = player.getName();
        this.by = by.getName();
        this.ip = player.getIp();
        this.type = type;
    }

    public PlayerSanctionEvent(Player sanctioned, CommandSender by, PunishmentType type)
    {
        this.sanctioned = sanctioned.getName();
        this.by = by.getName();
        this.ip = FUtil.getIp(sanctioned);
        this.type = type;
    }

    public PlayerSanctionEvent(Player sanctioned, CommandSender by, String ip, PunishmentType type)
    {
        this.sanctioned = sanctioned.getName();
        this.by = by.getName();
        this.ip = ip;
        this.type = type;
    }

    public PlayerSanctionEvent(String sanctioned, String by, String ip, PunishmentType type)
    {
        this.sanctioned = sanctioned;
        this.by = by;
        this.ip = ip;
        this.type = type;
    }

    public PlayerSanctionEvent(String sanctioned, String by, String ip, PunishmentType type, String reason)
    {
        this.sanctioned = sanctioned;
        this.by = by;
        this.ip = ip;
        this.type = type;
        this.reason = reason;
    }

    public String getSanctioned()
    {
        return sanctioned;
    }

    public String getBy()
    {
        return by;
    }

    public String getIP()
    {
        return ip;
    }

    public PunishmentType getType()
    {
        return type;
    }

    public boolean hasReason()
    {
        return reason != null;
    }

    public String getReason()
    {
        return reason;
    }

    public Punishment getPunishment()
    {
        return new Punishment(sanctioned, ip, by, type, reason);
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
