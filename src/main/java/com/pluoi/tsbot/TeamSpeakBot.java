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
import com.pluoi.tsbot.event.eventHandler.ChannelSpam;
import com.pluoi.tsbot.scheduler.SchedulerManager;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class TeamSpeakBot {
    public static TS3Api api = null;
    private static YamlFile config;
    private static TeamSpeakBot instance;
    List<String> temp = new ArrayList<>();
    private TS3Config tsconfig;
    private TS3Query query;

    private TeamSpeakBot() {
        Logger.write("Starting...", -1);
        Logger.write("Loading Config...", -1);
        config = new YamlFile("ts3bot.yml");
        if (!config.exists()) {
            Logger.write("Creating Config...", -1);
            config.set("settings.ip", "greev.eu");
            config.set("settings.username", "Loginname");
            config.set("settings.password", "Passwort");
            config.set("settings.nickname", "Pluoi Bot");
            config.set("settings.defaultchannel", 20);
            config.set("antichannelspam.waittime", 600000);
            config.set("antichannelspam.message", "Bitte warte noch %time% Minuten bevor du den nächsten Channel erstellst!");
            config.set("iplimit.maxclients", 3);
            config.set("iplimit.bantime", 10);
            config.set("iplimit.message", "Es sind derzeit zu viele Nutzer mit der selben IP auf dem Teamspeak!");
            config.set("randomchannels.temptime", 10000);
            config.set("botmuter.channel.normalgroup", 11);
            config.set("botmuter.channel.mutegroup", 12);
            config.set("botmuter.server.botgroup", 39);
            config.set("botmuter.server.rentgroup", 63);

            config.set("function.command.clientlist.enabled", true);
            config.set("function.command.randomchannelcreator.enabled", true);
            config.set("function.command.debug.enabled", true);
            config.set("function.command.removetempchannels.enabled", true);
            config.set("function.command.help.enabled", true);
            config.set("function.command.messageall.enabled", true);
            config.set("function.command.pokeall.enabled", true);
            config.set("function.command.tempchannelstoperm.enabled", true);

            config.set("function.event.ipbandeleter.enabled", true);
            config.set("function.event.channelspam.enabled", true);
            config.set("function.event.commands.enabled", true);
            config.set("function.event.musicbotrent.enabled", true);
            config.set("function.event.botmuter.enabled", true);
            config.set("function.event.supportbot.enabled", true);
            config.set("function.event.iplimit.enabled", true);

            temp = Arrays.asList("deltempchannels,help,clientlist".split(","));
            config.set("function.event.commands.modcommands", temp);
            config.set("function.event.commands.modgroup", 62);
            config.set("function.event.commands.admingroup", 61);

            config.set("function.scheduler.musicbotmover.enabled", true);
            config.set("function.scheduler.musicbotmuter.enabled", true);
            config.set("function.scheduler.randomcatgif.enabled", true);
            config.set("function.scheduler.supportergroup.enabled", true);
            config.set("function.scheduler.usergroup.enabled", true);

            config.set("function.scheduler.musicbotmover.timer", 60);
            config.set("function.scheduler.musicbotmuter.timer", 60);
            config.set("function.scheduler.randomcatgif.timer", 120);
            config.set("function.scheduler.supportergroup.timer", 10);
            config.set("function.scheduler.usergroup.timer", 30);

            config.set("function.scheduler.randomcatgif.channel", 19);

            config.set("function.scheduler.usergroup.group", 9);
            config.set("function.scheduler.usergroup.time", 3600000);
            temp = Arrays.asList("".split("23,9,30,31,25,29,11,12,27,39,26"));
            config.set("function.scheduler.usergroup.nothisgroup", 30);

            config.set("supportbot.group", 44);
            config.set("supportbot.waitingroom", 3632);
            config.set("supportbot.endroom", 20);
            config.set("supportbot.message.supportend", "Wir wünschen dir noch einen schönen Tag :) Bei weiteren Fragen komme gerne wieder in den Support");
            config.set("supportbot.maxafktime", 600000);
            config.set("supportbot.message.added", "Du bist wieder in Support Bereitschaft!");
            config.set("supportbot.message.removed", "Du wurdest aus Support Bereitschaft entfernt da gerade ein User Support möchte du aber nicht anwesend bist!");
            config.set("supportbot.message.nosups", "Gerade ist leider kein Supporter anwesend, du kannst entweder nachher nochmal kommen, auf den nächsten warten oder uns einfach ein Ticket auf https://grv.sh/ticket schreiben.");
            config.set("supportbot.message.atleastonesup", "Einer unserer %count% Supporter wird sich gleich um dich kümmern. Bitte habe etwas gedult.");
            config.set("supportbot.message.didntunderstand", "Das habe ich nicht verstanden. Bitte achte drauf dass das Wort richtig geschrieben ist.");
            config.set("supportbot.message.nofreemusicbot", "Derzeit sind leider keine Musikbots zur vermietung frei. Versuche es bitte später nochmal.");
            config.set("supportbot.maxwaitingtime", 60000);
            config.set("supportbot.supportchannel", 30);
            config.set("supportbot.maxmsglenght", 15);
            temp = Arrays.asList("msg({Hey, wobei können wir dir Helfen?})%newcommand%msg({Schreibe einen der folgenden Punkte im [b]Privaten Chat[/b]:})%newcommand%msg({'[color=#ff0000][b]TS[/b][/color]' wenn du eine Frage zum Teamspeak hast. [b](zB. Spiele Gruppen, Bewerben)[/b]})%newcommand%msg({'[color=#ff0000][b]MC[/b][/color]' wenn du eine Frage zu Minecraft hast. [b](zB. YouTuber Rang, Builder, [color=#1c12d1]Bewerben[/color])[/b]})\"%newcommand%msg({'[color=#ff0000][b]YT[/b][/color]' wenn du den YouTuber Rang willst.})%newcommand%msg({'[color=#ff0000][b]musikbot[/b][/color]' wenn du einen Musikbot in deinem Channel möchtest.})%newcommand%msg({'[color=#ff0000][b]Anderes[/b][/color]' wenn du eine Frage zu einem ANDEREM Thema als Minecraft oder Teamspeak hast (zB. Forum). [i]NUTZE DIESE AUSWAHL NUR WENN DIE FRAGE ÜBERHAUPT [b]NICHTS[/b] MIT MINECRAFT ODER TEAMSPEAK ZU TUN HAT![/i]})%newcommand%poke({Willkommen im Support! Du wurdest gerade von einem Bot angeschrieben, überprüfe deine Nachrichten :)})".split("%newcommand%"));
            config.set("supportbot.function.join", temp);

            temp = Arrays.asList("kickchannel({Du musst die Fragen des Bots beantworten um Support zu erhalten.})%newcommand%poke({!!! -> Du musst die Fragen des Bots beantworten um Support zu erhalten. <- !!!})".split("%newcommand%"));
            config.set("supportbot.function.afk", temp);

            temp = Arrays.asList("9,10".split(","));
            config.set("musicbotrent.botstorage", 25);
            config.set("musicbotrent.rentgroup", 63);
            config.set("musicbotrent.kickchannel", 20);
            config.set("musicbotrent.channelgroups", temp);

            //OWN Commands:
            temp = Arrays.asList("msg({Wie können wir dir Behilflich sein? Schreibe mir bitte eines der folgenden Wörter:%br%[color=#ff0000][b]yt[/b][/color] um den YouTuber / Premi+ Rang zu beantragen.%br%[color=#ff0000][b]report[/b][/color] um einen Nutzer in Minecraft zu Reporten.%br%[color=#ff0000][b]bug[/b][/color] wenn du einen Bug melden willst.%br%[color=#ff0000][b]unban[/b][/color] wenn du Entbannt werden möchtest.%br%'[color=#ff0000][b]bewerben[/b][/color] um dich zu Bewerben.%br%[color=#ff0000][b]builder[/b][/color] um dich als Builder zu Bewerben.%br%%br%[color=#ff0000][b]anderes[/b][/color] wenn keiner der anderen Punkte zutrifft.})%newcommand%supportcategory({Minecraft})".split("%newcommand%"));
            config.set("supportbot.messages.mc.functions", temp);
            config.set("supportbot.messages.mc.enabled", true);


            temp = Arrays.asList("msg({Wie können wir dir Behilflich sein? Schreibe mir bitte eines der folgenden Wörter:%br%[color=#ff0000][b]gruppen[/b][/color] um Spiele Gruppen zu erhalten.%br%[color=#ff0000][b]report[/b][/color] um Spieler zu melden.%br%[color=#ff0000][b]yt[/b][/color] um den YouTuber / Premi+ Rang zu erhalten willst.%br%[color=#ff0000][b]bewerben[/b][/color] um dich zu Bewerben.%br%%br%[color=#ff0000][b]anderes[/b][/color] wenn keiner der anderen Punkte zutrifft.})%newcommand%supportcategory({Teamspeak})".split("%newcommand%"));
            config.set("supportbot.messages.ts.functions", temp);
            config.set("supportbot.messages.ts.enabled", true);


            temp = Arrays.asList("msg({Um einen Entbannungsantrag zu erstellen gehe bitte auf diese Seite [URL]https://grv.sh/unban[/URL] Wichtig: Wir nehmen KEINE Entbannungsanträge im TeamSpeak an!})%newcommand%endsupport({})".split("%newcommand%"));
            config.set("supportbot.messages.unban.functions", temp);
            config.set("supportbot.messages.unban.enabled", true);


            temp = Arrays.asList("msg({Bitte melde den Bug auf dieser [URL]https://grv.sh/ticket[/URL] Seite.})%newcommand%endsupport({})".split("%newcommand%"));
            config.set("supportbot.messages.bug.functions", temp);
            config.set("supportbot.messages.bug.enabled", true);


            temp = Arrays.asList("msg({%br%Die Vorraussetzungen für den YouTuber / Premi+ Rang findest du hier: [URL]https://grv.sh/yt[/URL]%br%Wenn du diese Erfüllst schreibe '[color=#ff0000][b]yt-ja[/b][/color]' ansonsten melde dich bitte wenn du die Vorraussetzungen erfüllst. Wichtig: Wir machen KEINE Ausnahmen!})");
            config.set("supportbot.messages.yt.functions", temp);
            config.set("supportbot.messages.yt.enabled", true);


            temp = Arrays.asList("msg({Bitte schreibe mir welche Spiele Gruppe du benötigst:%br%[color=#429b57][b]game-mc[/b][/color] (Minecraft)%br%[color=#429b57][b]game-fortnite[/b][/color] (Fortnite)%br%[color=#429b57][b]game-lol[/b][/color] (League of Legends)%br%[color=#429b57][b]game-csgo[/b][/color] (CS:GO)%br%[color=#429b57][b]game-pubg[/b][/color] (PUBG)%br%[color=#429b57][b]game-arma[/b][/color] (Arma)%br%[color=#429b57][b]game-battlefield[/b][/color] (Battlefield)%br%[color=#429b57][b]game-gta[/b][/color] (GTA)%br%[color=#429b57][b]game-fifa[/b][/color] (FIFA)%br%[color=#429b57][b]game-osu[/b][/color] (OSU)%br%[color=#429b57][b]game-fifa[/b][/color] (rocketleague)%br%[color=#429b57][b]game-overwatch[/b][/color] (overwatch)})");
            config.set("supportbot.messages.gruppen.functions", temp);
            config.set("supportbot.messages.gruppen.enabled", true);


            temp = Arrays.asList("msg({Du kannst dich während einer Bewerbungsphase im Forum bewerben: [URL]https://grv.sh/bewerben[/URL] Wichtig: Wir machen KEINE Ausnahmen!})%newcommand%endsupport({})".split("%newcommand%"));
            config.set("supportbot.messages.bewerben.functions", temp);
            config.set("supportbot.messages.bewerben.enabled", true);


            temp = Arrays.asList("msg({Um einen Musikbot zu erhalten gehe bitte nun in deinen Channel, der Musikbot wird dann zu dir gemovt. (Wichtig du benötigst Channel Admin bzw Co Channel Admin)})%newcommand%msg({Um diese Aktion wieder abzubrechen schreibe bitte 'quit'})%newcommand%rentbot({})".split("%newcommand%"));
            config.set("supportbot.messages.musikbot.functions", temp);
            config.set("supportbot.messages.musikbot.enabled", true);


            temp = Arrays.asList("msg({Um Builder zu werden melde dich bitte in Minecraft bei einem Builder um dich als Builder zu bewerben.})%newcommand%tohuman({[color=#358e20][b][Minecraft][/b][/color] Der User %name% möchte sich als Builer Bewerben.})".split("%newcommand%"));
            config.set("supportbot.messages.builder.functions", temp);
            config.set("supportbot.messages.builder.enabled", true);


            temp = Arrays.asList("cartegorymatch({Teamspeak;tohuman[{}];donothing[{}]})%newcommand%cartegorymatch({Minecraft;msg[{Um einen User in Minecraft zu Melden nutze ingame bitte /report}];msg[{Du kannst nur Teamspeak User melden. Wenn du dies wolltest schreibe mir bitte 'ts' und danach nochmals report}]})".split("%newcommand%"));
            config.set("supportbot.messages.report.functions", temp);
            config.set("supportbot.messages.report.enabled", true);


            temp = Arrays.asList("endsupport({})");
            config.set("supportbot.messages.quit.functions", temp);
            config.set("supportbot.messages.quit.enabled", true);


            temp = Arrays.asList("tohuman({[b][%category%][/b] Der User %name% benötigt Support.})");
            config.set("supportbot.messages.anderes.functions", temp);
            config.set("supportbot.messages.anderes.enabled", true);


            temp = Arrays.asList("msg({Okey wir werden uns gleich um dich kümmern. Bitte habe etwas gedult :)})%newcommand%tohuman({[color=#2d302d][b][Allgemein][/b][/color] Der User %name% möchten den YouTuber Rang anfragen.})".split("%newcommand%"));
            config.set("supportbot.messages.yt-ja.functions", temp);
            config.set("supportbot.messages.yt-ja.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{16}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-mc.functions", temp);
            config.set("supportbot.messages.game-mc.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{17}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-lol.functions", temp);
            config.set("supportbot.messages.game-lol.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{16}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-mc.functions", temp);
            config.set("supportbot.messages.game-mc.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{24}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-csgo.functions", temp);
            config.set("supportbot.messages.game-csgo.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{64}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-pubg.functions", temp);
            config.set("supportbot.messages.game-pubg.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{65}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-arma.functions", temp);
            config.set("supportbot.messages.game-arma.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{66}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-battlefield.functions", temp);
            config.set("supportbot.messages.game-battlefield.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{67}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-gta.functions", temp);
            config.set("supportbot.messages.game-gta.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{68}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-fifa.functions", temp);
            config.set("supportbot.messages.game-fifa.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{35}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-fortnite.functions", temp);
            config.set("supportbot.messages.game-fortnite.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{70}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-osu.functions", temp);
            config.set("supportbot.messages.game-osu.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{71}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-overwatch.functions", temp);
            config.set("supportbot.messages.game-overwatch.enabled", true);


            temp = Arrays.asList("hasgroup({9;addgroup[{15}]|,|addgroup[{72}]|,|msg[{Du hast die Gruppe erhalten. Wenn du noch eine weitere Gruppe willst schreib mir einfach 'gruppen' und folge den anweisungen. Ansonsten schreibe mit einfach 'quit'}];msg[{Du kannst dir die Spiele Gruppen leider erst abholen wenn du User bist.}]})");
            config.set("supportbot.messages.game-rocketleague.functions", temp);
            config.set("supportbot.messages.game-rocketleague.enabled", true);

            try {
                config.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config.load();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.write("Config loaded", -1);
        instance = this;
        tsconfig = new TS3Config().setDebugLevel(Level.OFF);
        query = new TS3Query(tsconfig);
        api = query.getApi();
        tsconfig.setDebugLevel(Level.OFF);
        Logger.debug("Connection to: " + config.getString("settings.ip"));
        tsconfig.setHost(config.getString("settings.ip"));
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

        api.login(config.getString("settings.username"), config.getString("settings.password"));
        api.selectVirtualServerById(1);
        api.setNickname(config.getString("settings.nickname"));

        api.registerEvent(TS3EventType.TEXT_PRIVATE);
        api.registerEvent(TS3EventType.SERVER);
        api.registerEvent(TS3EventType.CHANNEL);

        //config.setDebugLevel(Level.ALL);

        Logger.write("Connected!", -1);
    }

    public static void main(String[] args) {
        new TeamSpeakBot();

        new CommandManager();
        new EventManager();
        new SchedulerManager();

        CommandListener.start();
    }

    public static TeamSpeakBot getInstance() {
        return instance;
    }

    public static YamlFile getConfig() {
        return config;
    }
}
