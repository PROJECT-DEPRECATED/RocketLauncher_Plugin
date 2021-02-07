package org.projecttl.plugin.rocket.launcher

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.projecttl.plugin.rocket.launcher.commands.GetLauncher
import org.projecttl.plugin.rocket.launcher.commands.arguments.ArgumentForGetLauncher
import org.projecttl.plugin.rocket.launcher.listeners.DefaultLauncherListener
import org.projecttl.plugin.rocket.launcher.listeners.NuclearListener
import java.io.File
import java.util.ArrayList


class RocketLauncher: JavaPlugin() {

    var explodes = ArrayList<Int>()
    var bullets = ArrayList<Int>()

    private var getFile: File? = null
    private var configuration: FileConfiguration? = null

    private var manager = server.pluginManager

    override fun onEnable() {
        load()
        logger.info("Plugin enabled.")

        getCommand("launcher")?.setExecutor(GetLauncher())
        getCommand("launcher")?.tabCompleter = ArgumentForGetLauncher()

        manager.registerEvents(DefaultLauncherListener(this), this)
        manager.registerEvents(NuclearListener(this), this)
    }

    override fun onDisable() {
        save()
        logger.info("Plugin disabled.")
    }

    private fun load() {
        getFile = File(dataFolder, "config.yml").also { config ->
            if (!config.exists()) {
                configuration?.save(config)
            }

            configuration?.load(config)
        }
        configuration = YamlConfiguration.loadConfiguration(getFile!!)
    }

    private fun save() {
        configuration?.save(getFile!!)
        Bukkit.broadcastMessage("Rocket_Launcher> config has successful saved")
    }

    fun weaponConfig(): FileConfiguration {
        return configuration!!
    }
}