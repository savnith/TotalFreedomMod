package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.freeze.FreezeData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRequires({"JDA"})
@CommandPermissions(level = Rank.ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Verify an admin without giving them admin permissions.", usage = "/<command> <player>", aliases = "vns,verifynostaff,vna")
public class Command_verifynoadmin extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        if (plugin.al.isAdminImpostor(player))
        {
            String ip = FUtil.getIp(player);
            if (!plugin.al.verifiedNoAdmin.containsKey(player.getName()))
            {
                List<String> ips = new ArrayList<>();
                ips.add(ip);
                plugin.al.verifiedNoAdmin.put(player.getName(), ips);
            }
            else
            {
                List<String> ips = plugin.al.verifiedNoAdmin.get(player.getName());
                if (!ips.contains(ip))
                {
                    ips.add(ip);
                    plugin.al.verifiedNoAdmin.remove(player.getName());
                    plugin.al.verifiedNoAdmin.put(player.getName(), ips);
                }
            }
            plugin.rm.updateDisplay(player);
            FUtil.adminAction(sender.getName(), "Verified " + player.getName() + ", without admin permissions.", true);
            player.setOp(true);
            msg(player, YOU_ARE_OP);
            final FreezeData fd = plugin.pl.getPlayer(player).getFreezeData();
            if (fd.isFrozen())
            {
                fd.setFrozen(false);
            }
            msg("Verified " + player.getName() + " but didn't give them admin permissions", ChatColor.GREEN);
        }
        else
        {
            msg(player.getName() + " is not an admin imposter.", ChatColor.RED);
        }

        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            List<String> adminImposters = new ArrayList<>();
            for (Player player : server.getOnlinePlayers())
            {
                if (plugin.al.isAdminImpostor(player))
                {
                    adminImposters.add(player.getName());
                }
            }
            return adminImposters;
        }

        return Collections.emptyList();
    }
}