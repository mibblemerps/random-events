package net.mitchfizz05.randomevents.util;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A very valuable class that assists with coordinate based operations, especially randomly picking coordinates.
 */
public class CoordinateHelper
{
    /**
     * Get a random point along the perimeter of a square.
     *
     * @param pos1 Top-left point of square.
     * @param pos2 Bottom-right point of square.
     * @return Random coordinate
     */
    public static Point getCoordinateAlongPerimeter(Point pos1, Point pos2)
    {
        int x1 = pos1.getX();
        int x2 = pos2.getX();
        int y1 = pos1.getY();
        int y2 = pos2.getY();

        int horizonalLength = Math.abs(x1 - x2);
        int verticalLength = Math.abs(y1 - y2);

        int unfoldedLength = (horizonalLength * 2) + (verticalLength * 2);

        int randomPoint = ThreadLocalRandom.current().nextInt(1, unfoldedLength + 1);

        if (randomPoint <= horizonalLength) { // North side
            return new Point(x1 + randomPoint, y1);
        } else if (randomPoint <= horizonalLength + verticalLength) { // Right side
            return new Point(x2, y1 + randomPoint - horizonalLength);
        } else if (randomPoint <= horizonalLength + verticalLength + horizonalLength) { // Bottom side
            return new Point(x1 + randomPoint - horizonalLength - verticalLength, y2);
        } else if (randomPoint <= horizonalLength + verticalLength + horizonalLength + verticalLength) {
            return new Point(x1, y1 + randomPoint - horizonalLength - verticalLength - horizonalLength);
        } else {
            throw new RuntimeException("Found point outside of perimeter - this shouldn't ever happen, but maybe it did?");
        }
    }

    /**
     * Pick a random position around an origin, similar to getCoordinateAroundPerimeter, except it returns a
     * {@link PickedPosition}, and accepts a {@link IChecksPickedCoordinates} for validation and modification of picked positions.
     *
     * <b>If validation fails, there is no attempt to try again - and the method will simply return null.</b>
     *
     * @param world World
     * @param origin Origin point.
     * @param radius Radius around the origin point (works in a cube).
     * @param checksPickedCoordinates An optional handler for validating the picked position.
     * @return Picked position object.
     */
    public static PickedPosition pickPositionAroundPerimeterOneTry(World world, BlockPos origin, int radius, @Nullable IChecksPickedCoordinates checksPickedCoordinates)
    {
        Point spawn = CoordinateHelper.getCoordinateAroundPerimeter(new Point(origin.getX(), origin.getZ()), radius);
        BlockPos blockPos = new BlockPos(spawn.getX(), 0d, spawn.getY());

        // Picked position.
        PickedPosition pickedPosition = new PickedPosition();

        // Calculate possible spawn positions.
        int sideSize = (radius * 2) + 1;
        pickedPosition.possiblePositions = sideSize * 4;

        // Find top block to spawn mob.
        blockPos = world.getTopSolidOrLiquidBlock(blockPos);

        if (checksPickedCoordinates != null) {
            BlockPos modifiedPos = checksPickedCoordinates.checkPosition(blockPos);
            if (modifiedPos == null) {
                // Position deemed invalid.
                pickedPosition.isInvalid = true;
            } else if (!modifiedPos.equals(blockPos)) {
                // Position updated.
                pickedPosition.isModified = true;
                blockPos = modifiedPos;
            }
        }

        pickedPosition.position = blockPos;

        return pickedPosition;
    }

    /**
     * Pick a random position around an origin, similar to getCoordinateAroundPerimeter, except it returns a
     * {@link PickedPosition}, and accepts a {@link IChecksPickedCoordinates} for validation and modification of picked positions.
     *
     * <b>If validation fails, the method will generate another position an try that. If there is no suitable position, a {@link NoValidPositionsException} will be thrown.</b>
     *
     * @param world World
     * @param origin Origin point.
     * @param radius Radius around the origin point (works in a cube).
     * @param checksPickedCoordinates An optional handler for validating the picked position.
     * @return Picked position object.
     */
    public static BlockPos pickPositionAroundPerimeter(World world, BlockPos origin, int radius, @Nullable IChecksPickedCoordinates checksPickedCoordinates)
    {
        List<BlockPos> failedSpawnPositions = new ArrayList<BlockPos>();

        while (true) {
            // Pick a position.
            PickedPosition pickedPosition = pickPositionAroundPerimeterOneTry(world, origin, radius, checksPickedCoordinates);

            // Check that we still have positions to try.
            if (failedSpawnPositions.size() >= pickedPosition.possiblePositions)
                throw new NoValidPositionsException();

            if (pickedPosition.isInvalid) // Invalid spot. Add it to failed positions list.
                failedSpawnPositions.add(pickedPosition.position);
            else // Valid spot. Use it.
                return pickedPosition.position;
        }
    }

