package icyllis.modernui.mc.mixin;

import icyllis.modernui.view.PointerIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PointerIcon.class, remap = false)
public abstract class MixinPointerIcon {

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lorg/lwjgl/glfw/GLFW;glfwCreateStandardCursor(I)J"), require = 2)
    private static long skipDesktopCursorCreation(int shape) {
        // UIManager uses the icon type to select Minecraft's SDL cursor.
        return 0;
    }
}
