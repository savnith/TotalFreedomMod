package me.totalfreedom.totalfreedommod;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import me.totalfreedom.totalfreedommod.admin.ActivityLog;
import me.totalfreedom.totalfreedommod.admin.AdminList;
import me.totalfreedom.totalfreedommod.banning.BanManager;
import me.totalfreedom.totalfreedommod.banning.IndefiniteBanList;
import me.totalfreedom.totalfreedommod.blocking.BlockBlocker;
import me.totalfreedom.totalfreedommod.blocking.EditBlocker;
import me.totalfreedom.totalfreedommod.blocking.EventBlocker;
import me.totalfreedom.totalfreedommod.blocking.InteractBlocker;
import me.totalfreedom.totalfreedommod.blocking.MobBlocker;
import me.totalfreedom.totalfreedommod.blocking.PVPBlocker;
import me.totalfreedom.totalfreedommod.blocking.PotionBlocker;
import me.totalfreedom.totalfreedommod.blocking.SignBlocker;
import me.totalfreedom.totalfreedommod.blocking.command.CommandBlocker;
import me.totalfreedom.totalfreedommod.bridge.BukkitTelnetBridge;
import me.totalfreedom.totalfreedommod.bridge.CoreProtectBridge;
import me.totalfreedom.totalfreedommod.bridge.EssentialsBridge;
import me.totalfreedom.totalfreedommod.bridge.LibsDisguisesBridge;
import me.totalfreedom.totalfreedommod.bridge.TFGuildsBridge;
import me.totalfreedom.totalfreedommod.bridge.WorldEditBridge;
import me.totalfreedom.totalfreedommod.bridge.WorldGuardBridge;
import me.totalfreedom.totalfreedommod.caging.Cager;
import me.totalfreedom.totalfreedommod.command.CommandLoader;
import me.totalfreedom.totalfreedommod.config.MainConfig;
import me.totalfreedom.totalfreedommod.discord.Discord;
import me.totalfreedom.totalfreedommod.freeze.Freezer;
import me.totalfreedom.totalfreedommod.fun.ItemFun;
import me.totalfreedom.totalfreedommod.fun.Jumppads;
import me.totalfreedom.totalfreedommod.fun.Landminer;
import me.totalfreedom.totalfreedommod.fun.MP44;
import me.totalfreedom.totalfreedommod.fun.Trailer;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.permissions.PermissionConfig;
import me.totalfreedom.totalfreedommod.permissions.PermissionManager;
import me.totalfreedom.totalfreedommod.player.PlayerList;
import me.totalfreedom.totalfreedommod.punishments.PunishmentList;
import me.totalfreedom.totalfreedommod.rank.RankManager;
import me.totalfreedom.totalfreedommod.shop.Shop;
import me.totalfreedom.totalfreedommod.shop.Votifier;
import me.totalfreedom.totalfreedommod.sql.SQLite;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.util.MethodTimer;
import me.totalfreedom.totalfreedommod.world.CleanroomChunkGenerator;
import me.totalfreedom.totalfreedommod.world.WorldManager;
import me.totalfreedom.totalfreedommod.world.WorldRestrictions;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.SpigotConfig;

public class TotalFreedomMod extends JavaPlugin
{
    public static final String CONFIG_FILENAME = "config.yml";
    //
    public static final BuildProperties build = new BuildProperties();
    //
    public static String pluginName;
    public static String pluginVersion;
    private static TotalFreedomMod plugin;
    //
    public MainConfig config;
    public PermissionConfig permissions;
    //
    // Service Handler
    public FreedomServiceHandler fsh;
    // Command Loader
    public CommandLoader cl;
    // Services
    public ServerInterface si;
    public WorldManager wm;
    public LogViewer lv;
    public AdminList al;
    public ActivityLog acl;
    public RankManager rm;
    public CommandBlocker cb;
    public EventBlocker eb;
    public BlockBlocker bb;
    public MobBlocker mb;
    public InteractBlocker ib;
    public PotionBlocker pb;
    public LoginProcess lp;
    public AntiNuke nu;
    public AntiSpam as;
    public PlayerList pl;
    public Shop sh;
    public Votifier vo;
    public SQLite sql;
    public Announcer an;
    public ChatManager cm;
    public Discord dc;
    public PunishmentList pul;
    public BanManager bm;
    public IndefiniteBanList im;
    public PermissionManager pem;
    public GameRuleHandler gr;
    public CommandSpy cs;
    public Cager ca;
    public Freezer fm;
    public EditBlocker ebl;
    public PVPBlocker pbl;
    public Orbiter or;
    public Muter mu;
    public Fuckoff fo;
    public AutoKick ak;
    public AutoEject ae;
    public Monitors mo;
    public MovementValidator mv;
    public ServerPing sp;
    public ItemFun it;
    public Landminer lm;
    public MP44 mp;
    public Jumppads jp;
    public Trailer tr;
    public HTTPDaemon hd;
    public WorldRestrictions wr;
    public SignBlocker snp;
    public EntityWiper ew;
    public Sitter st;
    public VanishHandler vh;
    public Pterodactyl ptero;
    //
    // Bridges
    public BukkitTelnetBridge btb;
    public EssentialsBridge esb;
    public LibsDisguisesBridge ldb;
    public CoreProtectBridge cpb;
    public TFGuildsBridge tfg;
    public WorldEditBridge web;
    public WorldGuardBridge wgb;

    public static TotalFreedomMod getPlugin()
    {
        return plugin;
    }

    public static TotalFreedomMod plugin()
    {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
        {
            if (plugin.getName().equalsIgnoreCase(pluginName))
            {
                return (TotalFreedomMod)plugin;
            }
        }
        return null;
    }

