package me.totalfreedom.totalfreedommod.util;

public class VersionMeta
{
    private String id;
    private String name;
    private String release_target;
    private int world_version;
    private String build_time;
    private boolean stable;

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getReleaseTarget()
    {
        return release_target;
    }

    public int getWorldVersion()
    {
        return world_version;
    }

    public String getBuildTime()
    {
        return build_time;
    }

    public boolean isStable()
    {
        return stable;
    }
}
