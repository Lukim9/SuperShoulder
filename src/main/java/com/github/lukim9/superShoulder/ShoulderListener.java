package com.github.lukim9.superShoulder;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

public class ShoulderListener implements Listener {
    private final JavaPlugin plugin;

    public ShoulderListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPulse(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();

        Vector side = new Vector(-direction.getZ(), 0, direction.getX()).normalize();

        player.getNearbyEntities(2, 1, 2).forEach(entity -> {
            Vector relativePos = entity.getLocation().toVector()
                    .subtract(eyeLoc.toVector());

            double forwardDist = relativePos.dot(direction);
            double sideDist = Math.abs(relativePos.dot(side));

            if (forwardDist <= 2 && forwardDist >= -2 && sideDist <= 1.5) {
                double damage = 5;
                Vector knockback = player.getLocation().getDirection()
                        .multiply(10).setY(0.3);
                entity.setVelocity(knockback);

                if (entity instanceof Damageable) {
                    ((Damageable) entity).damage(damage, player);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerProximity(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().setY(0).normalize();
        Vector side = new Vector(-direction.getZ(), 0, direction.getX()).normalize();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int x = 0; x <= 1; x++) {
                    for (int y = -1; y <= 0; y++) {
                        for (int z = -3; z <= 3; z++) {
                            Block block = eyeLoc.clone()
                                    .add(direction.clone().multiply(x))
                                    .add(0, y, 0)
                                    .add(side.clone().multiply(z * 0.5)).getBlock();
                            if(block.getChunk().isLoaded() && block.isSolid()) {
                                BlockData originalData = block.getBlockData();
                                block.breakNaturally();

                                SoundGroup group = originalData.getSoundGroup();
                                Sound breakSound = group.getBreakSound();

                                block.getWorld().playSound(
                                        block.getLocation().add(0.5, 0.5, 0.5),
                                        breakSound,
                                        group.getVolume(),
                                        group.getPitch()
                                );

                                block.getWorld().spawnParticle(
                                        Particle.BLOCK,
                                        block.getLocation().add(0.5, 0.5, 0.5),
                                        20, // 파티클 개수
                                        0.3, 0.3, 0.3, // 오프셋 범위
                                        0.5, // 속도
                                        originalData // 원본 블록 데이터
                                );
                            }
                        }
                    }
                }
            }
        }.runTask(plugin);
    }
}
