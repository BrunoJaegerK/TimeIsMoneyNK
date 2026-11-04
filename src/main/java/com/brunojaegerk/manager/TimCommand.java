package com.brunojaegerk.manager;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.brunojaegerk.Main;

public class TimCommand extends Command {

    private final Main plugin;

    public TimCommand(Main plugin) {
        super(
                "tim",
                "Comando principal de TimeIsMoneyNK",
                "/tim <reload|status>"
        );

        this.plugin = plugin;
        setPermission("timeismoney.info");
    }

    @Override
    public boolean execute(
            CommandSender sender,
            String commandLabel,
            String[] args
    ) {

        if (args.length == 0) {
            sender.sendMessage("§a§lTimeIsMoneyNK");
            sender.sendMessage("§e/tim status §7- Ver estado de tus recompensas");
            sender.sendMessage("§e/tim reload §7- Recargar configuración");
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage(
                        "§cEste comando debe ejecutarlo un jugador."
                );
                return true;
            }

            showStatus(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("timeismoney.admin")) {
                sender.sendMessage(
                        plugin.getConfigManager()
                                .getMessage("no-permission")
                );
                return true;
            }

            try {
                plugin.getConfigManager().reload();

                sender.sendMessage(
                        plugin.getConfigManager().getMessage("reload")
                );

            } catch (Exception exception) {

                plugin.getLogger().error(
                        "Error recargando config.yml.",
                        exception
                );

                sender.sendMessage(
                        plugin.getConfigManager().getMessage("error")
                );
            }

            return true;
        }

        sender.sendMessage("§cUso: /tim <reload|status>");
        return true;
    }

    private void showStatus(Player player) {

        boolean excluded =
                plugin.getConfigManager().isWorldExcluded(player);

        boolean afk =
                plugin.getAfkManager().isAfk(player);

        boolean canReceive =
                plugin.getAfkManager().canReceivePayment(player);

        long accumulated =
                plugin.getPaymentSchedulerManager()
                        .getAccumulatedSeconds(player);

        long inactive =
                plugin.getAfkManager().getInactiveSeconds(player);

        PaymentManager.RankPayment payment =
                plugin.getPaymentManager().getPaymentFor(player);

        String world = player.getLevel().getFolderName();

        senderMessage(player, "§8§m--------------------------");
        senderMessage(player, "§a§lTimeIsMoneyNK §7- Diagnóstico");
        senderMessage(player, "§7Mundo: §f" + world);
        senderMessage(player, "§7Mundo excluido: "
                + (excluded ? "§cSí" : "§aNo"));
        senderMessage(player, "§7Estado AFK: "
                + (afk ? "§cAFK" : "§aActivo"));
        senderMessage(player, "§7Última actividad: §f"
                + inactive + " segundos");
        senderMessage(player, "§7Puede recibir pagos: "
                + (canReceive && !excluded ? "§aSí" : "§cNo"));

        if (payment == null) {
            senderMessage(player, "§7Rango de pago: §cNo encontrado");
        } else {
            senderMessage(player, "§7Rango: §f" + payment.rank());
            senderMessage(player, "§7Recompensa: §a$"
                    + payment.amount());
            senderMessage(player, "§7Intervalo: §f"
                    + payment.intervalSeconds() + " segundos");

            long remaining = Math.max(
                    0L,
                    payment.intervalSeconds() - accumulated
            );

            senderMessage(player, "§7Tiempo acumulado: §f"
                    + accumulated + " segundos");

            senderMessage(player, "§7Faltan para el pago: §e"
                    + remaining + " segundos");
        }

        senderMessage(player, "§8§m--------------------------");
    }

    private void senderMessage(Player player, String message) {
        player.sendMessage(message);
    }
}


