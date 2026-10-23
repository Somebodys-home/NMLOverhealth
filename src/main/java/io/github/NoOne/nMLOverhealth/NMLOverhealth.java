package io.github.NoOne.nMLOverhealth;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLOverhealth extends JavaPlugin {
    private ProfileManager profileManager;
    private OverhealthManager overhealthManager;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();

        overhealthManager = new OverhealthManager(this);
        overhealthManager.overhealthRegenServerTask();

        getServer().getPluginManager().registerEvents(new OverhealthListener(this), this);
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public OverhealthManager getOverhealthManager() {
        return overhealthManager;
    }
}