package me.totalfreedom.totalfreedommod.event.freeze;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class FreezeEvent extends Event implements Cancellable
{
    public abstract Player getPlayer();

    public abstract FPlayer getFPlayer();
}
