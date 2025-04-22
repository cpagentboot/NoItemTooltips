package org.D7lan.noitemtooltips.client.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.D7lan.noitemtooltips.client.NoItemTooltipsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    // track last tooltip and when we started hovering
    private static List<Component> lastLines = new ArrayList<>();
    private static long hoverStart = 0L;

    @Inject(
            method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/ResourceLocation;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void delayedTooltips(Font font,
                                 List<Component> lines,
                                 Optional<TooltipComponent> extra,
                                 int x, int y,
                                 ResourceLocation tex,
                                 CallbackInfo ci)
    {
        // if mod is turned off, let vanilla run unmodified
        if (!NoItemTooltipsMod.enabled) {
            return;
        }

        // if tooltip contents changed, reset timer
        if (!lines.equals(lastLines)) {
            lastLines = new ArrayList<>(lines);
            hoverStart = System.currentTimeMillis();
            ci.cancel();
            return;
        }

        // if hovered under threshold, skip rendering
        if (System.currentTimeMillis() - hoverStart < NoItemTooltipsMod.hoverDelayMs) {
            ci.cancel();
        }
        // otherwise, let vanilla renderTooltip proceed
    }
}
