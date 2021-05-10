package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Get the seed of the world you are currently in.", usage = "/seed [world]")
public class Command_seed extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        World world;

        if (args.length > 0)
        {
            world = server.getWorld(args[0]);
            if (world == null)
            {
                msg("That world could not be found", ChatColor.RED);
                return true;
            }
        }
        else
        {
            // If the sender is a Player, use that world. Otherwise, use the overworld as a fallback.
            if (sender instanceof Player)
            {
                world = playerSender.getWorld();
            }
            else
            {
                world = server.getWorlds().get(0);
            }
        }

        msg("Seed: [" + ChatColor.GREEN + world.getSeed() + ChatColor.WHITE + "]", ChatColor.WHITE);
        return true;
    }
}