    /**
     * Get a coordinate around an origin point.
     *
     * @param origin Origin point.
     * @param radius Radius around the origin point (works in a cube).
     * @return
     */
    public static Point getCoordinateAroundPerimeter(Point origin, int radius)
    {
        Point point1 = new Point(origin.getX() - radius, origin.getY() - radius);
        Point point2 = new Point(origin.getX() + radius, origin.getY() + radius);

        return getCoordinateAlongPerimeter(point1, point2);
    }

    /**
     * Get a random coordinate within a cube drawn between 2 points in the world.
     *
     * @param start The smaller coordinate
     * @param end The larger coordinate
     * @return Random coordinate
     */
    public static BlockPos getCoordinateInside(BlockPos start, BlockPos end)
    {
        if (start.getX() > end.getX() ||
                start.getY() > end.getY() ||
                start.getZ() > end.getZ()) throw new IllegalArgumentException("Bottom right is larger than top left. Some coordinates may be the wrong way around?");

        return new BlockPos(
                ThreadLocalRandom.current().nextInt(start.getX(), end.getX()),
                ThreadLocalRandom.current().nextInt(start.getY(), end.getY()),
                ThreadLocalRandom.current().nextInt(start.getZ(), end.getZ())
        );
    }

    /**
     * Get a coordinate around an origin.
     * This point won't necessarily be around the perimeter (though it might be), it could be anywhere within the
     * radius.
     *
     * @param origin Origin point.
     * @param radius Radius around the origin point (works in a cube).
     * @return Random coordinate
     */
    public static BlockPos getCoordinateAround(BlockPos origin, int radius)
    {
        BlockPos start = new BlockPos(origin.getX() - radius, origin.getY() - radius, origin.getZ() - radius);
        BlockPos end = new BlockPos(origin.getX() + radius, origin.getY() + radius, origin.getZ() + radius);
        return getCoordinateInside(start, end);
    }

    public static BlockPos[] getCoordinatesInside(BlockPos start, BlockPos end)
    {
        if (start.getX() > end.getX() ||
                start.getY() > end.getY() ||
                start.getZ() > end.getZ()) throw new IllegalArgumentException("Bottom right is larger than top left. Some coordinates may be the wrong way around?");

        List<BlockPos> positions = new ArrayList<BlockPos>();

        for (int x = start.getX(); x <= end.getX(); x++) {
            for (int y = start.getY(); y <= end.getY(); y++) {
                for (int z = start.getZ(); z <= end.getZ(); z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }

        return Arrays.copyOf(positions.toArray(), positions.toArray().length, BlockPos[].class);
    }

    public static BlockPos[] getCoordinatesAround(BlockPos origin, int radius)
    {
        BlockPos start = new BlockPos(origin.getX() - radius, origin.getY() - radius, origin.getZ() - radius);
        BlockPos end = new BlockPos(origin.getX() + radius, origin.getY() + radius, origin.getZ() + radius);
        return getCoordinatesInside(start, end);
    }

    private static BlockScanResult[] scanForBlocks(World world, ICheckBlock checkBlock, BlockPos position, int radius, boolean immediateReturn)
    {
        int startX = position.getX() - radius;
        int startY = position.getY() - radius;
        int startZ = position.getZ() - radius;
        int endX = position.getX() + radius;
        int endY = position.getY() + radius;
        int endZ = position.getZ() + radius;

        List<BlockScanResult> results = new ArrayList<BlockScanResult>();

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                for (int z = startZ; z < endZ; z++) {
                    // Check block.
                    IBlockState blockState = world.getBlockState(new BlockPos(x, y, z));
                    if (checkBlock.checkBlock(blockState)) {
                        // Block match!
                        results.add(new BlockScanResult(blockState.getBlock(), blockState, new BlockPos(x, y, z)));

                        if (immediateReturn) {
                            // If no results then return null
                            if (results.size() == 0)
                                return new BlockScanResult[0];

                            BlockScanResult[] resultArray = new BlockScanResult[results.size()];
                            results.toArray(resultArray);
                            return resultArray;
                        }
                    }
                }
            }
        }

        BlockScanResult[] resultArray = new BlockScanResult[results.size()];
        results.toArray(resultArray);
        return resultArray;
    }

