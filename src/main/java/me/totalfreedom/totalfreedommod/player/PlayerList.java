package me.totalfreedom.totalfreedommod.player;

import com.earth2me.essentials.User;
import com.google.common.collect.Maps;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerList extends FreedomService
{
    public final Map<String, FPlayer> playerMap = Maps.newHashMap(); // ip,dataMap
    public final Map<UUID, PlayerData> dataMap = Maps.newHashMap(); // uuid, data

    @Override
    public void onStart()
    {
        dataMap.clear();
        loadMasterBuilders();
    }

    @Override
    public void onStop()
    {
    }

    public FPlayer getPlayerSync(Player player)
    {
        synchronized (playerMap)
        {
            return getPlayer(player);
        }
    }

    public void loadMasterBuilders()
    {
        ResultSet resultSet = plugin.sql.getMasterBuilders();

        if (resultSet == null)
        {
            return;
        }

        try
        {
            while (resultSet.next())
            {
                PlayerData playerData = load(resultSet);
                dataMap.put(playerData.getUuid(), playerData);
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to parse master builders: " + e.getMessage());
        }
    }

    public List<String> getMasterBuilderNames()
    {
        List<String> masterBuilders = new ArrayList<>();
        for (PlayerData playerData : plugin.pl.dataMap.values())
        {
            if (playerData.isMasterBuilder())
            {
                masterBuilders.add(playerData.getName());
            }
        }
        return masterBuilders;
    }

    public boolean canManageMasterBuilders(String name)
    {
        PlayerData data = getData(name);

        return (!ConfigEntry.HOST_SENDER_NAMES.getStringList().contains(name.toLowerCase()) && data != null && !ConfigEntry.SERVER_OWNERS.getStringList().contains(data.getName()))
                && !ConfigEntry.SERVER_EXECUTIVES.getStringList().contains(data.getName())
                && !isTelnetMasterBuilder(data)
                && !ConfigEntry.HOST_SENDER_NAMES.getStringList().contains(name.toLowerCase());
    }

    public boolean isTelnetMasterBuilder(PlayerData playerData)
    {
        Admin admin = plugin.al.getEntryByName(playerData.getName());
        return admin != null && admin.getRank().isAtLeast(Rank.ADMIN) && playerData.isMasterBuilder();
    }

    // May not return null
    public FPlayer getPlayer(Player player)
    {
        FPlayer tPlayer = playerMap.get(FUtil.getIp(player));
        if (tPlayer != null)
        {
            return tPlayer;
        }

        tPlayer = new FPlayer(plugin, player);
        playerMap.put(FUtil.getIp(player), tPlayer);

        return tPlayer;
    }

    public PlayerData loadByUuid(UUID uuid)
    {
        return load(plugin.sql.getPlayerByUuid(uuid));
    }

    public PlayerData loadByIp(String ip)
    {
        return load(plugin.sql.getPlayerByIp(ip));
    }

    public PlayerData load(ResultSet resultSet)
    {
        if (resultSet == null)
        {
            return null;
        }
        return new PlayerData(resultSet);
    }

    public void syncIps(Admin admin)
    {
        PlayerData playerData = getData(admin.getName());
        playerData.clearIps();
        playerData.addIps(admin.getIps());
        plugin.pl.save(playerData);
    }

    public void syncIps(PlayerData playerData)
    {
        Admin admin = plugin.al.getEntryByName(playerData.getName());

        if (admin != null && admin.isActive())
        {
            admin.clearIPs();
            admin.addIps(playerData.getIps());
            plugin.al.updateTables();
            plugin.al.save(admin);
        }
    }

    public void save(PlayerData player)
    {
        try
        {
            ResultSet currentSave = plugin.sql.getPlayerByUuid(player.getUuid());
            for (Map.Entry<String, Object> entry : player.toSQLStorable().entrySet())
            {
                Object storedValue = plugin.sql.getValue(currentSave, entry.getKey(), entry.getValue());
                if (storedValue != null && !storedValue.equals(entry.getValue()) || storedValue == null && entry.getValue() != null || entry.getValue() == null)
                {
                    plugin.sql.setPlayerValue(player, entry.getKey(), entry.getValue());
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to save player: " + e.getMessage());
        }
    }

    public PlayerData getData(Player player)
    {
        // Check for existing data
        PlayerData playerData = dataMap.get(player.getUniqueId());
        if (playerData != null)
        {
            return playerData;
        }

        // Load data
        playerData = loadByUuid(player.getUniqueId());

        if (playerData != null)
        {
            dataMap.put(player.getUniqueId(), playerData);
            return playerData;
        }

        // Create new data if nonexistent
        FLog.info("Creating new player data entry for " + player.getName());

        // Create new player
        playerData = new PlayerData(player);
        playerData.addIp(FUtil.getIp(player));

        // Store player
        dataMap.put(player.getUniqueId(), playerData);

        // Save player
        plugin.sql.addPlayer(playerData);
        return playerData;

    }

    public PlayerData getData(String username)
    {
        PlayerData playerData = null;
        UUID uuid = null;

        if (server.getPlayer(username) != null)
        {
            uuid = server.getPlayer(username).getUniqueId();
        }
        else
        {
            if (plugin.esb.isEnabled())
            {
                User user = plugin.esb.getEssentialsUser(username);

                if (user != null)
                {
                    uuid = user.getConfigUUID();
                }
            }

            if (uuid == null
                    && (server.getOfflinePlayer(username).hasPlayedBefore()
                    || ConfigEntry.PD_QUERY_MOJANG_FOR_UUIDLESS_PLAYERS.getBoolean()))
            {
                uuid = server.getOfflinePlayer(username).getUniqueId();
            }
            else
            {
                uuid = UUID.randomUUID();
            }
        }

        playerData = dataMap.get(uuid);

        if (playerData != null)
        {
            return playerData;
        }

        // Load data
        playerData = loadByUuid(uuid);

        if (playerData != null)
        {
            dataMap.put(uuid, playerData);
            return playerData;
        }

        return null;
    }

    public Map<String, FPlayer> getPlayerMap()
    {
        return playerMap;
    }

    public Map<UUID, PlayerData> getDataMap()
    {
        return dataMap;
    }
}