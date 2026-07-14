package com.metacontent.cobblenav.command

import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.networking.packet.client.OpenFishingnavPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.util.isTraveling
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component

/**
 * Apex fork: the Fishingnav item is not registered, so this command is the way
 * to open the Fishingnav UI. It sends the same packet the item's use action
 * sent, keeping the item's guard against opening while a fish is traveling.
 */
object OpenFishingnavCommand : CobblenavCommand {
    const val BASE = "fishingnav"
    const val OPEN = "open"

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(literal(BASE).then(literal(OPEN).executes(::open)))
    }

    private fun open(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.playerOrException
        if (!Cobblenav.hasClientMod(player)) {
            context.source.sendFailure(Component.literal("Cobblenav is not installed on this client"))
            return 0
        }
        val bobber = player.fishing
        if (bobber is PokeRodFishingBobberEntity && bobber.isTraveling()) {
            return 0
        }
        OpenFishingnavPacket(PokenavOS("Fishing", canUseFishingAid = true)).sendToPlayer(player)
        return Command.SINGLE_SUCCESS
    }
}
