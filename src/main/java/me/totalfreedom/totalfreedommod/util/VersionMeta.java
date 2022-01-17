package me.totalfreedom.totalfreedommod.util;

import lombok.Getter;

public class VersionMeta
{
    @Getter
    private String id;
    @Getter
    private String name;
    @Getter
    private String release_target;
    @Getter
    private int world_version;
    @Getter
    private int pack_version;
    @Getter
    private String build_time;
    @Getter
    private boolean stable;
}
