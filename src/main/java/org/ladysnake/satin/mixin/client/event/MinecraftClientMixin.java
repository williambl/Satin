/*
 * Satin
 * Copyright (C) 2019-2024 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package org.ladysnake.satin.mixin.client.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.ladysnake.satin.api.event.ResolutionChangeCallback;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Final
    @Shadow
    private Window window;

    @Inject(method = "onResolutionChanged", at = @At("RETURN"))
    private void hookResolutionChanged(CallbackInfo info) {
        int width = this.window.getFramebufferWidth();
        int height = this.window.getFramebufferHeight();
        ResolutionChangeCallback.EVENT.invoker().onResolutionChanged(width, height);
    }
}