    @Override
    public void onLoad()
    {
        plugin = this;
        TotalFreedomMod.pluginName = plugin.getDescription().getName();
        TotalFreedomMod.pluginVersion = plugin.getDescription().getVersion();

        FLog.setPluginLogger(plugin.getLogger());
        FLog.setServerLogger(getServer().getLogger());

        build.load(plugin);
    }

    @Override
    public void onEnable()
    {
        FLog.info("Created by Madgeek1450 and Prozza");
        FLog.info("Version " + build.version);
        FLog.info("Compiled " + build.date + " by " + build.author);

        final MethodTimer timer = new MethodTimer();
        timer.start();

        // Warn if we're running on a wrong version
        ServerInterface.warnVersion();

        // Delete unused files
        FUtil.deleteCoreDumps();
        FUtil.deleteFolder(new File("./_deleteme"));

        fsh = new FreedomServiceHandler();

        config = new MainConfig();

        if (FUtil.inDeveloperMode())
        {
            FLog.debug("Developer mode enabled.");
        }

        cl = new CommandLoader();
        cl.loadCommands();

        BackupManager backups = new BackupManager();
        backups.createAllBackups();

        permissions = new PermissionConfig();
        permissions.load();

        mv = new MovementValidator();
        sp = new ServerPing();

        new Initializer();

        fsh.startServices();

        FLog.info("Started " + fsh.getServiceAmount() + " services.");

        timer.update();
        FLog.info("Version " + pluginVersion + " for " + ServerInterface.COMPILE_NMS_VERSION + " enabled in " + timer.getTotal() + "ms");

        // Metrics @ https://bstats.org/plugin/bukkit/TotalFreedomMod/2966
        new Metrics(this, 2966);

        // little workaround to stop spigot from autorestarting - causing AMP to detach from process.
        SpigotConfig.config.set("settings.restart-on-crash", false);
    }

    @Override
    public void onDisable()
    {
        // Stop services and bridges
        fsh.stopServices();

        getServer().getScheduler().cancelTasks(plugin);

        FLog.info("Plugin disabled");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id)
    {
        return new CleanroomChunkGenerator(id);
    }

    public static class BuildProperties
    {
        public String author;
        public String codename;
        public String version;
        public String number;
        public String date;
        public String head;

        public void load(TotalFreedomMod plugin)
        {
            try
            {
                final Properties props;

                try (InputStream in = plugin.getResource("build.properties"))
                {
                    props = new Properties();
                    props.load(in);
                }

                author = props.getProperty("buildAuthor", "unknown");
                codename = props.getProperty("buildCodeName", "unknown");
                version = props.getProperty("buildVersion", pluginVersion);
                number = props.getProperty("buildNumber", "1");
                date = props.getProperty("buildDate", "unknown");
                // Need to do this or it will display ${git.commit.id.abbrev}
                head = props.getProperty("buildHead", "unknown").replace("${git.commit.id.abbrev}", "unknown");
            }
            catch (Exception ex)
            {
                FLog.severe("Could not load build properties! Did you compile with NetBeans/Maven?");
                FLog.severe(ex);
            }
        }

        public String formattedVersion()
        {
            return pluginVersion + "." + number + " (" + head + ")";
        }
    }

    /**
     * This class is provided to please Codacy.
     */
    private final class Initializer
    {
        public Initializer()
        {
            initServices();
            initAdminUtils();
            initBridges();
            initFun();
            initHTTPD();
        }

        private void initServices()
        {
            // Start services
            si = new ServerInterface();
            wm = new WorldManager();
            lv = new LogViewer();
            sql = new SQLite();
            al = new AdminList();
            acl = new ActivityLog();
            rm = new RankManager();
            cb = new CommandBlocker();
            eb = new EventBlocker();
            bb = new BlockBlocker();
            mb = new MobBlocker();
            ib = new InteractBlocker();
            pb = new PotionBlocker();
            lp = new LoginProcess();
            nu = new AntiNuke();
            as = new AntiSpam();
            wr = new WorldRestrictions();
            pl = new PlayerList();
            sh = new Shop();
            vo = new Votifier();
            an = new Announcer();
            cm = new ChatManager();
            dc = new Discord();
            pul = new PunishmentList();
            bm = new BanManager();
            im = new IndefiniteBanList();
            pem = new PermissionManager();
            gr = new GameRuleHandler();
            snp = new SignBlocker();
            ew = new EntityWiper();
            st = new Sitter();
            vh = new VanishHandler();
            ptero = new Pterodactyl();
        }

        private void initAdminUtils()
        {
            // Single admin utils
            cs = new CommandSpy();
            ca = new Cager();
            fm = new Freezer();
            or = new Orbiter();
            mu = new Muter();
            ebl = new EditBlocker();
            pbl = new PVPBlocker();
            fo = new Fuckoff();
            ak = new AutoKick();
            ae = new AutoEject();
            mo = new Monitors();
        }

        private void initBridges()
        {
            // Start bridges
            btb = new BukkitTelnetBridge();
            cpb = new CoreProtectBridge();
            esb = new EssentialsBridge();
            ldb = new LibsDisguisesBridge();
            tfg = new TFGuildsBridge();
            web = new WorldEditBridge();
            wgb = new WorldGuardBridge();
        }

        private void initFun()
        {
            // Fun
            it = new ItemFun();
            lm = new Landminer();
            mp = new MP44();
            jp = new Jumppads();
            tr = new Trailer();
        }

        private void initHTTPD()
        {
            // HTTPD
            hd = new HTTPDaemon();
        }
    }
}