package com.pretzel.dev.villagertradelimiter.scheduler;


import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

@SuppressWarnings("deprecation")
public class BukkitSchedulerAdapter implements SchedulerAdapter {

  private final Plugin plugin;
  private final BukkitScheduler scheduler;

  public BukkitSchedulerAdapter(final Plugin plugin) {
    this.plugin = plugin;
    this.scheduler = plugin.getServer().getScheduler();
  }

  @Override
  public void runAsync(final Runnable submitDataTask) {
    scheduler.runTaskAsynchronously(plugin, submitDataTask);
  }

  @Override
  public <T extends Entity> void runEntity(final T entity, final Consumer<T> consumer) {
    scheduler.runTask(plugin, () -> consumer.accept(entity));
  }
}
