package com.brunojaegerk.manager;

import cn.nukkit.Player;
import cn.nukkit.scheduler.TaskHandler;
import com.brunojaegerk.Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PaymentSchedulerManager {

    private static final int TASK_PERIOD_TICKS = 20;

    private final Main plugin;

    private final Map<UUID, Long> accumulatedTime = new HashMap<>();
    private final Map<UUID, Long> lastCheckTime = new HashMap<>();

    private TaskHandler taskHandler;

    public PaymentSchedulerManager(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Inicia el único temporizador global.
     */
    public void start() {

        if (taskHandler != null) {
            return;
        }

        taskHandler = plugin.getServer()
                .getScheduler()
                .scheduleRepeatingTask(
                        plugin,
                        this::tick,
                        TASK_PERIOD_TICKS
                );

        plugin.getLogger().info(
                "Temporizador de pagos iniciado."
        );
    }

    /**
     * Detiene el temporizador.
     */
    public void stop() {

        if (taskHandler != null) {
            taskHandler.cancel();
            taskHandler = null;
        }

        accumulatedTime.clear();
        lastCheckTime.clear();
    }

    /**
     * Inicializa el contador de un jugador.
     */
    public void registerPlayer(Player player) {

        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        accumulatedTime.putIfAbsent(uuid, 0L);
        lastCheckTime.put(uuid, now);
    }

    /**
     * Elimina los datos de un jugador.
     */
    public void unregisterPlayer(Player player) {

        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();

        accumulatedTime.remove(uuid);
        lastCheckTime.remove(uuid);
    }

    /**
     * Se ejecuta una vez por segundo.
     */
    private void tick() {

        long now = System.currentTimeMillis();

        Set<UUID> onlinePlayers = new HashSet<>();

        for (Player player : plugin.getServer().getOnlinePlayersList()) {

            if (player == null || !player.isOnline()) {
                continue;
            }

            UUID uuid = player.getUniqueId();
            onlinePlayers.add(uuid);

            processPlayer(player, now);
        }

        // Limpia registros de jugadores que ya no están conectados.
        accumulatedTime.keySet().removeIf(
                uuid -> !onlinePlayers.contains(uuid)
        );

        lastCheckTime.keySet().removeIf(
                uuid -> !onlinePlayers.contains(uuid)
        );
    }

    /**
     * Procesa el tiempo y los pagos de un jugador.
     */
    private void processPlayer(Player player, long now) {

        UUID uuid = player.getUniqueId();

        Long previousTime = lastCheckTime.put(uuid, now);

        if (previousTime == null) {
            accumulatedTime.putIfAbsent(uuid, 0L);
            return;
        }

        long elapsed = Math.max(0L, now - previousTime);

        // El tiempo AFK no se acumula si está deshabilitado
        // el pago para jugadores AFK.
        if (plugin.getAfkManager().canReceivePayment(player)) {

            accumulatedTime.merge(
                    uuid,
                    elapsed,
                    Long::sum
            );
        }

        PaymentManager.RankPayment payment =
                plugin.getPaymentManager().getPaymentFor(player);

        if (payment == null) {
            return;
        }

        long intervalMillis =
                payment.intervalSeconds() * 1000L;

        if (intervalMillis <= 0L) {
            return;
        }

        long accumulated =
                accumulatedTime.getOrDefault(uuid, 0L);

        if (accumulated < intervalMillis) {
            return;
        }

        // Solo se intenta entregar una recompensa por revisión.
        // Si falla, el tiempo no se descuenta y se reintentará.
        if (plugin.getRewardManager().giveReward(player, payment)) {

            accumulatedTime.put(
                    uuid,
                    accumulated - intervalMillis
            );
        }
    }

    /**
     * Consulta el tiempo acumulado de un jugador.
     */
    public long getAccumulatedSeconds(Player player) {

        if (player == null) {
            return 0L;
        }

        return accumulatedTime.getOrDefault(
                player.getUniqueId(),
                0L
        ) / 1000L;
    }
}

