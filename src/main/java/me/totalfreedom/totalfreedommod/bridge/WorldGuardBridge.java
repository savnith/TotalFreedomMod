package me.totalfreedom.totalfreedommod.bridge;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import java.util.HashMap;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class WorldGuardBridge extends FreedomService
{
    @Override
    public void onStart()
    {
        if (isEnabled())
        {
            protectWorld(plugin.wm.masterBuilderWorld.getWorld());
        }
    }

    @Override
    public void onStop()
    {
    }

    public RegionManager getRegionManager(World world)
    {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(world));
    }

    public void protectWorld(World world)
    {
        if (!isEnabled())
        {
            return;
        }

        final Map<Flag<?>, Object> flags = new HashMap<Flag<?>, Object>()
        {{
            put(Flags.PLACE_VEHICLE, StateFlag.State.DENY);
            put(Flags.DESTROY_VEHICLE, StateFlag.State.DENY);
            put(Flags.ENTITY_ITEM_FRAME_DESTROY, StateFlag.State.DENY);
            put(Flags.ENTITY_PAINTING_DESTROY, StateFlag.State.DENY);
        }};

        RegionManager regionManager = plugin.wgb.getRegionManager(world);

        GlobalProtectedRegion region = new GlobalProtectedRegion("__global__");

        region.setFlags(flags);

        regionManager.addRegion(region);
    }

    public boolean hasExtraFlags()
    {
        Plugin plugin = server.getPluginManager().getPlugin("WorldGuardExtraFlags");

        return plugin != null && plugin.isEnabled();
    }

    public boolean isEnabled()
    {
        Plugin plugin = server.getPluginManager().getPlugin("WorldGuard");

        return plugin != null && plugin.isEnabled();
    }
}