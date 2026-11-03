package com.brunojaegerk.manager;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.brunojaegerk.Main;

public class TimCommand extends Command {

    private final Main plugin;

    public TimCommand(Main plugin) {
        super(
                "tim",
                "Comando principal de TimeIsMoneyNK",
                "/tim reload"
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
            sender.sendMessage(
                    "§aTimeIsMoneyNK §7- Sistema de recompensas"
            );
            sender.sendMessage(
                    "§e/tim reload §7- Recargar configuración"
            );
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

        sender.sendMessage(
                plugin.getConfigManager().getMessage("usage")
        );

        return true;
    }
}

