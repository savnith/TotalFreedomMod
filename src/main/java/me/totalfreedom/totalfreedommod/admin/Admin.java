package me.totalfreedom.totalfreedommod.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import me.totalfreedom.totalfreedommod.LogViewer.LogsRegistrationMode;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Admin
{

    private UUID uuid;
    private boolean active = true;
    private Rank rank = Rank.ADMIN;
    private Date lastLogin = new Date();
    private Boolean commandSpy = false;
    private Boolean potionSpy = false;
    private String acFormat = null;
    private String pteroID = null;

    public Admin(LegacyAdmin legacy)
    {
        // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        this.uuid = TotalFreedomMod.getPlugin().getServer().getOfflinePlayer(legacy.getName()).getUniqueId();
        this.active = legacy.isActive();
        this.rank = legacy.getRank();
        this.lastLogin = legacy.getLastLogin();
        this.commandSpy = legacy.getCommandSpy();
        this.potionSpy = legacy.getPotionSpy();
        this.acFormat = legacy.getAcFormat();
        this.pteroID = legacy.getPteroID();
    }

    public Admin(Player player)
    {
        this.uuid = player.getUniqueId();
    }

    public Admin(ResultSet resultSet)
    {
        try
        {
            this.uuid = UUID.fromString(resultSet.getString("uuid"));
            this.active = resultSet.getBoolean("active");
            this.rank = Rank.findRank(resultSet.getString("rank"));
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

        output.append("Admin: ").append(uuid.toString()).append("\n")
                .append("- Last Login: ").append(FUtil.dateToString(lastLogin)).append("\n")
                .append("- Rank: ").append(rank.getName()).append("\n")
                .append("- Is Active: ").append(active).append("\n")
                .append("- Potion Spy: ").append(potionSpy).append("\n")
                .append("- Admin Chat Format: ").append(acFormat).append("\n")
                .append("- Pterodactyl ID: ").append(pteroID).append("\n");

        return output.toString();
    }

    public Map<String, Object> toSQLStorable()
    {
        Map<String, Object> map = new HashMap<String, Object>()
        {{
            put("uuid", uuid.toString());
            put("active", active);
            put("rank", rank.toString());
            put("last_login", lastLogin.getTime());
            put("command_spy", commandSpy);
            put("potion_spy", potionSpy);
            put("ac_format", acFormat);
            put("ptero_id", pteroID);
        }};
        return map;
    }

    public boolean isValid()
    {
        return uuid != null
                && rank != null
                && lastLogin != null;
    }

    public String getName()
    {
        OfflinePlayer player = TotalFreedomMod.getPlugin().getServer().getOfflinePlayer(uuid);
        try
        {
            return player.getName();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;

        final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();

        // Avoiding stupid NPE compiler warnings
        if (plugin == null)
        {
            Bukkit.getLogger().severe("The plugin is null!! This is a major issue and WILL break the plugin!");
            return;
        }

        final Server server = plugin.getServer();

        if (!active)
        {
            if (getRank().isAtLeast(Rank.ADMIN))
            {
                if (plugin.btb != null)
                {
                    plugin.btb.killTelnetSessions(server.getOfflinePlayer(uuid).getName());
                }
            }

            plugin.lv.updateLogsRegistration(null, server.getOfflinePlayer(uuid).getName(), LogsRegistrationMode.DELETE);
        }
    }

    public Rank getRank()
    {
        return rank;
    }

    public void setRank(Rank rank)
    {
        this.rank = rank;
    }

    public Date getLastLogin()
    {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin)
    {
        this.lastLogin = lastLogin;
    }

    public Boolean getCommandSpy()
    {
        return commandSpy;
    }

    public void setCommandSpy(Boolean commandSpy)
    {
        this.commandSpy = commandSpy;
    }

    public Boolean getPotionSpy()
    {
        return potionSpy;
    }

    public void setPotionSpy(Boolean potionSpy)
    {
        this.potionSpy = potionSpy;
    }

    public String getAcFormat()
    {
        return acFormat;
    }

    public void setAcFormat(String acFormat)
    {
        this.acFormat = acFormat;
    }

    public String getPteroID()
    {
        return pteroID;
    }

    public void setPteroID(String pteroID)
    {
        this.pteroID = pteroID;
    }
}