    private static BlockScanResult[] scanForBlocks(World world, final List<Block> blocks, BlockPos position, int radius, boolean immediateReturn)
    {
        return scanForBlocks(world, new ICheckBlock()
        {
            @Override
            public boolean checkBlock(IBlockState target)
            {
                return blocks.contains(target.getBlock());
            }
        }, position, radius, immediateReturn);
    }

    /**
     * Scan for blocks within an area around the player.
     * The scan is done in a cube, not sphere.
     *
     * @param world World
     * @param block Block to scan for
     * @param position Position of player to scan around.
     * @param size Radius of area to scan.
     * @return First matching block as a BlockScanResult.
     */
    public static BlockScanResult scanForBlock(World world, Block block, BlockPos position, int size)
    {
        BlockScanResult[] results = scanForBlocks(world, Collections.singletonList(block), position, size, true);
        if (results.length == 0)
            return null;
        return results[0];
    }

    /**
     * Scan for blocks within an area around the player.
     * The scan is done in a cube, not sphere.
     *
     * @param world World
     * @param block Block to scan for
     * @param position Position of player to scan around.
     * @param size Radius of area to scan.
     * @return An array of matching blocks within the area.
     */
    public static BlockScanResult[] scanForBlocks(World world, Block block, BlockPos position, int size)
    {
        return scanForBlocks(world, Collections.singletonList(block), position, size, false);
    }

    /**
     * Scan for several types of blocks within an area around the player.
     * The scan is done in a cube, not sphere.
     *
     * @param world World
     * @param blocks Blocks to scan for
     * @param position Position of player to scan around.
     * @param size Radius of area to scan.
     * @return An array of matching blocks within the area.
     */
    public static BlockScanResult[] scanForBlocks(World world, List<Block> blocks, BlockPos position, int size)
    {
        return scanForBlocks(world, blocks, position, size, false);
    }

    /**
     * Scan for several types of blocks within an area around the player.
     * The scan is done in a cube, not sphere.
     *
     * @param world World
     * @param blocks Blocks to scan for
     * @param position Position of player to scan around.
     * @param size Radius of area to scan.
     * @return First matching block as a BlockScanResult.
     */
    public static BlockScanResult scanForBlock(World world, List<Block> blocks, BlockPos position, int size)
    {
        BlockScanResult[] results = scanForBlocks(world, blocks, position, size, true);
        if (results.length == 0)
            return null;
        return results[0];
    }

    /**
     * Scan for several types of blocks within an area around the player.
     * The scan is done in a cube, not sphere.
     *
     * @param world World
     * @param blockCheck A block checker
     * @param position Position of player to scan around.
     * @param size Radius of area to scan.
     * @return An array of matching blocks within the area.
     */
    public static BlockScanResult[] scanForBlocks(World world, ICheckBlock blockCheck, BlockPos position, int size)
    {
        return scanForBlocks(world, blockCheck, position, size, false);
    }

    /**
     * Scan for several types of blocks within an area around the player.
     * The scan is done in a cube, not sphere.
     *
     * @param world World
     * @param blockCheck A block checker
     * @param position Position of player to scan around.
     * @param size Radius of area to scan.
     * @return First matching block as a BlockScanResult.
     */
    public static BlockScanResult scanForBlock(World world, ICheckBlock blockCheck, BlockPos position, int size)
    {
        BlockScanResult[] results = scanForBlocks(world, blockCheck, position, size, true);
        if (results.length == 0)
            return null;
        return results[0];
    }

