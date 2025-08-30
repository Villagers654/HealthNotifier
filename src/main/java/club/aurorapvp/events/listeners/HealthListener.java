package club.aurorapvp.events.listeners;

import cn.nukkit.Server;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.Task;
import cn.nukkit.Player;
import cn.nukkit.potion.Effect;

public class HealthListener implements Listener {

  private final Plugin plugin;

  public HealthListener(Plugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    Entity entity = event.getEntity();
    if (entity instanceof Player && !event.isCancelled()) {
      Server.getInstance()
          .getScheduler()
          .scheduleDelayedTask(plugin, new UpdateTask((Player) entity), 1);
    }
  }

  @EventHandler
  public void onRegain(EntityRegainHealthEvent event) {
    Entity entity = event.getEntity();
    if (entity instanceof Player && !event.isCancelled()) {
      Server.getInstance()
          .getScheduler()
          .scheduleDelayedTask(plugin, new UpdateTask((Player) entity), 1);
    }
  }

  @EventHandler
  public void onPotionEffect(EntityPotionEffectEvent event) {
    Entity entity = event.getEntity();
    if (entity instanceof Player) {
      Effect effect;
      if (event.getAction() == EntityPotionEffectEvent.Action.REMOVED) {
        effect = event.getOldEffect();
      } else {
        effect = event.getNewEffect();
      }
      if (effect != null && effect.getId() == Effect.ABSORPTION && !event.isCancelled()) {
        Server.getInstance()
            .getScheduler()
            .scheduleDelayedTask(plugin, new UpdateTask((Player) entity), 1);
      }
    }
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();
    Server.getInstance().getScheduler().scheduleDelayedTask(plugin, new UpdateTask(player), 1);
  }

  private static class UpdateTask extends Task {
    private final Player player;

    public UpdateTask(Player player) {
      this.player = player;
    }

    @Override
    public void onRun(int currentTick) {
      if (!player.isOnline()) return;

      UpdateAttributesPacket pk = new UpdateAttributesPacket();
      pk.entityId = player.getId();

      Attribute healthAttr = Attribute.getAttribute(Attribute.MAX_HEALTH);
      healthAttr.setMaxValue(player.getMaxHealth());
      healthAttr.setValue(player.getHealth());

      Attribute absorptionAttr = Attribute.getAttribute(Attribute.ABSORPTION);
      absorptionAttr.setMaxValue(1024f);
      absorptionAttr.setValue(player.getAbsorption());

      pk.entries = new Attribute[] {healthAttr, absorptionAttr};

      for (Player p : Server.getInstance().getOnlinePlayers().values()) {
        p.dataPacket(pk);
      }
    }
  }
}
