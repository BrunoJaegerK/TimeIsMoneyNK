package com.brunojaegerk.manager;

import cn.nukkit.Player;
import com.brunojaegerk.Main;
import me.onebone.economyapi.EconomyAPI;

import java.util.Locale;

public class RewardManager {

    private final Main plugin;

    public RewardManager(Main plugin) {
        this.plugin = plugin;
    }

    public boolean giveReward(
            Player player,
            PaymentManager.RankPayment payment
    ) {
        if (player == null || !player.isOnline() || payment == null) {
            return false;
        }

        double amount = payment.amount();

        if (!Double.isFinite(amount) || amount <= 0) {
            plugin.getLogger().warning(
                    "Cantidad de recompensa inválida para "
                            + player.getName()
            );
            return false;
        }

        try {
            EconomyAPI economy = EconomyAPI.getInstance();

            if (economy == null) {
                plugin.getLogger().error(
                        "No se pudo obtener la instancia de EconomyAPI."
                );
                return false;
            }

            // Comprueba que el jugador tenga una cuenta económica.
            if (!economy.hasAccount(player)) {
                plugin.getLogger().warning(
                        "El jugador " + player.getName()
                                + " no tenía una cuenta. Intentando crearla."
                );

                economy.createAccount(player);
            }

            // Comprueba el resultado real de la operación.
            int result = economy.addMoney(player, amount);

            if (result != EconomyAPI.RET_SUCCESS) {
                plugin.getLogger().error(
                        "EconomyAPI no pudo entregar la recompensa a "
                                + player.getName()
                                + ". Código de respuesta: " + result
                                + ". Cantidad: " + amount
                );
                return false;
            }

            String message = plugin.getConfigManager()
                    .getMessage("payment")
                    .replace("%amount%", formatAmount(amount))
                    .replace("%rank%", payment.rank());

            player.sendMessage(message);

            plugin.getLogger().info(
                    "Recompensa entregada: "
                            + player.getName()
                            + " recibió $" + formatAmount(amount)
                            + " (rango: " + payment.rank() + ")."
            );

            return true;

        } catch (Exception exception) {
            plugin.getLogger().error(
                    "Error entregando dinero a "
                            + player.getName(),
                    exception
            );

            return false;
        }
    }

    private String formatAmount(double amount) {
        return String.format(Locale.US, "%,.2f", amount);
    }
}


