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

    /**
     * Entrega la recompensa económica al jugador.
     */
    public boolean giveReward(
            Player player,
            PaymentManager.RankPayment payment
    ) {

        if (player == null || !player.isOnline()) {
            return false;
        }

        if (payment == null) {
            return false;
        }

        double amount = payment.amount();

        if (!Double.isFinite(amount) || amount <= 0) {
            plugin.getLogger().warning(
                    "Se intentó entregar una cantidad inválida."
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

            economy.addMoney(player, amount);

            String message = plugin.getConfigManager()
                    .getMessage("payment")
                    .replace("%amount%", formatAmount(amount))
                    .replace("%rank%", payment.rank());

            player.sendMessage(message);

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

    /**
     * Formatea las cantidades económicas.
     */
    private String formatAmount(double amount) {
        return String.format(Locale.US, "%,.2f", amount);
    }
}

