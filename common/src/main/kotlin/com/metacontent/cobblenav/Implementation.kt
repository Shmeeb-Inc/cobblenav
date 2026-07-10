package com.metacontent.cobblenav

import com.cobblemon.mod.common.NetworkManager
import com.metacontent.cobblenav.util.ModDependency
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import kotlin.reflect.KClass

interface Implementation {
    val networkManager: NetworkManager

    // Apex fork: lets server-side code skip clientbound packets for players
    // whose client does not have this mod installed.
    fun canSendToPlayer(player: ServerPlayer, packetId: ResourceLocation): Boolean = true

    fun registerItems()

    fun registerCommands()

    fun injectLootTables()

    fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(identifier: ResourceLocation, argumentClass: KClass<A>, serializer: ArgumentTypeInfo<A, T>)

    fun isModInstalled(mod: ModDependency): Boolean
}