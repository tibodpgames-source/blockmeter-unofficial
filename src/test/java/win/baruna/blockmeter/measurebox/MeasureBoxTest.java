package win.baruna.blockmeter.measurebox;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.junit.jupiter.api.Test;
import win.baruna.blockmeter.BoxPayload;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MeasureBoxTest {
    private MeasureBox box(BlockPos start, BlockPos end, String dimension) {
        return new MeasureBox(start, end, Identifier.parse(dimension), DyeColor.RED, true, 2, 1);
    }

    @Test
    void measurementSurvivesPacketRoundTrip() {
        var original = box(new BlockPos(-5, -64, 10), new BlockPos(8, 319, -20), "minecraft:overworld");
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            original.writePacketBuf(buffer);
            var decoded = MeasureBox.fromPacketByteBuf(buffer);
            assertEquals(original.getBlockStart(), decoded.getBlockStart());
            assertEquals(original.getBlockEnd(), decoded.getBlockEnd());
            assertEquals(original.getDimension(), decoded.getDimension());
            assertEquals(DyeColor.RED, decoded.color);
            assertTrue(decoded.isFinished());
            assertEquals(2, decoded.mode);
            assertEquals(1, decoded.orientation);
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    void sharingPacketPreservesPlayersAndDimensions() {
        var first = box(BlockPos.ZERO, new BlockPos(4, 8, 12), "minecraft:overworld");
        var second = box(new BlockPos(3, 2, 1), BlockPos.ZERO, "minecraft:the_nether");
        var payload = new BoxPayload(Map.of("BuilderOne", List.of(first, second), "BuilderTwo", List.of()));
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            BoxPayload.CODEC.encode(buffer, payload);
            var decoded = BoxPayload.CODEC.decode(buffer);
            assertEquals(2, decoded.receivedBoxes().size());
            assertEquals(2, decoded.receivedBoxes().get("BuilderOne").size());
            assertTrue(decoded.receivedBoxes().get("BuilderTwo").isEmpty());
            assertEquals(second.getDimension(), decoded.receivedBoxes().get("BuilderOne").get(1).getDimension());
            assertEquals(second.getBlockStart(), decoded.receivedBoxes().get("BuilderOne").get(1).getBlockStart());
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    void clearSharingPacketRemainsEmpty() {
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            BoxPayload.CODEC.encode(buffer, new BoxPayload(Map.of("", List.of())));
            var decoded = BoxPayload.CODEC.decode(buffer);
            assertTrue(decoded.receivedBoxes().get("").isEmpty());
        } finally {
            buffer.release();
        }
    }
}
