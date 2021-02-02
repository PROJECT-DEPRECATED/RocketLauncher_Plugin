package org.projecttl.plugin.rocket.launcher.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.projecttl.plugin.rocket.launcher.info.DefaultLauncher

class GetLauncher: CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (command.name == "launcher") {
                when (args.size) {
                    0 -> {
                        return false
                    }

                    1 -> {
                        if (args[0].equals("default", true)) {
                            val defaultLauncher = ItemStack(DefaultLauncher().launcherItem)
                            DefaultLauncher().itemMeta(defaultLauncher)

                            with(sender) {
                                inventory.addItem(defaultLauncher)
                                sendMessage("Rocket_Launcher> ${defaultLauncher.itemMeta.displayName}${ChatColor.GREEN} is successful added your inventory!")
                            }

                            return true
                        }
                    }
                }
            }
        }

        return false
    }
}