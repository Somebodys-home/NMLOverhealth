package io.github.NoOne.nMLOverhealthSystem;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.ResetStatsEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class OverhealthListener implements Listener {
    private NMLOverhealthSystem nmlOverhealthSystem;
    private final ProfileManager profileManager;
    private final OverhealthManager overhealthManager;

    public OverhealthListener(NMLOverhealthSystem nmlOverhealthSystem) {
        this.nmlOverhealthSystem = nmlOverhealthSystem;
        this.profileManager = nmlOverhealthSystem.getProfileManager();
        this.overhealthManager = nmlOverhealthSystem.getOverhealthManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        overhealthManager.add2OverhealthRegenMap(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            double prevOverhealth = profileManager.getPlayerProfile(player.getUniqueId()).getStats().getOverhealth();

            new BukkitRunnable() {
                @Override
                public void run() {
                    double newOverhealth = player.getAbsorptionAmount();

                    Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, "overhealth", newOverhealth - prevOverhealth));
                    overhealthManager.add2OverhealthRegenMap(player);
                }
            }.runTaskLater(nmlOverhealthSystem, 1L);
        }
    }

    @EventHandler
    public void updateOverhealthVisually(StatChangeEvent event) {
        if (event.getStat().equals("maxoverhealth") || event.getStat().equals("overhealth")) {
            Player player = event.getPlayer();
            Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
            double overhealth = (int) stats.getOverhealth();

            // Update the max absorption cap so it can display above 4 hearts
            player.getAttribute(Attribute.MAX_ABSORPTION).setBaseValue(stats.getMaxOverhealth());

            // Update the current visible overhealth
            if (overhealth >= 0 && Double.isFinite(overhealth)) {
                player.setAbsorptionAmount(stats.getOverhealth());
            }
        }
    }

    @EventHandler
    public void resetOverhealth(ResetStatsEvent event) {
        Player player = event.getPlayer();
        player.setAbsorptionAmount(0);
    }
}