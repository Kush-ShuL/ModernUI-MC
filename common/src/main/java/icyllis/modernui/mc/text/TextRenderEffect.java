/*
 * Modern UI.
 * Copyright (C) 2019-2022 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package icyllis.modernui.mc.text;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.ARGB;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4fc;

import javax.annotation.Nonnull;

/**
 * Draw underline or strikethrough.
 */
public class TextRenderEffect {

    /**
     * Offset from the string's baseline as which to draw the underline
     */
    public static final float UNDERLINE_OFFSET = 2f / 3f;

    /**
     * Offset from the string's baseline as which to draw the strikethrough line
     */
    public static final float STRIKETHROUGH_OFFSET = -7f / 2f;

    /**
     * Thickness of the underline
     */
    public static final float UNDERLINE_THICKNESS = 9f / 12f;

    /**
     * Thickness of the strikethrough line
     */
    public static final float STRIKETHROUGH_THICKNESS = 9f / 12f;

    /**
     * Offset Z to ensure that effects render over characters in 3D world
     */
    public static final float EFFECT_DEPTH = 0.01f;

    private TextRenderEffect() {
    }

    public static void drawUnderline(@Nonnull Matrix3x2fc pose, @Nonnull VertexConsumer builder,
                                     float start, float end, float baseline,
                                     int r, int g, int b, int a) {
        drawLine(pose, builder, start, end, baseline + UNDERLINE_OFFSET, UNDERLINE_THICKNESS, r, g, b, a);
    }

    public static void drawStrikethrough(@Nonnull Matrix3x2fc pose, @Nonnull VertexConsumer builder,
                                         float start, float end, float baseline,
                                         int r, int g, int b, int a) {
        drawLine(pose, builder, start, end, baseline + STRIKETHROUGH_OFFSET, STRIKETHROUGH_THICKNESS, r, g, b, a);
    }

    public static void drawUnderline(@Nonnull Matrix4fc matrix, @Nonnull VertexConsumer builder,
                                     float start, float end, float baseline,
                                     int r, int g, int b, int a, int light) {
        drawLine(matrix, builder, start, end, baseline + UNDERLINE_OFFSET, UNDERLINE_THICKNESS,
                ARGB.color(a, r, g, b), light);
    }

    public static void drawStrikethrough(@Nonnull Matrix4fc matrix, @Nonnull VertexConsumer builder,
                                         float start, float end, float baseline,
                                         int r, int g, int b, int a, int light) {
        drawLine(matrix, builder, start, end, baseline + STRIKETHROUGH_OFFSET, STRIKETHROUGH_THICKNESS,
                ARGB.color(a, r, g, b), light);
    }

    private static void drawLine(Matrix4fc matrix, VertexConsumer builder,
                                 float left, float right, float top, float thickness,
                                 int color, int light) {
        GlyphManager.getInstance().getEffectGlyph()
                .createEffect(left, top, right, top + thickness, EFFECT_DEPTH, color, 0, 0)
                .render(matrix, builder, light, false);
    }

    private static void drawLine(Matrix3x2fc pose, VertexConsumer builder,
                                 float left, float right, float top, float thickness,
                                 int r, int g, int b, int a) {
        float bottom = top + thickness;
        builder.addVertexWith2DPose(pose, right, bottom).setColor(r, g, b, a);
        builder.addVertexWith2DPose(pose, right, top).setColor(r, g, b, a);
        builder.addVertexWith2DPose(pose, left, top).setColor(r, g, b, a);
        builder.addVertexWith2DPose(pose, left, bottom).setColor(r, g, b, a);
    }
}
