package me.totalfreedom.totalfreedommod.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.admin.LegacyAdmin;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.LegacyPlayerData;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;

public class SQLite extends FreedomService
{
    private final String FILE_NAME = "database.db";

    private Connection connection;

    @Override
    public void onStart()
    {
        connect();
        convertTables();
        checkTables();
    }

    @Override
    public void onStop()
    {
        disconnect();
    }

    public void connect()
    {
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/" + FILE_NAME);
            FLog.info("Successfully connected to the database.");
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to connect to the database: " + e.getMessage());
        }
    }

    public void disconnect()
    {
        try
        {
            if (connection != null)
            {
                connection.close();
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to disconnect from the database: " + e.getMessage());
        }
    }

    public void checkTables()
    {
        try
        {
            DatabaseMetaData meta = connection.getMetaData();
            if (tableExists(meta, "bans"))
            {
                try
                {
                    connection.createStatement().execute("CREATE TABLE `bans` (`name` VARCHAR, `uuid` VARCHAR, `ips` VARCHAR, `by` VARCHAR NOT NULL, `at` LONG NOT NULL, `expires` LONG, `reason` VARCHAR);");
                }
                catch (SQLException e)
                {
                    FLog.severe("Failed to create the bans table: " + e.getMessage());
                }
            }

            if (tableExists(meta, "admins"))
            {
                try
                {
                    connection.createStatement().execute("CREATE TABLE `admins` (`uuid` VARCHAR NOT NULL, `rank` VARCHAR NOT NULL, `active` BOOLEAN NOT NULL, `last_login` LONG NOT NULL, `command_spy` BOOLEAN NOT NULL, `potion_spy` BOOLEAN NOT NULL, `ac_format` VARCHAR, `ptero_id` VARCHAR);");
                }
                catch (SQLException e)
                {
                    FLog.severe("Failed to create the admins table: " + e.getMessage());
                }
            }
            if (tableExists(meta, "players"))
            {
                try
                {
                    connection.createStatement().execute("CREATE TABLE `players` (`uuid` VARCHAR NOT NULL, `ips` VARCHAR NOT NULL, `notes` VARCHAR, `tag` VARCHAR, `discord_id` VARCHAR, `backup_codes` VARCHAR, `master_builder` BOOLEAN NOT NULL,`verification` BOOLEAN NOT NULL, `ride_mode` VARCHAR NOT NULL, `coins` INT, `items` VARCHAR, `total_votes` INT NOT NULL, `display_discord` BOOLEAN NOT NULL, `login_message` VARCHAR, `inspect` BOOLEAN NOT NULL);");
                }
                catch (SQLException e)
                {
                    FLog.severe("Failed to create the players table: " + e.getMessage());
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to check tables on database: " + e.getMessage());
        }
    }

    public void truncate(String table)
    {
        try
        {
            connection.createStatement().execute("DELETE FROM " + table);
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to truncate " + table + ": " + e.getMessage());
        }
    }

    public ResultSet getBanList() throws SQLException
    {
        return connection.createStatement().executeQuery("SELECT * FROM bans");
    }

    public ResultSet getAdminList() throws SQLException
    {
        return connection.createStatement().executeQuery("SELECT * FROM admins");
    }

    public void setAdminValue(Admin admin, String key, Object value)
    {
        try
        {
            Object[] data = {key, admin.getUuid()};
            PreparedStatement statement = connection.prepareStatement(MessageFormat.format("UPDATE admins SET {0}=? WHERE uuid=''{1}''", data));
            statement = setUnknownType(statement, 1, value);
            statement.executeUpdate();

        }
        catch (SQLException e)
        {
            FLog.severe("Failed to update admin value:");
            FLog.severe(e);
        }
    }

    public void setPlayerValue(PlayerData player, String key, Object value)
    {
        try
        {
            Object[] data = {key, player.getUuid()};
            PreparedStatement statement = connection.prepareStatement(MessageFormat.format("UPDATE players SET {0}=? WHERE uuid=''{1}''", data));
            statement = setUnknownType(statement, 1, value);
            statement.executeUpdate();

        }
        catch (SQLException e)
        {
            FLog.severe("Failed to update player value: " + e.getMessage());
        }
    }

    public PreparedStatement setUnknownType(PreparedStatement statement, int index, Object value) throws SQLException
    {
        if (value == null)
        {
            statement.setString(index, null);
        }
        else if (value.getClass().equals(String.class))
        {
            String v = (String)value;
            statement.setString(index, v);
        }
        else if (value.getClass().equals(Integer.class))
        {
            int v = (int)value;
            statement.setInt(index, v);
        }
        else if (value.getClass().equals(Boolean.class))
        {
            boolean v = (boolean)value;
            statement.setBoolean(index, v);
        }
        else if (value.getClass().equals(Long.class))
        {
            long v = (long)value;
            statement.setLong(index, v);
        }
        return statement;
    }

    public Object getValue(ResultSet resultSet, String key, Object value) throws SQLException
    {
        Object result = null;
        if (value instanceof String)
        {
            result = resultSet.getString(key);
        }
        else if (value instanceof Integer)
        {
            result = resultSet.getInt(key);
        }
        else if (value instanceof Boolean)
        {
            result = resultSet.getObject(key);
        }
        else if (value instanceof Long)
        {
            result = resultSet.getLong(key);
        }
        return result;
    }

    public void addAdmin(Admin admin)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO admins VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, admin.getUuid().toString());
            statement.setString(2, admin.getRank().toString());
            statement.setBoolean(3, admin.isActive());
            statement.setLong(4, admin.getLastLogin().getTime());
            statement.setBoolean(5, admin.getCommandSpy());
            statement.setBoolean(6, admin.getPotionSpy());
            statement.setString(7, admin.getAcFormat());
            statement.setString(8, admin.getPteroID());
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to add admin:");
            FLog.severe(e);
        }
    }

    public void addPlayer(PlayerData player)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO players VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, player.getUuid().toString());
            statement.setString(2, FUtil.listToString(player.getIps()));
            statement.setString(3, FUtil.listToString(player.getNotes()));
            statement.setString(4, player.getTag());
            statement.setString(5, player.getDiscordID());
            statement.setString(6, FUtil.listToString(player.getBackupCodes()));
            statement.setBoolean(7, player.isMasterBuilder());
            statement.setBoolean(8, player.hasVerification());
            statement.setString(9, player.getRideMode());
            statement.setInt(10, player.getCoins());
            statement.setString(11, FUtil.listToString(player.getItems()));
            statement.setInt(12, player.getTotalVotes());
            statement.setBoolean(13, player.doesDisplayDiscord());
            statement.setString(14, player.getLoginMessage());
            statement.setBoolean(15, player.hasInspection());
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to add player:");
            FLog.severe(e);
        }
    }

    public ResultSet getAdminByName(String name)
    {
        UUID uuid = server.getOfflinePlayer(name).getUniqueId();
        try
        {
            ResultSet resultSet = connection.createStatement().executeQuery(MessageFormat.format("SELECT * FROM admins WHERE uuid=''{0}''", uuid.toString()));
            if (resultSet.next())
            {
                return resultSet;
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to get admin by name:");
            FLog.severe(e);
        }

        return null;
    }

    public ResultSet getPlayerByName(String name)
    {
        UUID uuid = server.getOfflinePlayer(name).getUniqueId();
        try
        {
            ResultSet resultSet = connection.createStatement().executeQuery(MessageFormat.format("SELECT * FROM players WHERE uuid=''{0}''", uuid));
            if (resultSet.next())
            {
                return resultSet;
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to get player by name:");
            FLog.severe(e);
        }

        return null;
    }

    public ResultSet getMasterBuilders()
    {
        try
        {
            return connection.createStatement().executeQuery("SELECT * FROM players WHERE master_builder=true");
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to get Master Builders:");
            FLog.severe(e);
        }

        return null;
    }

    public ResultSet getPlayerByUuid(UUID uuid)
    {
        try
        {
            ResultSet resultSet = connection.createStatement().executeQuery(MessageFormat.format("SELECT * FROM players WHERE uuid=''{0}''", uuid.toString()));
            if (resultSet.next())
            {
                return resultSet;
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to get player by uuid:");
            FLog.severe(e);
        }

        return null;
    }

    public ResultSet getPlayerByIp(String ip)
    {
        try
        {
            ResultSet resultSet = connection.createStatement().executeQuery(MessageFormat.format("SELECT * FROM players WHERE ips LIKE ''%{0}%''", ip));
            if (resultSet.next())
            {
                return resultSet;
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to get player by ip:");
            FLog.severe(e);
        }

        return null;
    }

    public void removeAdmin(Admin admin)
    {
        try
        {
            connection.createStatement().executeUpdate(MessageFormat.format("DELETE FROM admins where name=''{0}''", admin.getName()));
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to remove admin:");
            FLog.severe(e);
        }
    }

    public void addBan(Ban ban)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO bans VALUES (?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, ban.getUsername());
            String uuid = null;
            if (ban.hasUUID())
            {
                uuid = ban.getUuid().toString();
            }
            statement.setString(2, uuid);
            statement.setString(3, FUtil.listToString(ban.getIps()));
            statement.setString(4, ban.getBy());
            statement.setLong(5, ban.getAt().getTime());
            statement.setLong(6, ban.getExpiryUnix());
            statement.setString(7, ban.getReason());
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to add ban: " + e.getMessage());
        }
    }

    public void removeBan(Ban ban)
    {
        try
        {
            connection.createStatement().executeUpdate(MessageFormat.format("DELETE FROM bans WHERE name=''{0}''", ban.getUsername()));
            for (String ip : ban.getIps())
            {
                connection.createStatement().executeUpdate(MessageFormat.format("DELETE FROM bans WHERE ips LIKE ''%{0}%''", ip));
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to remove ban: " + e.getMessage());
        }
    }

    public boolean tableExists(DatabaseMetaData meta, String name) throws SQLException
    {
        return !meta.getTables(null, null, name, null).next();
    }

    public boolean convertTables()
    {
        try
        {
            DatabaseMetaData meta = connection.getMetaData();

            // Legacy admin list check
            ResultSet adminColumns = meta.getColumns(null, null, "admins", "username");
            if (adminColumns.next())
            {
                FLog.info("Converting legacy admin data");

                // The current SQL database contains legacy data. Let's fix that.
                connection.createStatement().execute("ALTER TABLE `admins` RENAME TO `admins_backup`");
                connection.createStatement().execute("CREATE TABLE `admins` (`uuid` VARCHAR NOT NULL, `rank` VARCHAR NOT NULL, `active` BOOLEAN NOT NULL, `last_login` LONG NOT NULL, `command_spy` BOOLEAN NOT NULL, `potion_spy` BOOLEAN NOT NULL, `ac_format` VARCHAR, `ptero_id` VARCHAR);");

                ResultSet admins = connection.createStatement().executeQuery("SELECT * FROM `admins_backup`");
                while (admins.next())
                {
                    LegacyAdmin oldData = new LegacyAdmin(admins);
                    FLog.debug("Converting legacy entry for " + oldData.getName());
                    addAdmin(new Admin(oldData));
                }

                FLog.info("Legacy admin data conversion completed");
                FLog.info("In case something went wrong, you can recover the old data stored in `admins_backup`.");
            }

            // Legacy player list check
            ResultSet playerColumns = meta.getColumns(null, null, "players", "username");
            if (playerColumns.next())
            {
                FLog.info("Converting legacy player data");

                connection.createStatement().execute("ALTER TABLE `players` RENAME TO `players_backup`");
                connection.createStatement().execute("CREATE TABLE `players` (`uuid` VARCHAR NOT NULL, `ips` VARCHAR NOT NULL, `notes` VARCHAR, `tag` VARCHAR, `discord_id` VARCHAR, `backup_codes` VARCHAR, `master_builder` BOOLEAN NOT NULL,`verification` BOOLEAN NOT NULL, `ride_mode` VARCHAR NOT NULL, `coins` INT, `items` VARCHAR, `total_votes` INT NOT NULL, `display_discord` BOOLEAN NOT NULL, `login_message` VARCHAR, `inspect` BOOLEAN NOT NULL);");

                List<LegacyPlayerData> toConvert = new ArrayList<>();

                ResultSet players = connection.createStatement().executeQuery("SELECT * FROM `players_backup`");
                int so_far = 0;
                int size = players.getFetchSize();
                while (players.next())
                {
                    LegacyPlayerData oldPlData = new LegacyPlayerData(players);
                    toConvert.add(oldPlData);
                    FLog.debug("Adding " + oldPlData.getName() + " to the list of things to be converted (" + so_far + "/" + size + ")");
                    so_far++;
                }

                int completed = 0;
                for (LegacyPlayerData data : toConvert)
                {
                    FLog.debug("Converting legacy player entry for " + data.getName() + " (" + completed + "/" + toConvert.size() + ")");
                    addPlayer(new PlayerData(data));
                    completed++;
                }

                FLog.info("Legacy player data conversion completed");
                FLog.info("In case something went wrong, you can recover the old data stored in `players_backup`.");
            }
        }
        catch (SQLException ex)
        {
            FLog.severe("Failed to convert legacy data");
            FLog.severe(ex);
            return false;
        }

        return true;
    }
}