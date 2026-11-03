package com.brunojaegerk;

import cn.nukkit.plugin.PluginBase;
import com.brunojaegerk.manager.*;
import com.brunojaegerk.manager.TimCommand;

public class Main extends PluginBase {

    private static Main instance;

    private ConfigManager configManager;
    private PaymentManager paymentManager;
    private RewardManager rewardManager;
    private AFKManager afkManager;
    private PaymentSchedulerManager paymentSchedulerManager;

    @Override
    public void onEnable() {

        instance = this;

        getLogger().info("================================");
        getLogger().info("       TimeIsMoneyNK");
        getLogger().info("================================");

        try {

            configManager = new ConfigManager(this);
            configManager.load();

            paymentManager = new PaymentManager(this);
            rewardManager = new RewardManager(this);
            afkManager = new AFKManager(this);

            paymentSchedulerManager =
                    new PaymentSchedulerManager(this);

            getServer().getCommandMap().register(
                    "timeismoneynk",
                    new TimCommand(this)
            );

            getServer().getPluginManager().registerEvents(
                    new PlayerActivityListener(this),
                    this
            );

            paymentSchedulerManager.start();

            getLogger().info("Configuración cargada correctamente.");
            getLogger().info("PaymentManager iniciado.");
            getLogger().info("RewardManager iniciado.");
            getLogger().info("AFKManager iniciado.");
            getLogger().info("PaymentSchedulerManager iniciado.");
            getLogger().info("PlayerActivityListener registrado.");
            getLogger().info("TimeIsMoneyNK habilitado correctamente.");

        } catch (Exception exception) {

            getLogger().error(
                    "No se pudo iniciar TimeIsMoneyNK.",
                    exception
            );

            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

        if (paymentSchedulerManager != null) {
            paymentSchedulerManager.stop();
        }

        if (afkManager != null) {
            afkManager.clear();
        }

        getLogger().info("TimeIsMoneyNK desactivado.");

        instance = null;
    }

    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PaymentManager getPaymentManager() {
        return paymentManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public AFKManager getAfkManager() {
        return afkManager;
    }

    public PaymentSchedulerManager getPaymentSchedulerManager() {
        return paymentSchedulerManager;
    }
}







