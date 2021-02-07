package org.projecttl.plugin.rocket.launcher.info

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Nuclear {
    private var launcherName = "${ChatColor.AQUA}Rocket Launcher"
    private var launcherLore = listOf("${ChatColor.GOLD}This is default launcher.")

    var launcherItem = ItemStack(Material.DIAMOND_SHOVEL)
    private var launcherMeta = launcherItem.itemMeta!!

    fun itemMeta(launcherItem: ItemStack) {
        launcherMeta.let { meta ->
            meta.setDisplayName(launcherName)
            meta.lore = launcherLore

            meta
        }

        launcherItem.itemMeta = launcherMeta
    }
}