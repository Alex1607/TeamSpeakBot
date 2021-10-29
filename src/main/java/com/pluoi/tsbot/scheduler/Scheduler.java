package com.pluoi.tsbot.scheduler;

import com.pluoi.tsbot.Logger;

public abstract class Scheduler {
    protected static final Logger logger = new Logger();
    private final int delay;
    private final String name;
    private final String description;
    private int period;

    /**
     * Create a new Scheduler to run a task but this will not start it immediately.
     *
     * @param name
     * @param description
     * @param delay       How many seconds should the scheduler wait befor executing the first time.
     * @param period      How many seconds should the scheduler wait befor executing again.
     */
    protected Scheduler(String name, String description, Integer delay, Integer period) {
        this.name = name;
        this.description = description;
        this.delay = delay;
        this.period = period;
    }

    /**
     * Starts the Schduler after the given delay and run it again after the delay.
     *
     * @param delay  How long should be waited before executing?
     * @param period How long do you want to wait between the repetitions?
     */
    public abstract void start(int delay, int period);

    /**
     * Stops the scheduler.
     */
    public abstract void stop();

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getDelay() {
        return delay;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
