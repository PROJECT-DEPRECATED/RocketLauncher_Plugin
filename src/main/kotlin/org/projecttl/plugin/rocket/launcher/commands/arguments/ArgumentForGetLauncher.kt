package org.projecttl.plugin.rocket.launcher.commands.arguments

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ArgumentForGetLauncher: TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        if (sender is Player) {
            when (args.size) {
                1 -> {
                    val firstArgument: ArrayList<String> = ArrayList()
                    firstArgument.add("default")
                    firstArgument.add("armageddon")

                    return firstArgument
                }
            }
        }

        return null
    }
}