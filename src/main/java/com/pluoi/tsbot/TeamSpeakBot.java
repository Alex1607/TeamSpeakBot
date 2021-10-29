package com.pluoi.tsbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.pluoi.tsbot.command.CommandListener;
import com.pluoi.tsbot.command.CommandManager;
import com.pluoi.tsbot.event.EventManager;
import com.pluoi.tsbot.event.eventhandler.ChannelSpam;
import com.pluoi.tsbot.scheduler.SchedulerManager;
import com.pluoi.tsbot.utils.ConfigHelper;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.logging.Level;

public class TeamSpeakBot {
    public static TS3Api api = null;

    private static YamlFile config;
    private static TeamSpeakBot instance;
    private static CommandManager commandManager;
    private static ConfigHelper configHelper;
    private static CommandListener commandListener;
    private final TS3Config tsconfig;
    private final TS3Query query;
    private final Logger logger = new Logger();

    private TeamSpeakBot() {
        logger.write("Starting...", -1);
        logger.write("Loading ConfigHelper...", -1);
        config = configHelper.getConfig("ts3bot.yml");
        logger.write("ConfigHelper loaded", -1);
        instance = this;
        tsconfig = new TS3Config().setDebugLevel(Level.OFF);
        query = new TS3Query(tsconfig);
        api = query.getApi();
        tsconfig.setDebugLevel(Level.OFF);
        logger.debug("Connection to: " + getConfig().getString("settings.ip"));
        tsconfig.setHost(getConfig().getString("settings.ip"));
        tsconfig.setFloodRate(TS3Query.FloodRate.UNLIMITED);
        tsconfig.setReconnectStrategy(ReconnectStrategy.constantBackoff());
        tsconfig.setConnectionHandler(new ConnectionHandler() {

            @Override
            public void onConnect(TS3Query ts3Query) {
                // Nichts
            }

            @Override
            public void onDisconnect(TS3Query ts3Query) {
                ChannelSpam.resetTimer();
            }
        });

        query.connect();

        tsconfig.setDebugLevel(Level.OFF);

        api.login(getConfig().getString("settings.username"), getConfig().getString("settings.password"));
        api.selectVirtualServerById(1);
        api.setNickname(getConfig().getString("settings.nickname"));

        api.registerEvent(TS3EventType.TEXT_PRIVATE);
        api.registerEvent(TS3EventType.SERVER);
        api.registerEvent(TS3EventType.CHANNEL);

        //tsconfig.setDebugLevel(Level.ALL);

        logger.write("Connected!", -1);
    }

    public static void main(String[] args) {
        configHelper = new ConfigHelper();

        new TeamSpeakBot();

        commandManager = new CommandManager();
        new EventManager();
        new SchedulerManager();

        commandListener = new CommandListener();
        commandListener.start();
    }

    public static TeamSpeakBot getInstance() {
        return instance;
    }

    public static YamlFile getConfig() {
        return config;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }
}
