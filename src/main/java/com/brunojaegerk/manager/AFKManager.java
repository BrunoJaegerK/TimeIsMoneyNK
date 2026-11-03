package com.brunojaegerk.manager;

import cn.nukkit.Player;
import com.brunojaegerk.Main;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AFKManager {

    private final Main plugin;

    private final Map<UUID, Long> lastActivity =
            new ConcurrentHashMap<>();

    public AFKManager(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Registra al jugador cuando entra al servidor.
     */
    public void registerPlayer(Player player) {
        if (player == null) {
            return;
        }

        lastActivity.put(
                player.getUniqueId(),
                System.currentTimeMillis()
        );
    }

    /**
     * Elimina los datos cuando el jugador sale.
     */
    public void unregisterPlayer(Player player) {
        if (player == null) {
            return;
        }

        lastActivity.remove(player.getUniqueId());
    }

    /**
     * Actualiza la última actividad del jugador.
     */
    public void recordActivity(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        lastActivity.put(
                player.getUniqueId(),
                System.currentTimeMillis()
        );
    }

    /**
     * Comprueba si el jugador está AFK.
     */
    public boolean isAfk(Player player) {
        if (player == null || !player.isOnline()) {
            return true;
        }

        Long lastTime = lastActivity.get(player.getUniqueId());

        if (lastTime == null) {
            return true;
        }

        long timeoutMillis =
                plugin.getConfigManager().getActivityTimeout() * 1000L;

        long inactiveMillis =
                System.currentTimeMillis() - lastTime;

        return inactiveMillis >= timeoutMillis;
    }

    /**
     * Comprueba si el jugador puede recibir pagos
     * teniendo en cuenta la configuración AFK.
     */
    public boolean canReceivePayment(Player player) {

        if (player == null || !player.isOnline()) {
            return false;
        }

        // Si los pagos AFK están habilitados,
        // no se bloquean las recompensas.
        if (plugin.getConfigManager().isAfkPayoutEnabled()) {
            return true;
        }

        return !isAfk(player);
    }

    /**
     * Devuelve los segundos transcurridos desde
     * la última actividad registrada.
     */
    public long getInactiveSeconds(Player player) {

        if (player == null) {
            return 0L;
        }

        Long lastTime = lastActivity.get(player.getUniqueId());

        if (lastTime == null) {
            return 0L;
        }

        return Math.max(
                0L,
                (System.currentTimeMillis() - lastTime) / 1000L
        );
    }

    /**
     * Limpia todos los registros.
     */
    public void clear() {
        lastActivity.clear();
    }
}

