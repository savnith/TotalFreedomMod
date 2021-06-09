package me.totalfreedom.totalfreedommod.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FSync;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Check the name history of a specified player.", usage = "/<command> <username>", aliases = "nh")
public class Command_namehistory extends FreedomCommand
{
    public final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final Pattern usernameRegex = Pattern.compile("^[A-Za-z0-9_]*$");

    @Override
    public boolean run(final CommandSender sender, final Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        // Just in case someone tries
        Matcher matcher = usernameRegex.matcher(args[0]);
        if (!matcher.find())
        {
            msg("Invalid username: " + args[0], ChatColor.RED);
            return true;
        }

        msg("Connecting, please wait...", ChatColor.GREEN);
        reportHistory(sender, args[0]);
        return true;
    }

    public void reportHistory(final CommandSender sender, final String username)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                UUID uuid = server.getOfflinePlayer(username).getUniqueId();
                Gson gson = new GsonBuilder().create();
                String compactUuid = uuid.toString().replace("-", "");
                try
                {
                    URL url = new URL("https://api.mojang.com/user/profiles/" + compactUuid + "/names");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    FName[] oldNames = gson.fromJson(reader, FName[].class);
                    if (oldNames == null)
                    {
                        FSync.playerMsg(sender, ChatColor.RED + "Player not found!");
                        return;
                    }
                    reader.close();
                    conn.disconnect();
                    Arrays.sort(oldNames);
                    printHistory(sender, oldNames);
                }
                catch (Exception ex)
                {
                    FSync.playerMsg(sender, ChatColor.RED + "Error, see logs for more details.");
                    FLog.severe(ex);
                }
            }
        }.runTaskAsynchronously(TotalFreedomMod.getPlugin());
    }

    private void printHistory(CommandSender sender, FName[] oldNames)
    {
        if (oldNames.length == 1)
        {
            FSync.playerMsg(sender, ChatColor.GREEN + oldNames[0].getName() + ChatColor.GOLD + " has never changed their name.");
            return;
        }
        FSync.playerMsg(sender, ChatColor.GOLD + "Original name: " + ChatColor.GREEN + oldNames[0].getName());
        for (int i = 1; i < oldNames.length; i++)
        {
            Date date = new Date(oldNames[i].getChangedToAt());
            String formattedDate = dateFormat.format(date);
            FSync.playerMsg(sender, ChatColor.BLUE + formattedDate + ChatColor.GOLD + " changed to " + ChatColor.GREEN + oldNames[i].getName());
        }
    }

    private static class FName implements Comparable<FName>
    {
        private final String name;
        private final long changedToAt;

        //Added constructor because otherwise there's no way name or changedToAt would have been anything other than null.
        public FName(String name, long changedToAt)
        {
            this.name = name;
            this.changedToAt = changedToAt;
        }

        @Override
        public int compareTo(FName other)
        {
            return Long.compare(this.changedToAt, other.changedToAt);
        }

        public String getName()
        {
            return name;
        }

        public long getChangedToAt()
        {
            return changedToAt;
        }
    }
}