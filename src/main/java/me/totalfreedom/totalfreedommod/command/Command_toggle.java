package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import me.totalfreedom.totalfreedommod.GameRuleHandler;
import me.totalfreedom.totalfreedommod.LoginProcess;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Toggles TotalFreedomMod settings", usage = "/<command> [option] [value] [value]")
public class Command_toggle extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            msg("Available toggles: ");
            if (isAdmin(sender))
            {
                msg("- waterplace");
                msg("- fireplace");
                msg("- lavaplace");
                msg("- fluidspread");
                msg("- lavadmg");
                msg("- firespread");
                msg("- frostwalk");
                msg("- firework");
                msg("- prelog");
                msg("- lockdown");
                msg("- petprotect");
                msg("- entitywipe");
                msg("- nonuke [range] [count]");
                msg("- explosives [radius]");
                msg("- unsafeenchs");
                msg("- bells");
                msg("- armorstands");
                msg("- structureblocks");
                msg("- jigsaws");
                msg("- grindstones");
                msg("- jukeboxes");
                msg("- spawners");
                msg("- 4chan");
                msg("- beehives");
                msg("- respawnanchors");
                msg("- autotp");
                msg("- autoclear");
                msg("- minecarts");
                msg("- landmines");
                msg("- mp44");
                msg("- tossmob");
                msg("- chat");
                msg("- clownfish <player>");
                msg("- explosive_arrows");

                if (plugin.ldb.isEnabled())
                {
                    msg("- disguises");
                }

            }
            msg("- pickups");
            msg("- discord");
            msg("- gravity");
            msg("- glow");
            return false;
        }

        switch (args[0].toLowerCase())
        {
            case "waterplace":
            {
                checkRank(Rank.ADMIN);

                toggle("Water placement is", ConfigEntry.ALLOW_WATER_PLACE);
                break;
            }

            case "frostwalk":
            {
                checkRank(Rank.ADMIN);

                toggle("Frost walker enchantment is", ConfigEntry.ALLOW_FROSTWALKER);
                break;
            }

            case "fireplace":
            {
                checkRank(Rank.ADMIN);

                toggle("Fire placement is", ConfigEntry.ALLOW_FIRE_PLACE);
                break;
            }

            case "lavaplace":
            {
                checkRank(Rank.ADMIN);

                toggle("Lava placement is", ConfigEntry.ALLOW_LAVA_PLACE);
                break;
            }

            case "fluidspread":
            {
                checkRank(Rank.ADMIN);

                toggle("Fluid spread is", ConfigEntry.ALLOW_FLUID_SPREAD);
                break;
            }

            case "lavadmg":
            {
                checkRank(Rank.ADMIN);

                toggle("Lava damage is", ConfigEntry.ALLOW_LAVA_DAMAGE);
                break;
            }

            case "firespread":
            {
                checkRank(Rank.ADMIN);

                toggle("Fire spread is", ConfigEntry.ALLOW_FIRE_SPREAD);
                plugin.gr.setGameRule(GameRuleHandler.GameRule.DO_FIRE_TICK, ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean());
                break;
            }

            case "prelog":
            {
                checkRank(Rank.ADMIN);

                toggle("Command prelogging is", ConfigEntry.ENABLE_PREPROCESS_LOG);
                break;
            }

            case "lockdown":
            {
                checkRank(Rank.ADMIN);

                boolean active = !LoginProcess.isLockdownEnabled();
                LoginProcess.setLockdownEnabled(active);
                FUtil.adminAction(sender.getName(), (active ? "A" : "De-a") + "ctivating server lockdown", true);
                break;
            }

            case "petprotect":
            {
                checkRank(Rank.ADMIN);

                toggle("Tamed pet protection is", ConfigEntry.ENABLE_PET_PROTECT);
                break;
            }

            case "entitywipe":
            {
                checkRank(Rank.ADMIN);

                toggle("Automatic entity wiping is", ConfigEntry.AUTO_ENTITY_WIPE);
                break;
            }

            case "firework":
            {
                checkRank(Rank.ADMIN);

                toggle("Firework explosion is", ConfigEntry.ALLOW_FIREWORK_EXPLOSION);
                break;
            }

            case "nonuke":
            {
                checkRank(Rank.ADMIN);

                if (args.length >= 2)
                {
                    try
                    {
                        ConfigEntry.NUKE_MONITOR_RANGE.setDouble(Math.max(1.0, Math.min(500.0, Double.parseDouble(args[1]))));
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("The input provided is not a valid integer.");
                        return true;
                    }
                }

                if (args.length >= 3)
                {
                    try
                    {
                        ConfigEntry.NUKE_MONITOR_COUNT_BREAK.setInteger(Math.max(1, Math.min(500, Integer.parseInt(args[2]))));
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("The input provided is not a valid integer.");
                        return true;
                    }
                }

                toggle("Nuke monitor is", ConfigEntry.NUKE_MONITOR_ENABLED);

                if (ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
                {
                    msg("Anti-freecam range is set to " + ConfigEntry.NUKE_MONITOR_RANGE.getDouble() + " blocks.");
                    msg("Block throttle rate is set to " + ConfigEntry.NUKE_MONITOR_COUNT_BREAK.getInteger() + " blocks destroyed per 5 seconds.");
                }
                break;
            }

            case "explosives":
            {
                checkRank(Rank.ADMIN);

                if (args.length == 2)
                {
                    try
                    {
                        ConfigEntry.EXPLOSIVE_RADIUS.setDouble(Math.max(1.0, Math.min(30.0, Double.parseDouble(args[1]))));
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("The input provided is not a valid integer.");
                        return true;
                    }
                }

                toggle("Explosions are", ConfigEntry.ALLOW_EXPLOSIONS);

                if (ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
                {
                    msg("Radius set to " + ConfigEntry.EXPLOSIVE_RADIUS.getDouble());
                }
                break;
            }

            case "unsafeenchs":
            {
                checkRank(Rank.ADMIN);

                toggle("Unsafe enchantments are", ConfigEntry.ALLOW_UNSAFE_ENCHANTMENTS);
                break;
            }

            case "bells":
            {
                checkRank(Rank.ADMIN);

                toggle("The ringing of bells is", ConfigEntry.ALLOW_BELLS);
                break;
            }

            case "armorstands":
            {
                checkRank(Rank.ADMIN);

                toggle("The placement of armor stands is", ConfigEntry.ALLOW_ARMOR_STANDS);
                break;
            }

            case "structureblocks":
            {
                checkRank(Rank.ADMIN);

                toggle("Structure blocks are", ConfigEntry.ALLOW_STRUCTURE_BLOCKS);
                break;
            }

            case "jigsaws":
            {
                checkRank(Rank.ADMIN);

                toggle("Jigsaws are", ConfigEntry.ALLOW_JIGSAWS);
                break;
            }

            case "grindstones":
            {
                checkRank(Rank.ADMIN);

                toggle("Grindstones are", ConfigEntry.ALLOW_GRINDSTONES);
                break;
            }

            case "jukeboxes":
            {
                checkRank(Rank.ADMIN);

                toggle("Jukeboxes are", ConfigEntry.ALLOW_JUKEBOXES);
                break;
            }

            case "spawners":
            {
                checkRank(Rank.ADMIN);

                toggle("Spawners are", ConfigEntry.ALLOW_SPAWNERS);
                break;
            }

            case "4chan":
            {
                checkRank(Rank.ADMIN);

                toggle("4chan mode is", ConfigEntry.FOURCHAN_ENABLED);
                break;
            }

            case "beehives":
            {
                checkRank(Rank.ADMIN);

                toggle("Beehives are", ConfigEntry.ALLOW_BEEHIVES);
                break;
            }

            case "respawnanchors":
            {
                checkRank(Rank.ADMIN);

                toggle("Respawn anchors are", ConfigEntry.ALLOW_RESPAWN_ANCHORS);
                break;
            }

            case "autotp":
            {
                checkRank(Rank.ADMIN);

                toggle("Teleportation on join is", ConfigEntry.AUTO_TP);
                break;
            }

            case "autoclear":
            {
                checkRank(Rank.ADMIN);

                toggle("Clearing inventories on join is", ConfigEntry.AUTO_CLEAR);
                break;
            }

            case "minecarts":
            {
                checkRank(Rank.ADMIN);

                toggle("Minecarts are", ConfigEntry.ALLOW_MINECARTS);
                break;
            }

            case "landmines":
            {
                checkRank(Rank.ADMIN);

                toggle("Landmines are", ConfigEntry.LANDMINES_ENABLED);
                break;
            }

            case "mp44":
            {
                checkRank(Rank.ADMIN);

                toggle("MP44 is", ConfigEntry.MP44_ENABLED);
                break;
            }

            case "tossmob":
            {
                checkRank(Rank.ADMIN);

                toggle("Tossmob is", ConfigEntry.TOSSMOB_ENABLED);
                break;
            }

            case "chat":
            {
                checkRank(Rank.ADMIN);

                FUtil.adminAction(sender.getName(), "Chat " + (!ConfigEntry.TOGGLE_CHAT.getBoolean() ? "enabled" : "disabled") + ".", true);
                toggle("The ability to chat is", ConfigEntry.TOGGLE_CHAT);
                break;
            }

            case "clownfish":
            {
                checkRank(Rank.ADMIN);

                if (args.length >= 2)
                {
                    // TODO: Move this to PlayerData and please, for the love of god, don't ever do something like this again.
                    boolean enabled = plugin.lp.CLOWNFISH_TOGGLE.contains(args[1]);

                    if (enabled)
                    {
                        plugin.lp.CLOWNFISH_TOGGLE.remove(args[1]);
                    }
                    else
                    {
                        plugin.lp.CLOWNFISH_TOGGLE.add(args[1]);
                    }

                    msg(args[0] + " will " + (enabled ? "now" : "no longer") + " have the ability to use the Clownfish.");
                }
                else
                {
                    msg("You need to give a player name!", ChatColor.RED);
                }

                break;
            }

            case "explosivearrows":
            {
                checkRank(Rank.ADMIN);

                boolean onList = plugin.it.explosivePlayers.contains(playerSender);
                if (onList)
                {
                    plugin.it.explosivePlayers.remove(playerSender);
                    msg("You no longer have explosive arrows.", ChatColor.RED);
                }
                else
                {
                    plugin.it.explosivePlayers.add(playerSender);
                    msg("You now have explosive arrows.", ChatColor.GREEN);
                }
                break;
            }

            case "disguises":
            {
                checkRank(Rank.ADMIN);

                if (!plugin.ldb.isEnabled())
                {
                    msg("LibsDisguises is not enabled.");
                    return true;
                }

                FUtil.adminAction(sender.getName(), (plugin.ldb.isDisguisesEnabled() ? "Disabling" : "Enabling") + " disguises", false);

                if (plugin.ldb.isDisguisesEnabled())
                {
                    plugin.ldb.undisguiseAll(true);
                    plugin.ldb.setDisguisesEnabled(false);
                }
                else
                {
                    plugin.ldb.setDisguisesEnabled(true);
                }

                msg("Disguises are now " + (plugin.ldb.isDisguisesEnabled() ? "enabled." : "disabled."));
                break;
            }

            case "pickup":
            {
                checkPlayer();

                boolean enabled = !playerSender.getCanPickupItems();

                playerSender.setCanPickupItems(enabled);
                msg((enabled ? "En" : "Dis") + "abled item pickup.", (enabled ? ChatColor.GREEN : ChatColor.RED));
                break;
            }

            case "discord":
            {
                checkPlayer();

                PlayerData data = getData(playerSender);
                data.setDisplayDiscord(!data.doesDisplayDiscord());

                msg("Discord messages will " + (data.doesDisplayDiscord() ? "now" : "no longer") + " be shown.");
                break;
            }

            case "gravity":
            {
                checkPlayer();

                boolean enabled = !playerSender.hasGravity();
                playerSender.setGravity(enabled);

                msg((enabled ? "En" : "Dis") + "abled gravity.", (enabled ? ChatColor.GREEN : ChatColor.RED));
                break;
            }

            case "glow":
            {
                checkPlayer();

                boolean glowing = playerSender.hasPotionEffect(PotionEffectType.GLOWING);
                if (glowing)
                {
                    playerSender.removePotionEffect(PotionEffectType.GLOWING);
                }
                else
                {
                    PotionEffect glow = new PotionEffect(PotionEffectType.GLOWING, 1000000, 1, false, false);
                    playerSender.addPotionEffect(glow);
                }

                msg("You are " + (!glowing ? "now" : "no longer") + " glowing.");
                break;
            }
        }
        return true;
    }

    private void toggle(final String name, final ConfigEntry entry)
    {
        msg(name + " now " + (entry.setBoolean(!entry.getBoolean()) ? "enabled." : "disabled."));
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            ImmutableList.Builder<String> builder = ImmutableList.builder();

            if (isAdmin(sender))
            {
                builder.add("waterplace", "fireplace", "lavaplace", "fluidspread", "lavadmg", "firespread", "frostwalk",
                        "firework", "prelog", "lockdown", "petprotect", "entitywipe", "nonuke", "explosives", "unsafeenchs",
                        "bells", "armorstands", "structureblocks", "jigsaws", "grindstones", "jukeboxes", "spawners", "4chan", "beehives",
                        "respawnanchors", "autotp", "autoclear", "minecarts", "mp44", "landmines", "tossmob", "chat", "clownfish",
                        "explosivearrows");

                if (plugin.ldb.isEnabled())
                {
                    builder.add("disguises");
                }
            }

            builder.add("pickup", "discord", "gravity", "glow");

            return builder.build();
        }

        return Collections.emptyList();
    }
}