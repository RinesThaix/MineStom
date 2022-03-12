package net.minestom.server.instance.block.rule.vanilla;

import net.minestom.server.entity.Player;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.coordinate.Point;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class WallPlacementRule extends BlockPlacementRule {

    public WallPlacementRule(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull Instance instance, @NotNull Point blockPosition, @NotNull Block block) {
        final int x = blockPosition.blockX();
        final int y = blockPosition.blockY();
        final int z = blockPosition.blockZ();

        String east = "none";
        String north = "none";
        String south = "none";
        String up = "true";
        String waterlogged = "false";
        String west = "none";

        if (isConnectable(instance, x + 1, y, z)) {
            east = "low";
        }

        if (isConnectable(instance, x - 1, y, z)) {
            west = "low";
        }

        if (isConnectable(instance, x, y, z + 1)) {
            south = "low";
        }

        if (isConnectable(instance, x, y, z - 1)) {
            north = "low";
        }

        return block.withProperties(Map.of(
                "east", east,
                "north", north,
                "south", south,
                "west", west,
                "up", up,
                "waterlogged", waterlogged));
    }

    @Override
    public Block blockPlace(@NotNull Instance instance,
                            @NotNull Block block, @NotNull BlockFace blockFace, @NotNull Point blockPosition,
                            @NotNull Player pl) {
        return block;
    }

    private boolean isConnectable(Instance instance, int x, int y, int z) {
        Chunk chunk = instance.getChunk(ChunkUtils.getChunkCoordinate(x), ChunkUtils.getChunkCoordinate(z));
        if (chunk == null) {
            return false;
        }
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (chunk) {
            return chunk.getBlock(x, y, z).isSolid();
        }
    }
}
