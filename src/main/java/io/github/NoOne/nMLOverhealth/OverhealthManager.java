package io.github.NoOne.nMLOverhealth;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OverhealthManager {
    private NMLOverhealth nmlOverhealth;
    private ProfileManager profileManager;
    private Map<UUID, Long> overhealthRegenMap = new HashMap<>();
    private Map<UUID, Integer> activeRegenTasks = new HashMap<>();

    public OverhealthManager(NMLOverhealth nmlOverhealth) {
        this.nmlOverhealth = nmlOverhealth;
        profileManager = nmlOverhealth.getProfileManager();
    }

    public void add2OverhealthRegenMap(Player player) {
        overhealthRegenMap.put(player.getUniqueId(), System.currentTimeMillis());
        cancelRegenTask(player.getUniqueId());
    }

    public void overhealthRegenServerTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    long last = overhealthRegenMap.getOrDefault(uuid, 0L);

                    if (currentTime - last >= 3000) {
                        if (!activeRegenTasks.containsKey(uuid)) {
                            overhealthRegen(player);
                        }
                        overhealthRegenMap.put(uuid, currentTime);
                    }
                }
            }
        }.runTaskTimer(nmlOverhealth, 0L, 20L);
    }

    public void overhealthRegen(Player player) {
        UUID uuid = player.getUniqueId();

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                Stats stats = profileManager.getPlayerProfile(uuid).getStats();
                double maxOverhealth = stats.getMaxOverhealth();
                double overhealth = stats.getOverhealth();
                double newOverhealth;

                if (overhealth >= maxOverhealth) {
                    cancelRegenTask(uuid);
                    return;
                }
                                                        // seconds to regen to max overhealth
                newOverhealth = Math.min(overhealth + (maxOverhealth / 15), maxOverhealth);
                Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, "overhealth", newOverhealth - overhealth));

                if (newOverhealth >= maxOverhealth) {
                    cancelRegenTask(uuid);
                }
            }
        }.runTaskTimer(nmlOverhealth, 0L, 20L).getTaskId();

        activeRegenTasks.put(uuid, taskId);
    }

    private void cancelRegenTask(UUID uuid) {
        Integer taskId = activeRegenTasks.get(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
            activeRegenTasks.remove(uuid);
        }
    }
}