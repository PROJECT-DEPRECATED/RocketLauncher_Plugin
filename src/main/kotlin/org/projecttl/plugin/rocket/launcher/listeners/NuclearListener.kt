package org.projecttl.plugin.rocket.launcher.listeners

import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.projecttl.plugin.rocket.launcher.RocketLauncher
import org.projecttl.plugin.rocket.launcher.info.DefaultLauncher
import org.projecttl.plugin.rocket.launcher.info.Nuclear

class NuclearListener(private val plugin: RocketLauncher): Listener {

    @EventHandler
    fun onNuclearListener(event: PlayerInteractEvent) {
        val player: Player = event.player
        val action: Action = event.action

        val reloading = plugin.weaponConfig().getBoolean("plugin.rocket.launcher.nuclear.${player.name}.reload")
        val path = plugin.weaponConfig().getInt("plugin.rocket.launcher.nuclear.${player.name}.ammo")

        val ammo = ItemStack(Material.GUNPOWDER, 1)
        val launcher = ItemStack(Nuclear().launcherItem)
        DefaultLauncher().itemMeta(launcher)

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (player.inventory.itemInMainHand.type == launcher.type) {
                if (player.inventory.itemInMainHand.itemMeta.displayName == launcher.itemMeta.displayName) {
                    when (path) {
                        0 -> {
                            with (player) {
                                playSound(this.location, Sound.BLOCK_IRON_DOOR_CLOSE, 100.toFloat(), 2.toFloat())
                                sendActionBar("${ChatColor.GOLD}Left Bullet: ${ChatColor.RED}$path${ChatColor.GREEN}/1")
                            }
                        }

                        1 -> {
                            val bullet: Projectile = player.launchProjectile(Fireball::class.java).let { bullet ->
                                bullet.velocity = player.location.direction.multiply(1.5)

                                bullet
                            }

                            with (bullet) {
                                world.playEffect(this.location, Effect.SMOKE, 10)
                                world.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 100.toFloat(), 0.toFloat())
                            }

                            plugin.explodes.add(bullet.entityId)

                            plugin.weaponConfig().set("plugin.rocket.launcher.default.${player.name}.ammo", 0)
                            player.sendActionBar("${ChatColor.GOLD}Left Bullet: ${ChatColor.GREEN}$path/1")
                        }
                    }

                    event.isCancelled = true
                }
            }
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (player.inventory.itemInMainHand.type == launcher.type && player.inventory.itemInOffHand.type == ammo.type) {
                if (player.inventory.itemInMainHand.itemMeta.displayName == launcher.itemMeta.displayName) {
                    when (path) {
                        0 -> {
                            with (player) {
                                when {
                                    !reloading -> {
                                        sendActionBar("${ChatColor.GOLD}Reloading...")
                                        playSound(this.location, Sound.BLOCK_IRON_DOOR_OPEN, 100.toFloat(), 2.toFloat())

                                        plugin.weaponConfig()
                                            .set("plugin.rocket.launcher.nuclear.${player.name}.reload", true)

                                        inventory.itemInOffHand.subtract(1)
                                        object: BukkitRunnable() {
                                            override fun run() {
                                                playSound(
                                                    player.location,
                                                    Sound.BLOCK_IRON_DOOR_CLOSE,
                                                    100.toFloat(),
                                                    2.toFloat()
                                                )

                                                plugin.weaponConfig()
                                                    .set("plugin.rocket.launcher.nuclear.${player.name}.ammo", 1)
                                                plugin.weaponConfig()
                                                    .set("plugin.rocket.launcher.nuclear.${player.name}.reload", false)
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

                        1 -> {
                            with (player) {
                                sendActionBar("${ChatColor.GOLD}Left Bullet: ${ChatColor.GREEN}$path/1")
                                playSound(player.location, Sound.BLOCK_IRON_DOOR_CLOSE, 100.toFloat(), 2.toFloat())
                            }
                        }
                    }

                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val entity = event.entity
        val explodes = plugin.explodes

        if (entity is Fireball) {
            val entityId: Int = event.entity.entityId

            if (explodes.contains(entityId)) {
                explodes.remove(entityId)
                entity.world.createExplosion(entity.location, 400.toFloat())
            }
        }
    }
}