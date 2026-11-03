
package com.brunojaegerk.manager;

import cn.nukkit.utils.Config;
import com.brunojaegerk.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigManager {

    private final Main plugin;
    private Config config;

    private final Map<String, PaymentSettings> payments =
            new LinkedHashMap<>();

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void load() throws IOException {
        Path folder = plugin.getDataFolder().toPath();
        Path file = folder.resolve("config.yml");

        Files.createDirectories(folder);

        if (Files.notExists(file)) {
            Files.writeString(file, getDefaultConfig());
            plugin.getLogger().info("Configuración inicial creada.");
        }

        config = new Config(file.toFile(), Config.YAML);

        loadPayments();
    }

    /**
     * Recarga la configuración desde el archivo.
     */
    public void reload() throws IOException {
        load();
        plugin.getLogger().info("Configuración recargada.");
    }

    private void loadPayments() {
        payments.clear();

        Object section = config.get("payments");

        if (!(section instanceof Map<?, ?> paymentSection)) {
            plugin.getLogger().warning(
                    "La sección payments no es válida."
            );
            return;
        }

        for (Map.Entry<?, ?> entry : paymentSection.entrySet()) {

            String rank = String.valueOf(entry.getKey());

            if (!(entry.getValue() instanceof Map<?, ?> values)) {
                plugin.getLogger().warning(
                        "Configuración inválida para el rango: " + rank
                );
                continue;
            }

            String permission = getString(values, "permission", "");
            double amount = getDouble(values, "amount", -1);
            int interval = getInt(values, "interval-seconds", -1);
            int priority = getInt(values, "priority", 0);

            if (!Double.isFinite(amount) || amount < 0) {
                plugin.getLogger().warning(
                        "Cantidad inválida para el rango: " + rank
                );
                continue;
            }

            if (interval <= 0) {
                plugin.getLogger().warning(
                        "Intervalo inválido para el rango: " + rank
                );
                continue;
            }

            payments.put(
                    rank,
                    new PaymentSettings(
                            permission,
                            amount,
                            interval,
                            priority
                    )
            );
        }

        if (!payments.containsKey("default")) {
            plugin.getLogger().warning(
                    "No existe un rango default válido en config.yml."
            );
        }

        plugin.getLogger().info(
                "Rangos de pago cargados: " + payments.size()
        );
    }

    private String getString(
            Map<?, ?> values,
            String key,
            String fallback
    ) {
        Object value = values.get(key);

        return value == null
                ? fallback
                : String.valueOf(value);
    }

    private double getDouble(
            Map<?, ?> values,
            String key,
            double fallback
    ) {
        Object value = values.get(key);

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private int getInt(
            Map<?, ?> values,
            String key,
            int fallback
    ) {
        Object value = values.get(key);

        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    public String getMessage(String key) {
        return config.getString(
                "messages." + key,
                "§cMensaje no configurado: " + key
        );
    }

    public boolean isAfkPayoutEnabled() {
        return config.getBoolean("settings.afk-payout", false);
    }

    public int getActivityTimeout() {
        return Math.max(
                30,
                config.getInt("settings.activity-timeout-seconds", 300)
        );
    }

    public Map<String, PaymentSettings> getPayments() {
        return Collections.unmodifiableMap(payments);
    }

    public Config getConfig() {
        return config;
    }

    private String getDefaultConfig() {
        return """
                # TimeIsMoneyNK - DeluxeMine

                settings:
                  afk-payout: false
                  activity-timeout-seconds: 300

                # Configuracion de rangos
                # interval-seconds se expresa en segundos.
                # La prioridad mas alta determina el rango aplicado.

                payments:

                  default:
                    permission: ""
                    amount: 100
                    interval-seconds: 600
                    priority: 0

                  vip:
                    permission: "timeismoney.rank.vip"
                    amount: 150
                    interval-seconds: 600
                    priority: 1

                  vip-plus:
                    permission: "timeismoney.rank.vipplus"
                    amount: 200
                    interval-seconds: 600
                    priority: 2

                  vip-ext:
                    permission: "timeismoney.rank.vipext"
                    amount: 300
                    interval-seconds: 600
                    priority: 3

                # Mensajes personalizables
                # Puedes utilizar colores de Minecraft con §.

                messages:
                  payment: "§a§lTimeIsMoney §8» §r§aHas recibido §e$%amount% §apor permanecer conectado."
                  reload: "§aTimeIsMoneyNK: configuración recargada correctamente."
                  no-permission: "§cNo tienes permiso para utilizar este comando."
                  usage: "§eUso: /tim reload"
                  error: "§cNo se pudo recargar la configuración. Revisa la consola."
                """;
    }

    public record PaymentSettings(
            String permission,
            double amount,
            int intervalSeconds,
            int priority
    ) {
    }
}

