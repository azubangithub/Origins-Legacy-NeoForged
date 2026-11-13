package io.github.apace100.apoli.power;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.apace100.apoli.Apoli;
import net.minecraft.client.renderer.RenderPipelines;

public class OverlayPowerPipelines {
    public static final RenderPipeline.Snippet OVERLAY_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
        .withFragmentShader("core/position_tex_color")
        .withVertexShader("core/position_tex_color")
        .withSampler("Sampler0")
        .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
        .withUniform("ColorModulator", UniformType.UNIFORM_BUFFER)
        .withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY))
        .withDepthStencilState(new DepthStencilState(CompareOp.NEVER_PASS, false))
        .buildSnippet();

    public static final RenderPipeline OVERLAY_PIPELINE = RenderPipeline.builder(OVERLAY_SNIPPET)
        .withLocation(Apoli.identifier("pipeline/overlay"))
        .build();

    public static final RenderPipeline NAUSEA_PIPELINE = RenderPipeline.builder(OVERLAY_SNIPPET)
        .withLocation(Apoli.identifier("pipeline/overlay_nausea"))
        .withColorTargetState(new ColorTargetState(BlendFunction.ADDITIVE))
        .build();
}
