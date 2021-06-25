package me.totalfreedom.totalfreedommod.admin;

import me.totalfreedom.totalfreedommod.LogViewer.LogsRegistrationMode;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LegacyAdmin
{


    private final List<String> ips = new ArrayList<>();
    private String name;
    private boolean active = true;
    private Rank rank = Rank.ADMIN;
    private Date lastLogin = new Date();
    private Boolean commandSpy = false;
    private Boolean potionSpy = false;
    private String acFormat = null;
    private String pteroID = null;

    public LegacyAdmin(Player player)
    {
        this.name = player.getName();
        this.ips.add(FUtil.getIp(player));
    }

    public LegacyAdmin(ResultSet resultSet)
    {
        try
        {
            this.name = resultSet.getString("username");
            this.active = resultSet.getBoolean("active");
            this.rank = Rank.findRank(resultSet.getString("rank"));
            this.ips.clear();
            this.ips.addAll(FUtil.stringToList(resultSet.getString("ips")));
            this.lastLogin = new Date(resultSet.getLong("last_login"));
            this.commandSpy = resultSet.getBoolean("command_spy");
            this.potionSpy = resultSet.getBoolean("potion_spy");
            this.acFormat = resultSet.getString("ac_format");
            this.pteroID = resultSet.getString("ptero_id");
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to load admin: " + e.getMessage());
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder output = new StringBuilder();

        output.append("Admin: ").append(name).append("\n")
                .append("- IPs: ").append(StringUtils.join(ips, ", ")).append("\n")
                .append("- Last Login: ").append(FUtil.dateToString(lastLogin)).append("\n")
                .append("- Rank: ").append(rank.getName()).append("\n")
                .append("- Is Active: ").append(active).append("\n")
                .append("- Potion Spy: ").append(potionSpy).append("\n")
                .append("- Admin Chat Format: ").append(acFormat).append("\n")
                .append("- Pterodactyl ID: ").append(pteroID).append("\n");

        return output.toString();
    }

    public boolean isValid()
    {
        return name != null
                && rank != null
                && !ips.isEmpty()
                && lastLogin != null;
    }

    public String getName()
    {
        return name;
    }

    public boolean isActive()
    {
        return active;
    }

    public Rank getRank()
    {
        return rank;
    }

    public List<String> getIps()
    {
        return ips;
    }

    public Date getLastLogin()
    {
        return lastLogin;
    }

    public Boolean getCommandSpy()
    {
        return commandSpy;
    }

    public Boolean getPotionSpy()
    {
        return potionSpy;
    }

    public String getAcFormat()
    {
        return acFormat;
    }

    public String getPteroID()
    {
        return pteroID;
    }
}