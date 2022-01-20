package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.reflections.Reflections;

public class CommandLoader extends FreedomService
{
    private final List<FreedomCommand> commands;

    public CommandLoader()
    {
        commands = new ArrayList<>();
    }

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public void add(FreedomCommand command)
    {
        commands.add(command);
        command.register();
    }

    public FreedomCommand getByName(String name)
    {
        for (FreedomCommand command : commands)
        {
            if (name.equals(command.getName()))
            {
                return command;
            }
        }
        return null;
    }

    public boolean isAlias(String alias)
    {
        for (FreedomCommand command : commands)
        {
            if (Arrays.asList(command.getAliases().split(",")).contains(alias))
            {
                return true;
            }
        }
        return false;
    }

    public void loadCommands()
    {
        Reflections commandDir = new Reflections("me.totalfreedom.totalfreedommod.command");

        Set<Class<? extends FreedomCommand>> commandClasses = commandDir.getSubTypesOf(FreedomCommand.class);

        registerer:
        for (Class<? extends FreedomCommand> commandClass : commandClasses)
        {
            String cmdName = commandClass.getSimpleName().replace("Command_", "");

            // Don't load commands if they are marked as ignored in the config unless they are /tfm.
            if (!cmdName.equalsIgnoreCase("totalfreedommod")
                    && ConfigEntry.CL_IGNORE.getStringList().contains(cmdName))
            {
                continue;
            }

            // This prevents commands dependent on external plugins from being registered.
            if (!ConfigEntry.CL_FORCE_DEPENDENCYLESS_COMMANDS.getBoolean())
            {
                if (commandClass.isAnnotationPresent(CommandRequires.class))
                {
                    String[] requiredPlugins = commandClass.getAnnotation(CommandRequires.class).value();
                    for (String pl : requiredPlugins)
                    {
                        // If a plugin required by the command isn't present, the command is ignored
                        if (!server.getPluginManager().isPluginEnabled(pl))
                        {
                            FLog.warning("Ignoring command with unmet dependencies (" + pl + "):" + " /" + cmdName);
                            continue registerer;
                        }
                    }
                }
            }

            try
            {
                add(commandClass.newInstance());
            }
            catch (InstantiationException | IllegalAccessException | ExceptionInInitializerError ex)
            {
                FLog.warning("Failed to register command: /" + cmdName);
            }
        }

        FLog.info("Loaded " + commands.size() + " commands");
    }

    public List<FreedomCommand> getCommands()
    {
        return commands;
    }
}