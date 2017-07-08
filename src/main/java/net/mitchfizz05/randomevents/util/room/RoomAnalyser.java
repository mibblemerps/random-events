package net.mitchfizz05.randomevents.util.room;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.util.CoordinateHelper;

import javax.annotation.Nonnull;

/**
 * Analyses rooms and their characteristics.
 */
public class RoomAnalyser
{
    /**
     * Maximum size of a room. 1 = 1 block.
     */
    public static int maxRoomSize = 1000;

    /**
     * Maximum height (Y axis) of a room.
     */
    public static int maxHeight = 5;
    /**
     * Maximum width <em>or</em> depth of a room (X and Z axes).
     */
    public static int maxWidthDepth = 10;

    /**
     * Analyse a room and it's characteristics.
     *
     * @param world The world
     * @param origin Position to start analysing room
     * @param maxSize Maximum size of the room. This will override <code>RoomAnalyser.maxRoomSize</code>.
     * @return A room object
     */
    public static Room analyseRoom(@Nonnull World world, @Nonnull BlockPos origin, int maxSize)
    {
        Room room = new Room();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(origin.getX(), origin.getY(), origin.getZ());

        BlockPos startPos = CoordinateHelper.cloneBlockPos(pos);
        BlockPos endPos = CoordinateHelper.cloneBlockPos(pos);

        for (int x = origin.getX(); x < origin.getX() + maxWidthDepth; x++) {
            pos.setPos(x, pos.getY(), pos.getZ());
            if (!isRoomInterior(world.getBlockState(pos)))
                break;

            for (int y = origin.getY(); y < origin.getY() + maxHeight; y++) {
                pos.setPos(pos.getX(), y, pos.getZ());
                if (!isRoomInterior(world.getBlockState(pos)))
                    break;

                for (int z = origin.getZ(); z < origin.getZ() + maxWidthDepth; z++) {
                    pos.setPos(pos.getX(), pos.getY(), z);
                    if (!isRoomInterior(world.getBlockState(pos)))
                        break;

                    room.size++;
                    if ((pos.getX() >= endPos.getX()) &&
                            (pos.getY() >= endPos.getY()) &&
                            (pos.getZ() >= endPos.getZ()))
                        endPos = pos;
                }
            }
        }

        pos = new BlockPos.MutableBlockPos(origin.getX(), origin.getY(), origin.getZ());

        for (int x = origin.getX(); x > origin.getX() - maxWidthDepth; x--) {
            pos.setPos(x, pos.getY(), pos.getZ());
            if (!isRoomInterior(world.getBlockState(pos)))
                break;

            for (int y = origin.getY(); y > origin.getY() - maxHeight; y--) {
                pos.setPos(pos.getX(), y, pos.getZ());
                if (!isRoomInterior(world.getBlockState(pos)))
                    break;

                for (int z = origin.getZ(); z > origin.getZ() - maxWidthDepth; z--) {
                    pos.setPos(pos.getX(), pos.getY(), z);
                    if (!isRoomInterior(world.getBlockState(pos)))
                        break;

                    room.size++;

                    if ((pos.getX() <= startPos.getX()) &&
                            (pos.getY() <= startPos.getY()) &&
                            (pos.getZ() <= startPos.getZ()))
                        startPos = pos;
                }
            }
        }

        room.width = endPos.getX() - startPos.getX();
        room.height = endPos.getY() - startPos.getY();
        room.depth = endPos.getZ() - startPos.getZ();

        RandomEvents.logger.info("Start pos: " + startPos.toString());
        RandomEvents.logger.info("End pos: " + endPos.toString());

        return room;
    }

    /**
     * Analyse a room and it's characteristics.
     * Max size is specified in <code>RoomAnalyser.maxRoomSize</code>.
     *
     * @param world The world
     * @param origin Position to start analysing room
     * @return A room object
     */
    public static Room analyseRoom(@Nonnull World world, @Nonnull BlockPos origin)
    {
        return analyseRoom(world, origin, maxRoomSize);
    }

    // ---

    protected static boolean isRoomInterior(IBlockState blockState)
    {
        return blockState.getMaterial() == Material.AIR;
    }
}
