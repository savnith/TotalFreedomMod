package me.totalfreedom.totalfreedommod.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

public class Groups
{
    public static final List<Material> BANNERS = new ArrayList<>();
    public static final List<EntityType> MOB_TYPES = new ArrayList<>();
    public static final List<Material> SHULKER_BOXES = new ArrayList<>();
    public static final List<Material> SPAWN_EGGS = new ArrayList<>();
    public static final List<Material> WOOL_COLORS = new ArrayList<>();

    static
    {
        Arrays.stream(Material.values()).filter(m -> m.name().toUpperCase().endsWith("BANNER")).forEach(BANNERS::add);
        Arrays.stream(Material.values()).filter(m -> m.name().toUpperCase().endsWith("_WOOL")).forEach(WOOL_COLORS::add);
        Arrays.stream(Material.values()).filter(m -> m.name().toUpperCase().endsWith("SHULKER_BOX")).forEach(SHULKER_BOXES::add);
        Arrays.stream(Material.values()).filter(m -> m.name().toUpperCase().endsWith("SPAWN_EGG")).forEach(SPAWN_EGGS::add);
        Arrays.stream(EntityType.values()).filter(EntityType::isAlive).forEach(MOB_TYPES::add);
    }

    public static final List<Biome> EXPLOSIVE_BED_BIOMES = Arrays.asList(
            Biome.NETHER_WASTES,
            Biome.CRIMSON_FOREST,
            Biome.SOUL_SAND_VALLEY,
            Biome.WARPED_FOREST,
            Biome.BASALT_DELTAS,
            Biome.END_BARRENS,
            Biome.END_HIGHLANDS,
            Biome.END_MIDLANDS,
            Biome.THE_END,
            Biome.SMALL_END_ISLANDS);
}