    /**
     * Get all coordinates directly adjacent to block position.
     *
     * @param pos Origin
     * @return Array of adjacent block positions.
     */
    public static BlockPos[] getAdjacentCoordinates(BlockPos pos)
    {
        BlockPos[] coords = new BlockPos[6];
        coords[0] = new BlockPos(pos.getX(), pos.getY(), pos.getZ()).add(EnumFacing.UP.getDirectionVec());
        coords[1] = new BlockPos(pos.getX(), pos.getY(), pos.getZ()).add(EnumFacing.DOWN.getDirectionVec());
        coords[2] = new BlockPos(pos.getX(), pos.getY(), pos.getZ()).add(EnumFacing.NORTH.getDirectionVec());
        coords[3] = new BlockPos(pos.getX(), pos.getY(), pos.getZ()).add(EnumFacing.EAST.getDirectionVec());
        coords[4] = new BlockPos(pos.getX(), pos.getY(), pos.getZ()).add(EnumFacing.SOUTH.getDirectionVec());
        coords[5] = new BlockPos(pos.getX(), pos.getY(), pos.getZ()).add(EnumFacing.WEST.getDirectionVec());

        return coords;
    }

    /**
     * Check if a block is completely surrounded by blocks.
     *
     * @param world World
     * @param pos Position
     * @return Is surrounded?
     */
    public static boolean isBlockSurrounded(World world, BlockPos pos)
    {
        for (BlockPos checkPos : getAdjacentCoordinates(pos)) {
            if (world.isAirBlock(checkPos)) return false; // Found air block
        }
        return true;
    }

    /**
     * Clone a {@link BlockPos}.
     *
     * @param pos Source {@link BlockPos}
     * @return Cloned {@link BlockPos}
     */
    public static BlockPos cloneBlockPos(BlockPos pos)
    {
        return new BlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Find the size of a vein.
     *
     * @param world World
     * @param origin Origin position to scan vein. The block at this position will be the vein material.
     * @param maxSize Maximum size to scan.
     * @return Vein size. If hit the max, it'll return the max size.
     */
    public static int scanVein(World world, BlockPos origin, int maxSize)
    {
        IBlockState target = world.getBlockState(origin);

        // Count so far of target block found
        int count = 0;

        // List of blocks already checked
        List<BlockPos> checked = new ArrayList<BlockPos>();

        // List of block positions to scan next
        List<BlockPos> toCheck = new ArrayList<BlockPos>();
        toCheck.add(origin);

        while (toCheck.size() > 0) {
            // Pop next position off top of queue
            BlockPos pos = toCheck.get(0);
            toCheck.remove(0);

            checked.add(pos);

            // todo: do a better comparison here
            if (world.getBlockState(pos).getBlock() == target.getBlock()) {
                count++;

                // Check max size
                if (count >= maxSize) {
                    return count; // Reached max
                }

                // Add neighbouring blocks to queue
                for (BlockPos neighbourPos : getAdjacentCoordinates(pos)) {
                    if (!checked.contains(neighbourPos) && !toCheck.contains(neighbourPos))
                        toCheck.add(neighbourPos);
                }
            }
        }

        return count;
    }

    ///

    /**
     * Result from scanForBlocks.
     */
    public static class BlockScanResult
    {
        public BlockScanResult(Block block, IBlockState blockState, BlockPos blockPos) {
            this.block = block;
            this.blockState = blockState;
            this.position = blockPos;
        }

        public Block block;
        public IBlockState blockState;
        public BlockPos position;
    }

    /**
     * Implement this if your randomevent needs to make modifications or validate the spawn coordinates.
     */
    public interface IChecksPickedCoordinates
    {
        /**
         * Check position of proposed mob spawn location.
         * If accepted, return the position argument.
         * If modifications are wanted, return the modified {@link BlockPos}.
         * If the position is rejected and a new one should be generated, return null.
         *
         * @param position Proposed spawn position
         * @return Updated (or same) position. Null if invalid position.
         */
        @Nullable
        BlockPos checkPosition(BlockPos position);
    }

    /**
     * If no positions could be found that are suitable.
     */
    public static class NoValidPositionsException extends RuntimeException
    {
        public NoValidPositionsException() {
            super("No valid positions found!");
        }
    }

    /**
     * The picked position returned from pickPositionAroundPerimeterOneTry
     */
    private static class PickedPosition
    {
        public BlockPos position;
        public boolean isModified = false;
        public boolean isInvalid = false;
        public int possiblePositions;

        public PickedPosition(BlockPos position, boolean isModified, boolean isInvalid, int possiblePositions)
        {
            this.position = position;
            this.isModified = isModified;
            this.isInvalid = isInvalid;
            this.possiblePositions = possiblePositions;
        }

        public PickedPosition() {}
    }

    /**
     * Generic interface for class to check if a block is appropriate.
     */
    public interface ICheckBlock {
        boolean checkBlock(IBlockState blockState);
    }
}
