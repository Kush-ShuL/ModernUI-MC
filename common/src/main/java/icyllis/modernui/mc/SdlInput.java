package icyllis.modernui.mc;

import icyllis.modernui.view.KeyEvent;

import static com.mojang.blaze3d.platform.InputConstants.*;
import static org.lwjgl.sdl.SDLScancode.*;

final class SdlInput {

    private static final int CAPS_LOCK_ON = 0x10;
    private static final int NUM_LOCK_ON = 0x20;

    private SdlInput() {
    }

    // Modern UI 3.13 uses GLFW key codes and modifier masks.
    static int keyCode(net.minecraft.client.input.KeyEvent event) {
        int keycode = event.keycode();
        if (keycode >= 'a' && keycode <= 'z') {
            return keycode - 'a' + KeyEvent.KEY_A;
        }
        if (keycode >= '0' && keycode <= '9') {
            return keycode;
        }
        int key = event.key();
        if (key >= KEY_A && key <= KEY_Z) {
            return key - KEY_A + KeyEvent.KEY_A;
        }
        if (key >= KEY_1 && key <= KEY_9) {
            return key - KEY_1 + KeyEvent.KEY_1;
        }
        if (key >= KEY_F1 && key <= KEY_F12) {
            return key - KEY_F1 + KeyEvent.KEY_F1;
        }
        if (key >= KEY_F13 && key <= KEY_F24) {
            return key - KEY_F13 + KeyEvent.KEY_F13;
        }
        if (key >= KEY_NUMPAD1 && key <= KEY_NUMPAD9) {
            return key - KEY_NUMPAD1 + KeyEvent.KEY_KP_1;
        }
        return switch (key) {
            case KEY_0 -> KeyEvent.KEY_0;
            case KEY_SPACE -> KeyEvent.KEY_SPACE;
            case KEY_APOSTROPHE -> KeyEvent.KEY_APOSTROPHE;
            case KEY_COMMA -> KeyEvent.KEY_COMMA;
            case KEY_MINUS -> KeyEvent.KEY_MINUS;
            case KEY_PERIOD -> KeyEvent.KEY_PERIOD;
            case KEY_SLASH -> KeyEvent.KEY_SLASH;
            case KEY_SEMICOLON -> KeyEvent.KEY_SEMICOLON;
            case KEY_EQUALS -> KeyEvent.KEY_EQUAL;
            case KEY_LBRACKET -> KeyEvent.KEY_LEFT_BRACKET;
            case KEY_BACKSLASH -> KeyEvent.KEY_BACKSLASH;
            case KEY_RBRACKET -> KeyEvent.KEY_RIGHT_BRACKET;
            case KEY_GRAVE -> KeyEvent.KEY_GRAVE_ACCENT;
            case KEY_ESCAPE -> KeyEvent.KEY_ESCAPE;
            case KEY_RETURN -> KeyEvent.KEY_ENTER;
            case KEY_TAB -> KeyEvent.KEY_TAB;
            case KEY_BACKSPACE -> KeyEvent.KEY_BACKSPACE;
            case KEY_INSERT -> KeyEvent.KEY_INSERT;
            case KEY_DELETE -> KeyEvent.KEY_DELETE;
            case KEY_RIGHT -> KeyEvent.KEY_RIGHT;
            case KEY_LEFT -> KeyEvent.KEY_LEFT;
            case KEY_DOWN -> KeyEvent.KEY_DOWN;
            case KEY_UP -> KeyEvent.KEY_UP;
            case KEY_PAGEUP -> KeyEvent.KEY_PAGE_UP;
            case KEY_PAGEDOWN -> KeyEvent.KEY_PAGE_DOWN;
            case KEY_HOME -> KeyEvent.KEY_HOME;
            case KEY_END -> KeyEvent.KEY_END;
            case KEY_CAPSLOCK -> KeyEvent.KEY_CAPS_LOCK;
            case KEY_SCROLLLOCK -> KeyEvent.KEY_SCROLL_LOCK;
            case KEY_NUMLOCK -> KeyEvent.KEY_NUM_LOCK;
            case KEY_PRINTSCREEN -> KeyEvent.KEY_PRINT_SCREEN;
            case KEY_PAUSE -> KeyEvent.KEY_PAUSE;
            case KEY_NUMPAD0 -> KeyEvent.KEY_KP_0;
            case SDL_SCANCODE_KP_PERIOD, KEY_NUMPADCOMMA -> KeyEvent.KEY_KP_DECIMAL;
            case SDL_SCANCODE_KP_DIVIDE -> KeyEvent.KEY_KP_DIVIDE;
            case KEY_MULTIPLY -> KeyEvent.KEY_KP_MULTIPLY;
            case SDL_SCANCODE_KP_MINUS -> KeyEvent.KEY_KP_SUBTRACT;
            case KEY_ADD -> KeyEvent.KEY_KP_ADD;
            case KEY_NUMPADENTER -> KeyEvent.KEY_KP_ENTER;
            case KEY_NUMPADEQUALS -> KeyEvent.KEY_KP_EQUAL;
            case KEY_LSHIFT -> KeyEvent.KEY_LEFT_SHIFT;
            case KEY_LCONTROL -> KeyEvent.KEY_LEFT_CONTROL;
            case KEY_LALT -> KeyEvent.KEY_LEFT_ALT;
            case KEY_LGUI -> KeyEvent.KEY_LEFT_SUPER;
            case KEY_RSHIFT -> KeyEvent.KEY_RIGHT_SHIFT;
            case KEY_RCONTROL -> KeyEvent.KEY_RIGHT_CONTROL;
            case KEY_RALT -> KeyEvent.KEY_RIGHT_ALT;
            case KEY_RGUI -> KeyEvent.KEY_RIGHT_SUPER;
            case SDL_SCANCODE_APPLICATION, SDL_SCANCODE_MENU -> KeyEvent.KEY_MENU;
            default -> KeyEvent.KEY_UNKNOWN;
        };
    }

    static int modifiers(int modifiers) {
        int result = 0;
        if ((modifiers & MOD_SHIFT) != 0) {
            result |= KeyEvent.META_SHIFT_ON;
        }
        if ((modifiers & MOD_CONTROL) != 0) {
            result |= KeyEvent.META_CONTROL_ON;
        }
        if ((modifiers & MOD_ALT) != 0) {
            result |= KeyEvent.META_ALT_ON;
        }
        if ((modifiers & MOD_SUPER) != 0) {
            result |= KeyEvent.META_SUPER_ON;
        }
        if ((modifiers & MOD_CAPS_LOCK) != 0) {
            result |= CAPS_LOCK_ON;
        }
        if ((modifiers & MOD_NUM_LOCK) != 0) {
            result |= NUM_LOCK_ON;
        }
        return result;
    }
}
