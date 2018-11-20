package com.pluoi.tsbot.scheduler.schedulers;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.scheduler.Scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RandomGif extends Scheduler {
    private final ScheduledExecutorService scheduler;
    ScheduledFuture<?> future;
    private TS3Api api = TeamSpeakBot.api;
    private int period = TeamSpeakBot.getConfig().getInt("function.scheduler.randomcatgif.timer");
    private int channel = TeamSpeakBot.getConfig().getInt("function.scheduler.randomcatgif.channel");

    public RandomGif() {
        super("RandomGif", "Sets a random Cat gif in the channel", 0, 120);
        this.setPeriod(period);
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public static String readFromWeb(String webURL) throws IOException {
        URL url = new URL(webURL);
        InputStream is = url.openStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String temp = br.readLine();
            Logger.debug(temp);
            return temp;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new MalformedURLException("URL is malformed!!");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    private void run() {
        Logger.debug("RandomGif runned");
        HashMap<ChannelProperty, String> properties = new HashMap<>();
        try {
            properties.put(ChannelProperty.CHANNEL_DESCRIPTION, "[img]" + readFromWeb("http://api.pluoi.com/gif/index.php?q=cat") + "[/img]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        api.editChannel(channel, properties);
    }

    @Override
    public void start(int delay, int period) {
        future = scheduler.scheduleAtFixedRate(this::run, delay, period, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        future.cancel(true);
    }
}
