package icyllis.modernui.mc.mixin;

import icyllis.modernui.mc.MuiModApi;
import icyllis.modernui.mc.UIManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Gui.class)
public abstract class MixinGui {

    @Shadow
    @Nullable
    private Screen screen;

    @Shadow
    @Final
    private GuiRenderState guiRenderState;

    @Inject(method = "setScreen", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/gui/Gui;screen:Lnet/minecraft/client/gui/screens/Screen;",
            opcode = Opcodes.PUTFIELD))
    private void onSetScreen(@Nullable Screen newScreen, CallbackInfo ci) {
        MuiModApi.dispatchOnScreenChange(screen, newScreen);
    }

    @Inject(method = "extractRenderState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/toasts/ToastManager;extractRenderState(" +
                    "Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
    private void beforeToasts(DeltaTracker deltaTracker, boolean renderLevel, boolean resourcesLoaded, CallbackInfo ci) {
        UIManager.getInstance().renderAbove(guiRenderState);
    }
}
