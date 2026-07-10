package com.metacontent.cobblenav.networking.packet

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.server
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.server.level.ServerPlayer

interface CobblenavNetworkPacket<T : NetworkPacket<T>> : NetworkPacket<T> {
    override fun sendToServer() = Cobblenav.implementation.networkManager.sendToServer(this)

    // Apex fork: players without the mod on their client cannot handle these packets.
    override fun sendToPlayer(player: ServerPlayer) {
        if (Cobblenav.implementation.canSendToPlayer(player, id)) {
            Cobblenav.implementation.networkManager.sendPacketToPlayer(player, this)
        }
    }

    override fun sendToPlayers(players: Iterable<ServerPlayer>) {
        if (players.any()) {
            players.forEach { sendToPlayer(it) }
        }
    }

    override fun sendToAllPlayers() = sendToPlayers(server()!!.playerList.players)
}