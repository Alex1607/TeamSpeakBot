package com.pluoi.tsbot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static boolean debugMode = false;
    private static String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

    public static void write(String message, int id) {
        if (id == -1) {
            newTime();
            System.out.println("[" + timeStamp + " INFO] " + message);
        } else {
            TeamSpeakBot.api.sendPrivateMessage(id, message);
        }
    }

    public static void chat(String message) {
        newTime();
        System.out.println("[" + timeStamp + " CHAT] " + message);
    }

    public static void error(String message, int id) {
        if (id == -1) {
            newTime();
            System.out.println("[" + timeStamp + " Error] " + message);
        } else {
            TeamSpeakBot.api.sendPrivateMessage(id, message);
        }
    }

    public static void debug(String message) {
        if (debugMode) {
            newTime();
            System.out.println("[" + timeStamp + " DEBUG] " + message);
        }
    }

    private static void newTime() {
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}
