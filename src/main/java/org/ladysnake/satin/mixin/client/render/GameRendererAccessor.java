package org.ladysnake.satin.mixin.client.render;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Pool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Accessor
    Pool getPool();
}
