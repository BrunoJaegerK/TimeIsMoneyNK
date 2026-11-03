package com.brunojaegerk.manager;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import com.brunojaegerk.Main;

public class PlayerActivityListener implements Listener {

    private final Main plugin;

    public PlayerActivityListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        plugin.getAfkManager().registerPlayer(player);
        plugin.getPaymentSchedulerManager().registerPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        plugin.getAfkManager().unregisterPlayer(player);
        plugin.getPaymentSchedulerManager().unregisterPlayer(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        if (event.getFrom() == null || event.getTo() == null) {
            return;
        }

        double dx = event.getTo().getX() - event.getFrom().getX();
        double dy = event.getTo().getY() - event.getFrom().getY();
        double dz = event.getTo().getZ() - event.getFrom().getZ();

        double distanceSquared =
                dx * dx + dy * dy + dz * dz;

        if (distanceSquared > 0.0001) {
            plugin.getAfkManager().recordActivity(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        plugin.getAfkManager().recordActivity(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        plugin.getAfkManager().recordActivity(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        plugin.getAfkManager().recordActivity(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player player) {
            plugin.getAfkManager().recordActivity(player);
        }
    }
}

