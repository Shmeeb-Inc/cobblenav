package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.metacontent.cobblenav.CobblenavItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokerodItem.class)
public class PokeRodItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    protected void injectUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        // Apex fork: must not touch CobblenavItems — its static init constructs Item
        // instances, whose intrusive registry holders throw after the registry freezes.
        // This ran on every Pokerod use, and the Fishingnav offhand check can never be
        // true without the item registered, so the injection is a no-op.
    }
}
