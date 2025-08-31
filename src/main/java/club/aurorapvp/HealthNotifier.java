package club.aurorapvp;

import club.aurorapvp.events.Events;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;

public class HealthNotifier extends PluginBase {

  private static Plugin INSTANCE;

  public void onEnable() {
    INSTANCE = this;

    // Setup classes
    Events.init();
  }

  public static Plugin getInstance() {
    return INSTANCE;
  }
}
