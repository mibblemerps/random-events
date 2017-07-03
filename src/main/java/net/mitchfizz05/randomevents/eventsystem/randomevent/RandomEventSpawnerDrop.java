package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.eventsystem.EventDifficulty;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CDifficulty;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.MobSpawner;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A bunch of creeper spawners drop around the player.
 */
public class RandomEventSpawnerDrop extends RandomEvent implements MobSpawner.IMobSpawnEvent, CoordinateHelper.IChecksPickedCoordinates
{
    public RandomEventSpawnerDrop()
    {
        super("spawner_drop");

        ((CDifficulty) getComponent(CDifficulty.class)).difficulty = EventDifficulty.VERY_BAD;

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToTicks(2), TimeHelper.hrsToTicks(3)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        if (world.getWorldTime() < 12575 || world.getWorldTime() > 23031) throw new ExecuteEventException("Not night", this);

        MobSpawner.execute(this, new MobSpawner.MobSpawnEventParameters(2, 4, 17), world, player);
    }

    @Override
    public Entity getEntity(World world, EntityPlayer player)
    {
        IBlockState fallState = Blocks.MOB_SPAWNER.getDefaultState();


        EntityFallingBlock fallingBlock = new EntityFallingBlock(world, 0, 0, 0, fallState);
        fallingBlock.fallTime = 1;

        NBTTagCompound spawnerNbt = new NBTTagCompound();

        // Entity tag to be spawned.
        NBTTagCompound entityId = new NBTTagCompound();
        entityId.setString("id", "minecraft:creeper");

        // Entity potential entry.
        NBTTagCompound entityPotential = new NBTTagCompound();
        entityPotential.setTag("Entity", entityId);
        entityPotential.setInteger("Weight", 1);

        // Entity spawn potentials list. Only has one for this.
        NBTTagList spawnPotentials = new NBTTagList();
        spawnPotentials.appendTag(entityPotential);

        // Mob the spawner is currently spawning.
        NBTTagCompound spawnData = new NBTTagCompound();
        spawnData.setString("id", entityId.getString("id"));

        // Put everything into spawner's NBT.
        spawnerNbt.setTag("SpawnData", spawnData);
        spawnerNbt.setTag("SpawnPotentials", spawnPotentials);

        // Additional properties.
        spawnerNbt.setShort("RequiredPlayerRange", (short) 28); // 28 blocks required range
        spawnerNbt.setShort("MinSpawnDelay", (short)120); // 6 seconds (min)
        spawnerNbt.setShort("MaxSpawnDelay", (short)420); // 21 seconds (max)

        fallingBlock.tileEntityData = spawnerNbt;

        return fallingBlock;
    }

    @Nullable
    @Override
    public BlockPos checkPosition(BlockPos position)
    {
        position = new BlockPos(position.getX(), 128, position.getZ());
        return position;
    }
}
