package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataFactory
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.server.level.ServerPlayer

/**
 * Apex fork: wraps a [PlayerInstancedDataFactory] so its data is only synced to
 * clients that actually have this mod installed. Cobblemon's
 * SetClientPlayerDataPacket.decode throws on unknown data store types, which
 * would disconnect players whose client lacks this mod.
 */
class ClientGatedPlayerDataFactory<T : InstancedPlayerData>(
    private val delegate: PlayerInstancedDataFactory<T>
) : PlayerInstancedDataFactory<T> by delegate {
    override fun sendToPlayer(player: ServerPlayer) {
        if (Cobblenav.hasClientMod(player)) {
            delegate.sendToPlayer(player)
        }
    }
}
