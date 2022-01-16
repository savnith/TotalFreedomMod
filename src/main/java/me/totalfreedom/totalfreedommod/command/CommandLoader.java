package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import me.totalfreedom.totalfreedommod.FreedomService;
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
            // This prevents commands dependent on external plugins from being registered.
            if (commandClass.isAnnotationPresent(CommandRequires.class))
            {
                String[] requiredPlugins = commandClass.getAnnotation(CommandRequires.class).value();
                for (String pl : requiredPlugins)
                {
                    // If a plugin required by the command isn't present, the command is ignored
                    if (!server.getPluginManager().isPluginEnabled(pl))
                    {
                        FLog.warning("Ignoring command with unmet dependencies (" + pl + "):" + " /" + commandClass.getSimpleName().replace("Command_", ""));
                        continue registerer;
                    }
                }
            }

            try
            {
                add(commandClass.newInstance());
            }
            catch (InstantiationException | IllegalAccessException | ExceptionInInitializerError ex)
            {
                FLog.warning("Failed to register command: /" + commandClass.getSimpleName().replace("Command_", ""));
            }
        }

        FLog.info("Loaded " + commands.size() + " commands");
    }

    public List<FreedomCommand> getCommands()
    {
        return commands;
    }
}