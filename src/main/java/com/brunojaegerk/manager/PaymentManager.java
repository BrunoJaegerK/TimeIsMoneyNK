package com.brunojaegerk.manager;

import cn.nukkit.Player;
import com.brunojaegerk.Main;

import java.util.Map;

public class PaymentManager {

    private final Main plugin;

    public PaymentManager(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Obtiene el rango de pago más alto que posee el jugador.
     */
    public RankPayment getPaymentFor(Player player) {

        Map<String, ConfigManager.PaymentSettings> payments =
                plugin.getConfigManager().getPayments();

        ConfigManager.PaymentSettings selectedPayment = null;
        String selectedRank = null;

        for (Map.Entry<String, ConfigManager.PaymentSettings> entry
                : payments.entrySet()) {

            String rank = entry.getKey();

            ConfigManager.PaymentSettings settings = entry.getValue();

            if (rank.equalsIgnoreCase("default")) {
                continue;
            }

            String permission = settings.permission();

            if (permission == null || permission.isBlank()) {
                continue;
            }

            if (!player.hasPermission(permission)) {
                continue;
            }

            if (selectedPayment == null
                    || settings.priority() > selectedPayment.priority()) {

                selectedPayment = settings;
                selectedRank = rank;
            }
        }

        // Si no tiene ningún rango especial, utiliza default.
        if (selectedPayment == null) {

            ConfigManager.PaymentSettings defaultPayment =
                    payments.get("default");

            if (defaultPayment == null) {
                return null;
            }

            selectedPayment = defaultPayment;
            selectedRank = "default";
        }

        return new RankPayment(
                selectedRank,
                selectedPayment.amount(),
                selectedPayment.intervalSeconds(),
                selectedPayment.priority()
        );
    }

    /**
     * Comprueba si el jugador tiene una configuración de pago válida.
     */
    public boolean canReceivePayment(Player player) {
        RankPayment payment = getPaymentFor(player);

        return payment != null
                && payment.amount() >= 0
                && payment.intervalSeconds() > 0;
    }

    /**
     * Convierte el intervalo de segundos a ticks de Nukkit.
     */
    public long getIntervalTicks(Player player) {

        RankPayment payment = getPaymentFor(player);

        if (payment == null) {
            return 0L;
        }

        return payment.intervalSeconds() * 20L;
    }

    /**
     * Información del rango seleccionado.
     */
    public record RankPayment(
            String rank,
            double amount,
            int intervalSeconds,
            int priority
    ) {
    }
}

