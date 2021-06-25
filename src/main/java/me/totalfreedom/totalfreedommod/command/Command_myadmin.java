package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.discord.Discord;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage your admin entry.", usage = "/<command> [-o <admin name>] <setscformat <format> | clearscformat> | syncroles>")
public class Command_myadmin extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        Player init = null;
        Admin target = getAdmin(playerSender);
        Player targetPlayer = playerSender;

        // -o switch
        if (args[0].equals("-o"))
        {
            checkRank(Rank.SENIOR_ADMIN);
            init = playerSender;
            targetPlayer = getPlayer(args[1]);
            if (targetPlayer == null)
            {
                msg(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }

            target = getAdmin(targetPlayer);
            if (target == null)
            {
                msg("That player is not an admin", ChatColor.RED);
                return true;
            }

            // Shift 2
            args = Arrays.copyOfRange(args, 2, args.length);
            if (args.length < 1)
            {
                return false;
            }
        }

        switch (args[0])
        {
            case "setacformat":
            case "setscformat":
            {
                String format = StringUtils.join(args, " ", 1, args.length);
                target.setAcFormat(format);
                plugin.al.save(target);
                plugin.al.updateTables();
                msg("Set admin chat format to \"" + format + "\".", ChatColor.GRAY);
                String example = format.replace("%name%", "ExampleAdmin").replace("%rank%", Rank.ADMIN.getAbbr()).replace("%rankcolor%", Rank.ADMIN.getColor().toString()).replace("%msg%", "The quick brown fox jumps over the lazy dog.");
                msg(ChatColor.GRAY + "Example: " + FUtil.colorize(example));
                return true;
            }

            case "clearacformat":
            case "clearscformat":
            {
                target.setAcFormat(null);
                plugin.al.save(target);
                plugin.al.updateTables();
                msg("Cleared admin chat format.", ChatColor.GRAY);
                return true;
            }

            case "syncroles":
            {
                if (plugin.dc.enabled)
                {
                    if (!ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                    {
                        msg("Role syncing is not enabled.", ChatColor.RED);
                        return true;
                    }
                    PlayerData playerData = plugin.pl.getData(target.getUuid());
                    if (playerData.getDiscordID() == null)
                    {
                        msg("Please run /linkdiscord first!", ChatColor.RED);
                        return true;
                    }
                    boolean synced = Discord.syncRoles(target, playerData.getDiscordID());
                    if (synced)
                    {
                        msg("Successfully synced your roles.", ChatColor.GREEN);
                    }
                    else
                    {
                        msg("Failed to sync your roles, please check the console.", ChatColor.RED);
                    }
                }

                return true;
            }

            default:
            {
                return false;
            }
        }
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!plugin.al.isAdmin(sender))
        {
            return Collections.emptyList();
        }

        List<String> singleArguments = Arrays.asList("clearips", "setscformat", "setacformat");
        List<String> doubleArguments = Arrays.asList("clearip", "clearscformat", "clearacformat", "syncroles");
        if (args.length == 1)
        {
            List<String> options = new ArrayList<>();
            options.add("-o");
            options.addAll(singleArguments);
            options.addAll(doubleArguments);
            return options;
        }
        else if (args.length == 2)
        {
            if (args[0].equals("-o"))
            {
                return FUtil.getPlayerList();
            }
        }
        else if (args.length == 3)
        {
            if (args[0].equals("-o"))
            {
                List<String> options = new ArrayList<>();
                options.addAll(singleArguments);
                options.addAll(doubleArguments);
                return options;
            }
        }
        return FUtil.getPlayerList();
    }
}