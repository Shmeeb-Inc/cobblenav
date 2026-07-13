package com.metacontent.cobblenav.client.gui.widget.spawndata

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.spawndata.BlockConditions
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import kotlin.math.ceil

class BlockConditionWidget(
    val blockConditions: BlockConditions,
    x: Int,
    y: Int,
    width: Int,
    val horizontalGap: Int,
    val verticalGap: Int
) : SoundlessWidget(x, y, width, 0, Component.literal("Block Conditions")) {
    companion object {
        const val ITEM_SIZE = 16
    }

    init {
        if (blockConditions.isNotEmpty()) {
            val rows = ceil((blockConditions.size * ITEM_SIZE + (blockConditions.size - 1) * horizontalGap) / width.toFloat()).toInt()
            height = rows * ITEM_SIZE + (rows - 1) * verticalGap
        }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        var lineY = y
        var itemCount = 0
        blockConditions.asItemStacks.forEach {
            val itemX = x - 2 + itemCount * (ITEM_SIZE + horizontalGap)
            guiGraphics.renderFakeItem(it, itemX, lineY)
            // Apex fork: block name on hover, deferred through the screen's tooltip
            // pass so it renders above the Pokenav frame.
            if (i in itemX until itemX + ITEM_SIZE && j in lineY until lineY + ITEM_SIZE) {
                Minecraft.getInstance().screen?.setTooltipForNextRenderPass(it.hoverName)
            }
            itemCount++
            if (itemCount * (ITEM_SIZE + horizontalGap) >= width) {
                lineY += ITEM_SIZE + verticalGap
                itemCount = 0
            }
        }
    }
}