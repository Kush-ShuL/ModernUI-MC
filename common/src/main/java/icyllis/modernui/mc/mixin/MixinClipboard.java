package icyllis.modernui.mc.mixin;

import icyllis.modernui.core.Clipboard;
import icyllis.modernui.text.TextUtils;
import org.lwjgl.sdl.SDLClipboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = Clipboard.class, remap = false)
public abstract class MixinClipboard {

    /**
     * @author Modern UI
     * @reason Read the clipboard through Minecraft's windowing library.
     */
    @Overwrite
    public static String getText() {
        String text = SDLClipboard.SDL_GetClipboardText();
        return text == null || text.isEmpty() ? null : TextUtils.validateSurrogatePairs(text);
    }

    /**
     * @author Modern UI
     * @reason Write the clipboard through Minecraft's windowing library.
     */
    @Overwrite
    public static void setText(CharSequence text) {
        SDLClipboard.SDL_SetClipboardText(text);
    }
}
