package win.baruna.blockmeter;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.sdl.SDLScancode;

/** Keeps legacy Block Meter keyboard bindings out of SDL's keyboard-state buffer. */
final class BlockMeterKeyMapping extends KeyMapping {
    BlockMeterKeyMapping(String name, InputConstants.Type type, int code, Category category) {
        super(name, type, normalize(type.getOrCreate(code)).getValue(), category);
    }

    @Override
    public void setKey(InputConstants.Key key) {
        super.setKey(normalize(key));
    }

    private static InputConstants.Key normalize(InputConstants.Key key) {
        if (key.getType() == InputConstants.Type.KEYBOARD
                && (key.getValue() < 0 || key.getValue() >= SDLScancode.SDL_SCANCODE_COUNT)) {
            return InputConstants.UNKNOWN;
        }
        return key;
    }
}
