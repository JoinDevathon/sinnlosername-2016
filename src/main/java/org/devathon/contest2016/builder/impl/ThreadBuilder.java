package org.devathon.contest2016.builder.impl;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.devathon.contest2016.DevathonPlugin;
import org.devathon.contest2016.builder.Builder;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.builder.impl
 */
public class ThreadBuilder implements Builder<BukkitTask> {

    private BukkitTask task;
    private Runnable runnable;

    public ThreadBuilder start(boolean async, long delay) {
        task = async ? Bukkit.getScheduler().runTaskTimerAsynchronously(DevathonPlugin.helper().plugin(), runnable, 0, delay)
                : Bukkit.getScheduler().runTaskTimer(DevathonPlugin.helper().plugin(), runnable, 0, delay);
        return this;
    }

    public ThreadBuilder with(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    @Override
    public BukkitTask build() {
        return task;
    }

}
