package icyllis.modernui.mc;

import net.minecraft.client.Minecraft;
import org.lwjgl.sdl.SDL_DialogFileCallback;
import org.lwjgl.sdl.SDL_DialogFileFilter;
import org.lwjgl.sdl.SDLDialog;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.lwjgl.system.MemoryUtil.*;

public final class FileDialogs {

    private FileDialogs() {
    }

    public static CompletableFuture<Path> open(String description, String extensions) {
        var result = new CompletableFuture<Path>();
        var minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            var name = memUTF8(description);
            var pattern = memUTF8(extensions);
            var filter = SDL_DialogFileFilter.malloc(1);
            filter.get(0).set(name, pattern);
            var callback = new SDL_DialogFileCallback() {
                @Override
                public void invoke(long userdata, long files, int filterIndex) {
                    long path = files == NULL ? NULL : memGetAddress(files);
                    try {
                        result.complete(path == NULL ? null : Path.of(memUTF8(path)));
                    } catch (RuntimeException e) {
                        result.completeExceptionally(e);
                    } finally {
                        // The callback and filter must remain alive until SDL returns.
                        minecraft.schedule(() -> {
                            free();
                            filter.free();
                            memFree(name);
                            memFree(pattern);
                        });
                    }
                }
            };
            SDLDialog.SDL_ShowOpenFileDialog(callback, NULL, minecraft.getWindow().handle(),
                    filter, (CharSequence) null, false);
        });
        return result;
    }
}
