package com.metacontent.cobblenav

import com.mojang.brigadier.arguments.ArgumentType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import kotlin.reflect.KClass

class CobbleNavFabric :
	ModInitializer,
	CobbleNavImplementation {
	override val networkManager = CobblenavFabricNetworkManager

	override fun onInitialize() {
		CobbleNav.init(this)
		networkManager.registerMessages()
		networkManager.registerServerHandlers()
	}

	// Apex fork: no items, creative tab, or wanderer trades are registered so that
	// clients without this mod pass Fabric registry sync and can join the server.
	override fun registerItems() {
	}

	override fun registerCommands() {
		CommandRegistrationCallback.EVENT.register(CobbleNavCommands::register)
	}

	override fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(
		identifier: ResourceLocation,
		argumentClass: KClass<A>,
		serializer: ArgumentTypeInfo<A, T>,
	) {
		ArgumentTypeRegistry.registerArgumentType(identifier, argumentClass.java, serializer)
	}

	// Apex fork: no loot injection since the items are not registered.
	override fun injectLootTables() {
	}

	override fun canSendToPlayer(player: ServerPlayer, packetId: ResourceLocation): Boolean = ServerPlayNetworking.canSend(player, packetId)
}
