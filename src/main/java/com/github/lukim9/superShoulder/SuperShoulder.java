package com.github.lukim9.superShoulder;

import org.bukkit.plugin.java.JavaPlugin;

public final class SuperShoulder extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(
                new ShoulderListener(this), // this(플러그인) 주입
                this
        );
    }
}
