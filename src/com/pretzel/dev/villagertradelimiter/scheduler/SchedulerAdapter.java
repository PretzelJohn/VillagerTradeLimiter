package com.pretzel.dev.villagertradelimiter.scheduler;

import java.util.function.Consumer;
import org.bukkit.entity.Entity;

public interface SchedulerAdapter {

  void runAsync(Runnable submitDataTask);

  <T extends Entity> void runEntity(T entity, Consumer<T> consumer);
}
