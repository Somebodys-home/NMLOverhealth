package io.github.NoOne.nMLOverhealth;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.StatConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLOverhealth extends JavaPlugin {
    private StatConfigManager statConfigManager;
    private OverhealthManager overhealthManager;

    @Override
    public void onEnable() {
        statConfigManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getStatConfigManager();

        overhealthManager = new OverhealthManager(this);
        overhealthManager.overhealthRegenServerTask();

        getServer().getPluginManager().registerEvents(new OverhealthListener(this), this);
    }

    public StatConfigManager getStatConfigManager() {
        return statConfigManager;
    }

    public OverhealthManager getOverhealthManager() {
        return overhealthManager;
    }
}