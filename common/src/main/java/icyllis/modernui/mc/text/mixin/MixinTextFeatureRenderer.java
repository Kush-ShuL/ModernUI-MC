package icyllis.modernui.mc.text.mixin;

import icyllis.modernui.mc.text.TextLayoutEngine;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;
import net.minecraft.client.renderer.feature.TextFeatureRenderer.Content;
import net.minecraft.client.renderer.feature.TextFeatureRenderer.Submit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(TextFeatureRenderer.class)
public abstract class MixinTextFeatureRenderer extends RenderTypeFeatureRenderer<Submit> {

    /**
     * @author Modern UI
     * @reason Emit shaped text into Minecraft's feature renderer vertex buffers.
     */
    @Overwrite
    protected void buildGroup(FeatureFrameContext context, List<Submit> submissions) {
        var renderer = TextLayoutEngine.getInstance().getTextRenderer();
        for (Submit submit : submissions) {
            switch (submit.content()) {
                case Content.Text text -> {
                    if (text.outlineColor() != 0) {
                        renderer.drawText8xOutline(text.string(), text.x(), text.y(), text.color(),
                                text.outlineColor(), submit.pose(), this::getVertexBuilder, submit.lightCoords());
                    } else {
                        renderer.drawText(text.string(), text.x(), text.y(), text.color(), text.dropShadow(),
                                submit.pose(), this::getVertexBuilder, submit.displayMode(),
                                text.backgroundColor(), submit.lightCoords());
                    }
                }
                case Content.StandaloneBackground background -> {
                    var quad = context.font().prepareBackground(
                            background.x0(), background.y0(), background.x1(), background.y1(), background.color());
                    quad.render(submit.pose(), getVertexBuilder(quad.renderType(submit.displayMode())),
                            submit.lightCoords(), false);
                }
            }
        }
    }
}
