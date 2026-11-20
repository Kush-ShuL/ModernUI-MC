package icyllis.modernui.mc.mixin;

import icyllis.modernui.view.KeyEvent;
import org.lwjgl.sdl.SDLKeyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = KeyEvent.class, remap = false)
public abstract class MixinModernKeyEvent {

    @Shadow
    public abstract int getScanCode();

    /**
     * @author Modern UI
     * @reason Resolve the unmodified key using SDL's current keyboard layout.
     */
    @Overwrite
    public final char getMappedChar() {
        int keycode = SDLKeyboard.SDL_GetKeyFromScancode(getScanCode(), (short) 0, false);
        return keycode > Character.MAX_VALUE || Character.isISOControl(keycode) ? 0 : (char) keycode;
    }
}
