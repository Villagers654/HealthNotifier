package club.aurorapvp.events;

import club.aurorapvp.HealthNotifier;
import club.aurorapvp.events.listeners.HealthListener;

public class Events {
  public static void init() {
    HealthNotifier.getInstance()
        .getServer()
        .getPluginManager()
        .registerEvents(new HealthListener(HealthNotifier.getInstance()), HealthNotifier.getInstance());
  }
}
