package io.github.mcengine.extension.skript.essential.example;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.extension.logger.MCEngineExtensionLogger;
import io.github.mcengine.api.essential.extension.skript.IMCEngineEssentialSkript;

import io.github.mcengine.extension.skript.essential.example.command.EssentialSkriptCommand;
import io.github.mcengine.extension.skript.essential.example.listener.EssentialSkriptListener;
import io.github.mcengine.extension.skript.essential.example.tabcompleter.EssentialSkriptTabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Main class for the Essential <b>Skript</b> example module.
 * <p>
 * Registers the {@code /essentialskriptexample} command and related event listeners.
 * <p>
 * Integrates with the {@link IMCEngineEssentialSkript} lifecycle.
 */
public class ExampleEssentialSkript implements IMCEngineEssentialSkript {

    /** Custom extension logger for this module, with contextual labeling. */
    private MCEngineExtensionLogger logger;

    /**
     * Initializes the Essential Skript example module.
     * Called automatically by the MCEngine core plugin.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onLoad(Plugin plugin) {
        // Initialize contextual logger once and keep it for later use.
        this.logger = new MCEngineExtensionLogger(plugin, "Skript", "EssentialExampleSkript");

        try {
            // Register event listener
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(new EssentialSkriptListener(plugin, this.logger), plugin);

            // Reflectively access Bukkit's CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // Define the /essentialskriptexample command
            Command essentialSkriptExampleCommand = new Command("essentialskriptexample") {

                /** Handles command execution for /essentialskriptexample. */
                private final EssentialSkriptCommand handler = new EssentialSkriptCommand();

                /** Handles tab-completion for /essentialskriptexample. */
                private final EssentialSkriptTabCompleter completer = new EssentialSkriptTabCompleter();

                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return handler.onCommand(sender, this, label, args);
                }

                @Override
                public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                    return completer.onTabComplete(sender, this, alias, args);
                }
            };

            essentialSkriptExampleCommand.setDescription("Essential Skript example command.");
            essentialSkriptExampleCommand.setUsage("/essentialskriptexample");

            // Dynamically register the /essentialskriptexample command
            commandMap.register(plugin.getName().toLowerCase(), essentialSkriptExampleCommand);

            this.logger.info("Enabled successfully.");
        } catch (Exception e) {
            this.logger.warning("Failed to initialize ExampleEssentialSkript: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the Essential Skript example module is disabled/unloaded.
     * No explicit unregistration is required for the anonymous Command here.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onDisload(Plugin plugin) {
        if (this.logger != null) {
            this.logger.info("Disabled.");
        }
    }

    /**
     * Sets the unique ID for this module.
     *
     * @param id the assigned identifier (ignored; a fixed ID is used for consistency)
     */
    @Override
    public void setId(String id) {
        MCEngineCoreApi.setId("mcengine-essential-skript-example");
    }
}
