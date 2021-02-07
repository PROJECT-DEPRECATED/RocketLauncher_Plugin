package org.projecttl.plugin.rocket.launcher.listeners

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.projecttl.plugin.rocket.launcher.RocketLauncher
import org.projecttl.plugin.rocket.launcher.info.DefaultLauncher
import org.projecttl.plugin.rocket.launcher.info.Armageddon

class ArmageddonListener(private val plugin: RocketLauncher): Listener {

    @EventHandler
    fun onArmageddonListener(event: PlayerInteractEvent) {
        val player: Player = event.player
        val action: Action = event.action

        val reloading = plugin.weaponConfig().getBoolean("plugin.rocket.launcher.armageddon.${player.name}.reload")
        val path = plugin.weaponConfig().getInt("plugin.rocket.launcher.armageddon.${player.name}.ammo")

        val ammo = ItemStack(Material.GUNPOWDER, 1)
        val launcher = ItemStack(Armageddon().launcherItem)
        DefaultLauncher().itemMeta(launcher)
        if (player.gameMode == GameMode.SPECTATOR) {
            return
        } else {
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                if (player.inventory.itemInMainHand.type == launcher.type) {
                    if (player.inventory.itemInMainHand.itemMeta.displayName == launcher.itemMeta.displayName) {
                        when {
                            path == 0 -> {
                                with(player) {
                                    playSound(this.location, Sound.BLOCK_IRON_DOOR_CLOSE, 100.toFloat(), 2.toFloat())
                                    sendActionBar("${ChatColor.GOLD}Left Bullet: ${ChatColor.RED}$path${ChatColor.GREEN}/4")
                                }
                            }

                            path > 0 -> {
                                val bullet: Projectile = player.launchProjectile(Snowball::class.java).let { bullet ->
                                    bullet.velocity = player.location.direction.multiply(3)

                                    bullet
                                }

                                with(bullet) {
                                    world.playEffect(this.location, Effect.SMOKE, 10)
                                    world.playSound(
                                        player.location,
                                        Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST,
                                        100.toFloat(),
                                        0.toFloat()
                                    )
                                }

                                plugin.explodes.add(bullet.entityId)

                                plugin.weaponConfig().set("plugin.rocket.launcher.armageddon.${player.name}.ammo", path - 1)
                                player.sendActionBar("${ChatColor.GOLD}Left Bullet: ${ChatColor.GREEN}$path/4")
                            }
                        }

                        event.isCancelled = true
                    }
                }
            } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (player.inventory.itemInMainHand.type == launcher.type && player.inventory.itemInOffHand.type == ammo.type) {
                    if (player.inventory.itemInMainHand.itemMeta.displayName == launcher.itemMeta.displayName) {
                        when {
                            path == 0 -> {
                                with(player) {
                                    when {
                                        !reloading -> {
                                            sendActionBar("${ChatColor.GOLD}Reloading...")
                                            playSound(
                                                this.location,
                                                Sound.BLOCK_IRON_DOOR_OPEN,
                                                100.toFloat(),
                                                2.toFloat()
                                            )

                                            plugin.weaponConfig()
                                                .set("plugin.rocket.launcher.armageddon.${player.name}.reload", true)

                                            inventory.itemInOffHand.subtract(1)
                                            object : BukkitRunnable() {
                                                override fun run() {
                                                    playSound(
                                                        player.location,
                                                        Sound.BLOCK_IRON_DOOR_CLOSE,
                                                        100.toFloat(),
                                                        2.toFloat()
                                                    )

                                                    plugin.weaponConfig()
                                                        .set("plugin.rocket.launcher.armageddon.${player.name}.ammo", 4)
                                                    plugin.weaponConfig()
                                                        .set(
                                                            "plugin.rocket.launcher.armageddon.${player.name}.reload",
                                                            false
                                                        )
                                                }
                                            }.runTaskLater(plugin, 2 * 20.toLong())
                                        }

                                        reloading -> {
                                            sendMessage("Rocket_Launcher> ${ChatColor.GOLD}You're already reloading!")
                                            playSound(
                                                this.location,
                                                Sound.ENTITY_ENDERMAN_TELEPORT,
                                                100.toFloat(),
                                                1.0.toFloat()
                                            )
                                        }

                                        else -> sendMessage("Rocket_Launcher> ${ChatColor.RED}Error!")
                                    }

                                    return@with
                                }
                            }

                            path > 0 -> {
                                with(player) {
                                    sendActionBar("${ChatColor.GOLD}Left Bullet: ${ChatColor.GREEN}$path/4")
                                    playSound(player.location, Sound.BLOCK_IRON_DOOR_CLOSE, 100.toFloat(), 2.toFloat())
                                }
                            }
                        }

                        event.isCancelled = true
                    }
                }
            }
        }
    }

    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val entity = event.entity
        val explodes = plugin.explodes

        if (entity is Snowball) {
            val entityId: Int = event.entity.entityId

            if (explodes.contains(entityId)) {
                explodes.remove(entityId)
                entity.world.createExplosion(entity.location, 200.toFloat())
            }
        }
    }
}