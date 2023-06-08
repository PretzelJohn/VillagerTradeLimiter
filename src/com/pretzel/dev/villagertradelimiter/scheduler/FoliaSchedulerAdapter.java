package com.pretzel.dev.villagertradelimiter.scheduler;


import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class FoliaSchedulerAdapter implements SchedulerAdapter {

  private final static boolean SUPPORTED = checkSupport();
  private final Plugin plugin;
  private final AsyncScheduler asyncScheduler;

  public FoliaSchedulerAdapter(final Plugin plugin) {
    this.plugin = plugin;
    this.asyncScheduler = plugin.getServer().getAsyncScheduler();
  }

  public static boolean isSupported() {
    return SUPPORTED;
  }

  private static boolean checkSupport() {
    try {
      Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  @Override
  public void runAsync(Runnable submitDataTask) {
    asyncScheduler.runNow(plugin, task -> submitDataTask.run());
  }

  @Override
  public <T extends Entity> void runEntity(T entity, Consumer<T> consumer) {
    entity.getScheduler().run(plugin, task -> consumer.accept(entity), () -> {
    });
  }
}
