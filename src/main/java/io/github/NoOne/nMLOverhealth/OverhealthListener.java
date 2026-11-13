package io.github.NoOne.nMLOverhealth;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.PlayerStat;
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

public class OverhealthListener implements Listener {
    private NMLOverhealth nmlOverhealth;
    private ProfileManager profileManager;
    private OverhealthManager overhealthManager;

    public OverhealthListener(NMLOverhealth nmlOverhealth) {
        this.nmlOverhealth = nmlOverhealth;
        this.profileManager = nmlOverhealth.getProfileManager();
        this.overhealthManager = nmlOverhealth.getOverhealthManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        overhealthManager.add2OverhealthRegenMap(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            double prevOverhealth = profileManager.getStats(player.getUniqueId()).getStatValue(PlayerStat.OVERHEALTH);

            new BukkitRunnable() {
                @Override
                public void run() {
                    double newOverhealth = player.getAbsorptionAmount();

                    Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, PlayerStat.OVERHEALTH, newOverhealth - prevOverhealth));
                    overhealthManager.add2OverhealthRegenMap(player);
                }
            }.runTaskLater(nmlOverhealth, 1L);
        }
    }

    @EventHandler
    public void updateOverhealthVisually(StatChangeEvent event) {
        if (event.getStat() == PlayerStat.MAX_OVERHEALTH || event.getStat() == PlayerStat.OVERHEALTH) {
            Player player = event.getPlayer();
            Stats stats = profileManager.getStats(player.getUniqueId());
            double overhealth = (int) stats.getStatValue(PlayerStat.OVERHEALTH);

            // Update the max absorption cap so it can display above 4 hearts
            player.getAttribute(Attribute.MAX_ABSORPTION).setBaseValue(stats.getStatValue(PlayerStat.MAX_OVERHEALTH));

            // Update the current visible overhealth
            if (overhealth >= 0 && Double.isFinite(overhealth)) {
                player.setAbsorptionAmount(overhealth);
            }
        }
    }

    @EventHandler
    public void resetOverhealth(ResetStatsEvent event) {
        Player player = event.getPlayer();
        player.setAbsorptionAmount(0);
    }
}