package com.bgcommands;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Mod(modid = BGCommands.MODID, name = BGCommands.NAME, version = BGCommands.VERSION)
public class BGCommands {
    public static final String MODID = "bgcommands";
    public static final String NAME = "Background Commands";
    public static final String VERSION = "1.0";

    private static List<String> commands = new ArrayList<>();
    private static List<String> setupCommands = new ArrayList<>();
    private static int tickInterval = 20;
    private static boolean showLogs = true;
    private int tickCounter = 0;
    private Set<Integer> initializedWorlds = new HashSet<>();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        String[] commandArray = config.getStringList(
                "commands",
                "general",
                new String[]{},
                "Commands to run repeatedly in the background. Add one command per line."
        );

        String[] setupCommandArray = config.getStringList(
                "setupCommands",
                "general",
                new String[]{},
                "Commands to run ONCE when a world is first loaded. Add one command per line."
        );

        tickInterval = config.getInt(
                "tickInterval",
                "general",
                20,
                1,
                Integer.MAX_VALUE,
                "How many ticks between command executions (20 ticks = 1 second)"
        );

        showLogs = config.getBoolean(
                "showLogs",
                "general",
                true,
                "Whether to show command execution logs in the server console. To hide command feedback in game chat, add 'gamerule sendCommandFeedback false' to setupCommands."
        );

        for (String cmd : commandArray) {
            if (!cmd.isEmpty()) {
                commands.add(cmd);
            }
        }

        for (String cmd : setupCommandArray) {
            if (!cmd.isEmpty()) {
                setupCommands.add(cmd);
            }
        }

        config.save();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            int dimensionId = event.getWorld().provider.getDimension();

            if (!initializedWorlds.contains(dimensionId)) {
                initializedWorlds.add(dimensionId);
                executeSetupCommands();
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;
            if (tickCounter >= tickInterval) {
                tickCounter = 0;
                executeCommands();
            }
        }
    }

    private void executeSetupCommands() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null && !setupCommands.isEmpty()) {
            if (showLogs) {
                System.out.println("[BGCommands] Running setup commands...");
            }
            for (String command : setupCommands) {
                if (showLogs) {
                    System.out.println("[BGCommands] Running: " + command);
                }
                server.getCommandManager().executeCommand(server, command);
            }
        }
    }

    private void executeCommands() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            for (String command : commands) {
                server.getCommandManager().executeCommand(server, command);
            }
        }
    }
}