package com.pluoi.tsbot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private boolean debugMode;
    private Logger logger;

    public Logger() {
        logger = this;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public Logger getLogger() {
        return logger;
    }

    public void write(String message, int id) {
        if (id == -1) {
            System.out.println("[" + getTime() + " INFO] " + message);
        } else {
            TeamSpeakBot.api.sendPrivateMessage(id, message);
        }
    }

    public void chat(String message) {
        System.out.println("[" + getTime() + " CHAT] " + message);
    }

    public void error(String message, int id) {
        if (id == -1) {
            System.out.println("[" + getTime() + " Error] " + message);
        } else {
            TeamSpeakBot.api.sendPrivateMessage(id, message);
        }
    }

    public void debug(String message) {
        if (isDebugMode()) {
            System.out.println("[" + getTime() + " DEBUG] " + message);
        }
    }

    private String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}
