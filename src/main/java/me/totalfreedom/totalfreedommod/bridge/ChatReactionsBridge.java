package me.totalfreedom.totalfreedommod.bridge;

import me.clip.chatreaction.events.ReactionWinEvent;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

public class ChatReactionsBridge extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler
    public void onReactionWin(ReactionWinEvent event)
    {
        // If integration is disabled, don't bother continuing
        if (!ConfigEntry.SHOP_REACTIONS_ENABLED.getBoolean())
        {
            return;
        }

        // Awards the winner with coins.
        PlayerData data = plugin.pl.getData(event.getWinner());
        data.setCoins(data.getCoins() + ConfigEntry.SHOP_REACTIONS_COINS_PER_WIN.getInteger());

        // Lets the player know of their award
        FUtil.playerMsg(event.getWinner(), FUtil.colorize("&aYou have been given &6" + ConfigEntry.SHOP_REACTIONS_COINS_PER_WIN.getInteger() + " &acoins!"));
    }
}
