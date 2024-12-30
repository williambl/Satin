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
package org.ladysnake.satin.impl;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderPass;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.managed.uniform.SamplerUniformV2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

/**
 * A sampler uniform applying to a {@link PostEffectPass}
 */
public final class ManagedPassSamplerUniform extends ManagedSamplerUniformBase implements SamplerUniformV2, PostEffectPass.Sampler {
    public ManagedPassSamplerUniform(String name) {
        super(name);
    }

    @Override
    public void preRender(RenderPass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
        // NO-OP
    }

    @Override
    public void bind(ShaderProgram program, Map<Identifier, Handle<Framebuffer>> internalTargets) {
        program.addSamplerTexture(this.name, this.value.getAsInt());
    }

    @Override
    public boolean findUniformTargets(List<PostEffectPass> passes) {
        List<SamplerAccess> targets = new ArrayList<>(passes.size());
        boolean found = false;
        for (PostEffectPass pass : passes) {
            pass.addSampler(this);
            found = true;
        }
        this.targets = targets.toArray(new SamplerAccess[0]);
        this.syncCurrentValues();
        return found;
    }

    @Override
    public void set(AbstractTexture texture) {
        set(texture::getGlId);
    }

    @Override
    public void set(Framebuffer textureFbo) {
        set(textureFbo::getColorAttachment);
    }

    @Override
    public void set(int textureName) {
        set(() -> textureName);
    }

    @Override
    public void set(IntSupplier value) {
        SamplerAccess[] targets = this.targets;
        if (targets.length > 0 && this.value != value) {
            this.value = value;
        }
    }
}
