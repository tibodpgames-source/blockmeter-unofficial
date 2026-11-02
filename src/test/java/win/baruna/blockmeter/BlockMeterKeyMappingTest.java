package win.baruna.blockmeter;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.junit.jupiter.api.Test;
import org.lwjgl.sdl.SDLScancode;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class BlockMeterKeyMappingTest {
    private static boolean polledOnFocus(KeyMapping mapping) throws Exception {
        var method = KeyMapping.class.getDeclaredMethod("shouldSetOnIngameFocus");
        method.setAccessible(true);
        return (boolean) method.invoke(mapping);
    }

    private static BlockMeterKeyMapping mapping(int code) {
        return new BlockMeterKeyMapping("key.blockmeter.regression", InputConstants.Type.KEYBOARD,
                code, KeyMapping.Category.MISC);
    }

    @Test
    void legacyMinusOneIsNotUnboundAndWouldReadOutsideSdlBuffer() throws Exception {
        var legacy = new KeyMapping("key.blockmeter.legacy", -1, KeyMapping.Category.MISC);
        assertEquals(0, InputConstants.UNKNOWN.getValue());
        assertFalse(legacy.isUnbound());
        assertTrue(polledOnFocus(legacy));
        var sdlState = ByteBuffer.allocateDirect(SDLScancode.SDL_SCANCODE_COUNT);
        assertThrows(IndexOutOfBoundsException.class, () -> sdlState.get(legacy.getDefaultKey().getValue()));
    }

    @Test
    void unboundDefaultIsSkippedByVanillaFocusPolling() throws Exception {
        var repaired = mapping(InputConstants.UNKNOWN.getValue());
        assertTrue(repaired.isUnbound());
        assertFalse(polledOnFocus(repaired));
        assertEquals(0, repaired.getDefaultKey().getValue());
    }

    @Test
    void savedNegativeAndOutOfRangeKeyboardBindingsAreNormalized() throws Exception {
        var repaired = mapping(InputConstants.KEY_M);
        for (int invalid : new int[]{-1, -12, SDLScancode.SDL_SCANCODE_COUNT, Integer.MAX_VALUE}) {
            // Options.load resolves a persisted name, then calls setKey on the mapping.
            repaired.setKey(InputConstants.getKey("key.keyboard." + invalid));
            assertTrue(repaired.isUnbound(), "Invalid keyboard code: " + invalid);
            assertFalse(polledOnFocus(repaired));
        }
    }

    @Test
    void validKeyboardBindingsAndMouseAssignmentsArePreserved() throws Exception {
        var repaired = mapping(InputConstants.KEY_M);
        for (int code : new int[]{InputConstants.KEY_M, InputConstants.KEY_LALT,
                InputConstants.KEY_LCONTROL, InputConstants.KEY_RCONTROL,
                InputConstants.KEY_LSHIFT, InputConstants.KEY_RSHIFT}) {
            var key = InputConstants.Type.KEYBOARD.getOrCreate(code);
            repaired.setKey(key);
            assertEquals(key.getName(), repaired.saveString());
            assertTrue(polledOnFocus(repaired));
        }
        var mouse = InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_4);
        repaired.setKey(mouse);
        assertEquals(mouse.getName(), repaired.saveString());
        assertFalse(repaired.isUnbound());
        assertFalse(polledOnFocus(repaired));
    }
}
