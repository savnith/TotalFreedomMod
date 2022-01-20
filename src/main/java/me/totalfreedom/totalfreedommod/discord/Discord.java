package me.totalfreedom.totalfreedommod.discord;

import com.google.common.base.Strings;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.security.auth.login.LoginException;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.event.PlayerReportEvent;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.concurrent.CountingThreadFactory;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class Discord extends FreedomService
{

    public static HashMap<String, PlayerData> LINK_CODES = new HashMap<>();
    public static JDA bot = null;
    public ScheduledThreadPoolExecutor RATELIMIT_EXECUTOR;
    public List<CompletableFuture<Message>> sentMessages = new ArrayList<>();
    public Boolean enabled = false;
    private final Pattern DISCORD_MENTION_PATTERN = Pattern.compile("(<@!?([0-9]{16,20})>)");

    public static String getMD5(String string)
    {
        return DigestUtils.md5Hex(string);
    }

    public static String getCode(PlayerData playerData)
    {
        for (String code : LINK_CODES.keySet())
        {
            if (LINK_CODES.get(code).equals(playerData))
            {
                return code;
            }
        }
        return null;
    }

    public static boolean syncRoles(Admin admin, String discordID)
    {
        if (discordID == null)
        {
            return false;
        }

        Guild server = bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
        if (server == null)
        {
            FLog.severe("The Discord server ID specified is invalid, or the bot is not on the server.");
            return false;
        }

        Member member = server.getMemberById(discordID);
        if (member == null)
        {
            return false;
        }

        Role adminRole = server.getRoleById(ConfigEntry.DISCORD_NEW_ADMIN_ROLE_ID.getString());
        if (adminRole == null)
        {
            FLog.severe("The specified Admin role does not exist!");
            return false;
        }

        Role senioradminRole = server.getRoleById(ConfigEntry.DISCORD_SENIOR_ADMIN_ROLE_ID.getString());
        if (senioradminRole == null)
        {
            FLog.severe("The specified Senior Admin role does not exist!");
            return false;
        }

        if (!admin.isActive())
        {
            if (member.getRoles().contains(adminRole))
            {
                server.removeRoleFromMember(member, adminRole).complete();
            }
            if (member.getRoles().contains(senioradminRole))
            {
                server.removeRoleFromMember(member, senioradminRole).complete();
            }
            return true;
        }

        if (admin.getRank().equals(Rank.ADMIN))
        {
            if (!member.getRoles().contains(adminRole))
            {
                server.addRoleToMember(member, adminRole).complete();
            }
            if (member.getRoles().contains(senioradminRole))
            {
                server.removeRoleFromMember(member, senioradminRole).complete();
            }
            return true;
        }
        else if (admin.getRank().equals(Rank.SENIOR_ADMIN))
        {
            if (!member.getRoles().contains(senioradminRole))
            {
                server.addRoleToMember(member, senioradminRole).complete();
            }
            if (member.getRoles().contains(adminRole))
            {
                server.removeRoleFromMember(member, adminRole).complete();
            }
            return true;
        }
        return false;
    }

    public void startBot()
    {
        enabled = !Strings.isNullOrEmpty(ConfigEntry.DISCORD_TOKEN.getString());
        if (!enabled)
        {
            return;
        }

        if (bot != null)
        {
            RATELIMIT_EXECUTOR = new ScheduledThreadPoolExecutor(5, new CountingThreadFactory(this::poolIdentifier, "RateLimit"));
            RATELIMIT_EXECUTOR.setRemoveOnCancelPolicy(true);
            for (Object object : bot.getRegisteredListeners())
            {
                bot.removeEventListener(object);
            }
        }

        try
        {
            bot = JDABuilder.createDefault(ConfigEntry.DISCORD_TOKEN.getString())
                    .addEventListeners(new PrivateMessageListener(),
                            new DiscordToMinecraftListener(),
                            new DiscordToAdminChatListener(),
                            new ListenerAdapter()
                            {
                                @Override
                                public void onReady(@NotNull ReadyEvent event)
                                {
                                    new StartEvent().start();
                                }
                            })
                    .setAutoReconnect(true)
                    .setRateLimitPool(RATELIMIT_EXECUTOR)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build();
            FLog.info("Discord integration bot has successfully enabled!");
        }
        catch (LoginException e)
        {
            FLog.warning("An invalid token for the Discord integration bot was provided, the bot will not enable.");
            enabled = false;
        }
        catch (IllegalArgumentException e)
        {
            FLog.warning("Discord integration bot failed to start.");
            enabled = false;
        }
        catch (NoClassDefFoundError e)
        {
            FLog.warning("The JDA plugin is not installed, therefore the Discord bot cannot start.");
            FLog.warning("To resolve this error, please download the latest JDA from: https://github.com/TFPatches/Minecraft-JDA/releases");
            enabled = false;
        }
    }

    public String poolIdentifier()
    {
        return "JDA";
    }

    public void clearQueue()
    {
        for (CompletableFuture<Message> messages : sentMessages)
        {
            if (!messages.isDone())
            {
                messages.cancel(true);
            }
        }
        sentMessages.clear();
        messageChatChannel("**Message queue cleared**");
    }

    public void sendPteroInfo(PlayerData playerData, String username, String password)
    {
        User user = getUser(playerData.getDiscordID());
        String message = "The following are your Pterodactyl details:\n\nUsername: " + username + "\nPassword: " + password + "\n\nYou can connect to the panel at " + plugin.ptero.URL;
        PrivateChannel privateChannel = user.openPrivateChannel().complete();
        privateChannel.sendMessage(message).complete();
    }

    public User getUser(String id)
    {
        Guild guild = bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
        if (guild == null)
        {
            FLog.severe("Either the bot is not in the Discord server or it doesn't exist. Check the server ID.");
            return null;
        }

        Member member = guild.getMemberById(id);
        if (member == null)
        {
            return null;
        }

        return member.getUser();
    }

    public String generateCode(int size)
    {
        StringBuilder code = new StringBuilder();
        SplittableRandom random = new SplittableRandom();
        for (int i = 0; i < size; i++)
        {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        //Avoiding NPE Unboxing Warnings
        Boolean b = event.getEntity().getWorld().getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES);
        if (b == null || !b)
        {
            return;
        }

        if (event.getDeathMessage() != null)
        {
            messageChatChannel("**" + event.getDeathMessage() + "**");
        }
    }

    @Override
    public void onStart()
    {
        startBot();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (!plugin.al.isVanished(event.getPlayer().getName()))
        {
            messageChatChannel("**" + event.getPlayer().getName() + " joined the server" + "**");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        if (!plugin.al.isVanished(event.getPlayer().getName()))
        {
            messageChatChannel("**" + event.getPlayer().getName() + " left the server" + "**");
        }
    }

    public void messageChatChannel(String message)
    {
        String chat_channel_id = ConfigEntry.DISCORD_CHAT_CHANNEL_ID.getString();
        if (message.contains("@everyone") || message.contains("@here"))
        {
            message = StringUtils.remove(message, "@");
        }

        if (message.toLowerCase().contains("discord.gg"))
        {
            return;
        }
        
        if (message.contains("§"))
        {
            message = StringUtils.remove(message, "§");
        }


        Matcher DISCORD_MENTION_MATCHER = DISCORD_MENTION_PATTERN.matcher(message);

        while (DISCORD_MENTION_MATCHER.find())
        {
            String mention = DISCORD_MENTION_MATCHER.group(1);
            message = message.replace(mention, mention.replace("@",""));
        }

        if (enabled && !chat_channel_id.isEmpty())
        {
            CompletableFuture<Message> sentMessage = Objects.requireNonNull(bot.getTextChannelById(chat_channel_id)).sendMessage(deformat(message)).submit(true);
            sentMessages.add(sentMessage);
        }
    }

    public void messageAdminChatChannel(String message)
    {
        String chat_channel_id = ConfigEntry.DISCORD_ADMINCHAT_CHANNEL_ID.getString();
        if (message.contains("@everyone") || message.contains("@here"))
        {
            message = StringUtils.remove(message, "@");
        }

        if (message.toLowerCase().contains("discord.gg"))
        {
            return;
        }
        
        if (message.contains("§"))
        {
            message = StringUtils.remove(message, "§");
        }


        Matcher DISCORD_MENTION_MATCHER = DISCORD_MENTION_PATTERN.matcher(message);

        while (DISCORD_MENTION_MATCHER.find())
        {
            String mention = DISCORD_MENTION_MATCHER.group(1);
            message = message.replace(mention, mention.replace("@",""));
        }

        if (enabled && !chat_channel_id.isEmpty())
        {
            CompletableFuture<Message> sentMessage = Objects.requireNonNull(bot.getTextChannelById(chat_channel_id)).sendMessage(deformat(message)).submit(true);
            sentMessages.add(sentMessage);
        }
    }

    public String formatBotTag()
    {
        SelfUser user = bot.getSelfUser();
        return user.getName() + "#" + user.getDiscriminator();
    }

    @Override
    public void onStop()
    {
        if (bot != null)
        {
            messageChatChannel("**Server has stopped**");
        }

        FLog.info("Discord integration bot has successfully shutdown.");
    }

    public String deformat(String input)
    {
        return input.replace("_", "\\_");
    }

    // Do no ask why this is here. I spent two hours trying to make a simple thing work
    public class StartEvent
    {
        public void start()
        {
            messageChatChannel("**Server has started**");
        }
    }

    @EventHandler
    public void onPlayerReport(PlayerReportEvent event)
    {
        Player sender = event.getSender();
        Player target = event.getTarget();
        String reason = event.getReason();

        if (!enabled || ConfigEntry.DISCORD_REPORT_CHANNEL_ID.getString().isEmpty())
        {
            return;
        }

        if (ConfigEntry.DISCORD_SERVER_ID.getString().isEmpty())
        {
            FLog.severe("No Discord server ID was specified in the config, but there is a report channel ID.");
            return;
        }

        Guild server = bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
        if (server == null)
        {
            FLog.severe("The Discord server ID specified is invalid, or the bot is not on the server.");
            return;
        }

        TextChannel channel = server.getTextChannelById(ConfigEntry.DISCORD_REPORT_CHANNEL_ID.getString());
        if (channel == null)
        {
            FLog.severe("The report channel ID specified in the config is invalid.");
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Report for " + target.getName());
        embedBuilder.setDescription(reason);
        embedBuilder.setFooter("Reported by " + sender.getName(), "https://minotar.net/helm/" + sender.getName() + ".png");
        embedBuilder.setTimestamp(Instant.from(ZonedDateTime.now()));

        String location = "World: " + Objects.requireNonNull(target.getLocation().getWorld()).getName() + ", X: " + target.getLocation().getBlockX() + ", Y: " + target.getLocation().getBlockY() + ", Z: " + target.getLocation().getBlockZ();
        embedBuilder.addField("Location", location, true);
        embedBuilder.addField("Game Mode", WordUtils.capitalizeFully(target.getGameMode().name()), true);

        if (plugin.esb.isEnabled())
        {
            com.earth2me.essentials.User user = plugin.esb.getEssentialsUser(target.getName());
            embedBuilder.addField("God Mode", WordUtils.capitalizeFully(String.valueOf(user.isGodModeEnabled())), true);
            if (user.getNickname() != null)
            {
                embedBuilder.addField("Nickname", user.getNickname(), true);
            }
        }

        MessageEmbed embed = embedBuilder.build();
        channel.sendMessage(embed).complete();

        FUtil.playerMsg(sender, ChatColor.RED + "Note: This report has been logged to a Discord channel. As with any report system, spamming reports can lead to you getting banned.");
    }
}