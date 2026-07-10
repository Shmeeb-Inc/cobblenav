package com.metacontent.cobblenav.command

import com.cobblemon.mod.common.block.entity.PokeSnackBlockEntity
import com.cobblemon.mod.common.util.raycast
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.networking.packet.client.OpenPokenavPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component
import net.minecraft.world.level.ClipContext

/**
 * Apex fork: the Pokenav items are not registered, so this command is the way
 * to open the Pokenav UI. It sends the same packet the item's use action sent.
 */
object OpenPokenavCommand : PokenavCommand() {
    const val OPEN = "open"

    override fun afterBase(): ArgumentBuilder<CommandSourceStack, *> = literal(OPEN).executes(::open)

    private fun open(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.playerOrException
        if (!Cobblenav.hasClientMod(player)) {
            context.source.sendFailure(Component.literal("Cobblenav is not installed on this client"))
            return 0
        }
        val posUsedOn = player.raycast(player.blockInteractionRange().toFloat(), ClipContext.Fluid.NONE).blockPos
        val hasAreaSpawner = player.level().getBlockEntity(posUsedOn)?.let { it is PokeSnackBlockEntity } == true
        OpenPokenavPacket(
            os = PokenavOS("Lite", canUseLocation = true),
            fixedAreaPoint = posUsedOn.takeIf { hasAreaSpawner }
        ).sendToPlayer(player)
        return Command.SINGLE_SUCCESS
    }
}
