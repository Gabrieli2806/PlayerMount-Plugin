package com.g2806.PlayerMount;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class PlayerMountPlugin extends JavaPlugin implements Listener {
    private final HashMap<UUID, Integer> sneakCount = new HashMap<>();
    private final HashMap<UUID, BossBar> riderBossBars = new HashMap<>();
    private final HashMap<UUID, Long> messageCooldown = new HashMap<>();
    private FileConfiguration config;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        config = getConfig();
    }

    @Override
    public void onDisable() {
        saveConfig();
        riderBossBars.values().forEach(BossBar::removeAll);
        riderBossBars.clear();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player) || args.length != 1) {
            return false;
        }
        boolean value = args[0].equalsIgnoreCase("true");

        if (command.getName().equalsIgnoreCase("playermount")) {
            config.set(player.getUniqueId() + ".allowMount", value);
            saveConfig();
            player.sendMessage("Mounting preference set to: " + value);
        } else if (command.getName().equalsIgnoreCase("playermountbossbar")) {
            config.set(player.getUniqueId() + ".bossbar", value);
            saveConfig();
            player.sendMessage("Boss bar visibility set to: " + value);
            if (!value) {
                BossBar bossBar = riderBossBars.get(player.getUniqueId());
                if (bossBar != null) {
                    bossBar.removePlayer(player);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player rider = event.getPlayer();
        UUID riderId = rider.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (messageCooldown.containsKey(riderId) && currentTime - messageCooldown.get(riderId) < 1000) {
            return;
        }

        if (event.getRightClicked() instanceof Player mount) {
            if (!config.getBoolean(mount.getUniqueId() + ".allowMount", true)) {
                rider.sendMessage(mount.getName() + " has disabled mounting.");
                messageCooldown.put(riderId, currentTime);
                return;
            }
            if (mount.addPassenger(rider)) {
                rider.sendMessage("Now riding " + mount.getName());
                mount.sendMessage(rider.getName() + " is now riding you");
                messageCooldown.put(riderId, currentTime);
                if (config.getBoolean(rider.getUniqueId() + ".bossbar", false)) {
                    BossBar bossBar = Bukkit.createBossBar(mount.getName() + "'s Health", BarColor.RED, BarStyle.SOLID);
                    bossBar.addPlayer(rider);
                    bossBar.setProgress(mount.getHealth() / mount.getAttribute(Attribute.MAX_HEALTH).getValue());
                    riderBossBars.put(rider.getUniqueId(), bossBar);
                    getServer().getScheduler().runTaskTimer(this, () -> {
                        if (rider.getVehicle() == mount) {
                            bossBar.setProgress(mount.getHealth() / mount.getAttribute(Attribute.MAX_HEALTH).getValue());
                        } else {
                            bossBar.removeAll();
                            riderBossBars.remove(rider.getUniqueId());
                        }
                    }, 0L, 10L);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!player.getPassengers().isEmpty()) {
            player.eject();
        }
        sneakCount.remove(player.getUniqueId());
        messageCooldown.remove(player.getUniqueId());
        BossBar bossBar = riderBossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player mount = event.getPlayer();
        if (!event.isSneaking() || mount.getPassengers().isEmpty() || mount.getLocation().getPitch() > -60) return;

        UUID mountId = mount.getUniqueId();
        int count = sneakCount.getOrDefault(mountId, 0) + 1;

        if (count >= 3) {
            mount.eject();
            mount.sendMessage("Dismounted rider!");
            for (Entity rider : mount.getPassengers()) {
                if (rider instanceof Player) {
                    rider.sendMessage("You were dismounted by " + mount.getName());
                    BossBar bossBar = riderBossBars.remove(rider.getUniqueId());
                    if (bossBar != null) {
                        bossBar.removeAll();
                    }
                }
            }
            sneakCount.remove(mountId);
        } else {
            sneakCount.put(mountId, count);
            mount.sendActionBar(Component.text("Look up and sneak " + (3 - count) + " more times to dismount rider."));
            getServer().getScheduler().runTaskLater(this, () -> sneakCount.remove(mountId), 60L);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && event.getEntity() instanceof Player target) {
            if (!damager.getPassengers().isEmpty() && damager.getPassengers().contains(target)) {
                event.setCancelled(true);
            }
        }
    }
}