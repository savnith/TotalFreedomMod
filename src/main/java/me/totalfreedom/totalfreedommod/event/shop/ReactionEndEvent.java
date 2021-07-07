package me.totalfreedom.totalfreedommod.event.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReactionEndEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private final String reactionString;
    private Player winner;

    public ReactionEndEvent(@Nullable Player winner, @NotNull String reactionString)
    {
        this(winner, reactionString, false);
    }

    public ReactionEndEvent(@Nullable Player winner, @NotNull String reactionString, boolean async)
    {
        super(async);

        this.winner = winner;
        this.reactionString = reactionString;
    }

    public String getReactionString()
    {
        return reactionString;
    }

    public Player getWinner()
    {
        return winner;
    }

    public boolean hasWinner()
    {
        return winner != null;
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
