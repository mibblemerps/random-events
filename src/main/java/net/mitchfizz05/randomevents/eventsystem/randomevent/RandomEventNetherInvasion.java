package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.GameData;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.EventDifficulty;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CDifficulty;
import net.mitchfizz05.randomevents.eventsystem.component.CRandomPlayer;
import net.mitchfizz05.randomevents.eventsystem.component.CWorldTimer;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.MobSpawner;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEventNetherInvasion extends RandomEvent implements MobSpawner.IMobSpawnEvent
{
    private ArrayList<Block> replaceableBlocks = new ArrayList<>();

    private int minSpawnDistance = 24;
    private int maxSpawnDistance = 36;

    private boolean enableExplosion = true;

    public RandomEventNetherInvasion()
    {
        super("nether_invasion");

        ((CDifficulty) getComponent(CDifficulty.class)).difficulty = EventDifficulty.VERY_BAD;

        addComponent(new CWorldTimer(this, TimeHelper.hrsToSecs(1), TimeHelper.hrsToSecs(3)));
        addComponent(new CRandomPlayer());

        replaceableBlocks.add(Blocks.DIRT);
        replaceableBlocks.add(Blocks.GRASS);
        replaceableBlocks.add(Blocks.SAND);
        replaceableBlocks.add(Blocks.STONE);
        replaceableBlocks.add(Blocks.SAND);
        replaceableBlocks.add(Blocks.GRASS_PATH);

        // Load config
        minSpawnDistance = RandomEvents.config.get(getConfigName(), "minimum_spawn_distance", minSpawnDistance,
                "Minimum distance away a nether invasion will spawn.").getInt();
        maxSpawnDistance = RandomEvents.config.get(getConfigName(), "maximum_spawn_distance", maxSpawnDistance,
                "Maximum distance away a nether invasion will spawn.").getInt();
        enableExplosion = RandomEvents.config.get(getConfigName(), "enable_explosion", enableExplosion,
                "Enable the small explosion when the portal spawns.").getBoolean();
    }

    @Override
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        if (player.dimension != 0) {
            throw new ExecuteEventException("Player must be in overworld!", this, true);
        }

        if (world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            throw new ExecuteEventException("World must not be in peaceful mode!", this, true);
        }

        // Pick location for portal
        int radius = ThreadLocalRandom.current().nextInt(minSpawnDistance, maxSpawnDistance);
        BlockPos portalPos = CoordinateHelper.pickPositionAroundPerimeter(world, player.getPosition(), radius, null);

        if (enableExplosion) {
            // Make an explosion
            world.createExplosion(null, portalPos.getX() + 2, portalPos.getY() + 2, portalPos.getZ(), 1f, true);
        }

        // Spawn portal (1 block into the ground so bottom of portal is flush with ground)
        spawnPortal(world, portalPos.add(0, -1, 0));

        // Spawn zombie pigmen
        MobSpawner.execute(this, new MobSpawner.MobSpawnEventParameters(6, 10, 5, portalPos), world, player);

        // Set all adjacent blocks on fire.
        for (BlockPos pos : CoordinateHelper.getCoordinatesAround(portalPos, 5)) {
            if (world.isAirBlock(pos) && Blocks.FIRE.canPlaceBlockAt(world, pos)) {
                // Set fire
                if (ThreadLocalRandom.current().nextFloat() < 0.4f) {
                    world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                }
            } else if (replaceableBlocks.contains(world.getBlockState(pos).getBlock())) {
                // Maybe replace block with nether rack or lava
                if (ThreadLocalRandom.current().nextFloat() < 0.5f) {
                    world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
                } else if (ThreadLocalRandom.current().nextFloat() < 0.33f) {
                    world.setBlockState(pos, Blocks.SOUL_SAND.getDefaultState());
                } else if (ThreadLocalRandom.current().nextFloat() < 0.33f) {
                    world.setBlockState(pos, Blocks.LAVA.getDefaultState());
                }
            }
        }
    }

    private void spawnPortal(World world, BlockPos pos)
    {
        IBlockState frame = Blocks.OBSIDIAN.getDefaultState();
        IBlockState corners = Blocks.OBSIDIAN.getDefaultState();

        BlockPos currentPos = new BlockPos(pos);

        // Build sides of portal
        for (int i = 0; i < 2; i++) {
            world.setBlockState(currentPos, corners);
            currentPos = currentPos.add(0, 1, 0);
            for (int y = 0; y < 3; y++) {
                world.setBlockState(currentPos, frame);
                currentPos = currentPos.add(0, 1, 0);
            }
            world.setBlockState(currentPos, corners);

            // Go to the right bottom to build next side of portal
            currentPos = currentPos.add(3, -4, 0);
        }

        // Reset position to build bottom part of frame
        currentPos = pos.add(1, 0, 0);

        for (int i = 0; i < 2; i++) {
            world.setBlockState(currentPos, frame);
            currentPos = currentPos.add(1, 0, 0);
            world.setBlockState(currentPos, frame);

            currentPos = currentPos.add(-1, 4, 0);
        }

        // Light portal
        Blocks.PORTAL.trySpawnPortal(world, pos.add(1, 1, 0));
    }

    @Override
    public Entity getEntity(World world, EntityPlayer player)
    {
        EntityPigZombie pigman = new EntityPigZombie(world);
        pigman.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20, 5));
        pigman.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 60, 0));

        pigman.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD, 1));

        // Do several zero damage attacks to the pigman from the player, to anger them
        for (int i = 0; i < 4; i++) {
            pigman.attackEntityFrom(new EntityDamageSource("anger", player), 0f);
        }

        return pigman;
    }
}
