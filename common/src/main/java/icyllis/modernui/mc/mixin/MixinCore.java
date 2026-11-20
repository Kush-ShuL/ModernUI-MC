package icyllis.modernui.mc.mixin;

import icyllis.modernui.core.Core;
import org.lwjgl.sdl.SDLTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Core.class, remap = false)
public abstract class MixinCore {

    @Shadow
    private static volatile Thread sMainThread;

    /**
     * @author Modern UI
     * @reason Minecraft initializes SDL before creating the renderer.
     */
    @Overwrite
    public static void initialize() {
        sMainThread = Thread.currentThread();
    }

    /**
     * @author Modern UI
     * @reason Tie UI animations and input events to SDL's monotonic clock.
     */
    @Overwrite
    public static long timeNanos() {
        return SDLTimer.SDL_GetTicksNS();
    }

    /**
     * @author Modern UI
     * @reason Use the same clock as timeNanos.
     */
    @Overwrite
    public static long timeMillis() {
        return SDLTimer.SDL_GetTicks();
    }
}
