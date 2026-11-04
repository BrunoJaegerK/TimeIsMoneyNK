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

    public void stop() {

        if (taskHandler != null) {
            taskHandler.cancel();
            taskHandler = null;
        }

        accumulatedTime.clear();
        lastCheckTime.clear();
    }

    public void registerPlayer(Player player) {

        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        accumulatedTime.putIfAbsent(uuid, 0L);
        lastCheckTime.put(uuid, now);
    }

    public void unregisterPlayer(Player player) {

        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();

        accumulatedTime.remove(uuid);
        lastCheckTime.remove(uuid);
    }

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

        accumulatedTime.keySet().removeIf(
                uuid -> !onlinePlayers.contains(uuid)
        );

        lastCheckTime.keySet().removeIf(
                uuid -> !onlinePlayers.contains(uuid)
        );
    }

    private void processPlayer(Player player, long now) {

        UUID uuid = player.getUniqueId();

        Long previousTime = lastCheckTime.put(uuid, now);

        if (previousTime == null) {
            accumulatedTime.putIfAbsent(uuid, 0L);
            return;
        }

        long elapsed = Math.max(0L, now - previousTime);

        // Los mundos excluidos no generan tiempo ni recompensas.
        // lastCheckTime ya se actualizó para evitar contar
        // el tiempo pasado en el mundo excluido al salir.
        if (plugin.getConfigManager().isWorldExcluded(player)) {
            return;
        }

        // Si el jugador no puede recibir pagos por estar AFK,
        // tampoco se acumula tiempo ni se procesa un pago pendiente.
        if (!plugin.getAfkManager().canReceivePayment(player)) {
            return;
        }

        accumulatedTime.merge(
                uuid,
                elapsed,
                Long::sum
        );

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

        // Solo se descuenta el tiempo si EconomyAPI confirma el pago.
        if (plugin.getRewardManager().giveReward(player, payment)) {

            accumulatedTime.put(
                    uuid,
                    accumulated - intervalMillis
            );
        }
    }

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


