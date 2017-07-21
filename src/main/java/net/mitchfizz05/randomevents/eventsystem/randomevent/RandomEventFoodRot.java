package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.IUsesConfig;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.CoordinateHelper;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Food in nearby chests rots to rotten flesh.
 */
public class RandomEventFoodRot extends RandomEvent
{
    /**
     * Chance (0-1) of a stack of food rotting.
     */
    protected double rotChance = 0.8;

    public RandomEventFoodRot()
    {
        super("food_rot");

        rotChance = RandomEvents.config.get(getConfigName(), "rot_chance", rotChance, "Chance of a stack of food in an inventory rotting. 0-1", 0, 0).getDouble();

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(2), TimeHelper.hrsToSecs(3)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(@Nonnull World world, EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        int foodRotted = 0;

        // Loop through all nearby blocks
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        for (BlockPos pos : CoordinateHelper.getCoordinatesAround(player.getPosition(), 64)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null)
                continue;

            // Block is inventory, rot the contents
            if (tileEntity instanceof IInventory) {
                // Rot food
                foodRotted = foodRotted + rotInventory((IInventory) tileEntity, rotChance);
            }
        }

        if (foodRotted == 0)
            throw new ExecuteEventException("No food to rot (or maybe was lucky)", this);
    }

    /**
     * Rot (some of) the food in an inventory.
     *
     * @param inventory Inventory to rot
     * @param rotChance Chance (calculated per stack) of rotting. (0-1)
     * @return Number of items rotted
     */
    public static int rotInventory(IInventory inventory, double rotChance)
    {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int foodRotted = 0;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack.getItem() instanceof ItemFood) {
                ItemFood food = (ItemFood) stack.getItem();

                // Don't rot rotten flesh.
                if (food.getRegistryName() == null || food.getRegistryName().toString().equals("minecraft:rotten_flesh"))
                    continue;

                // Don't rot food that has no food value.
                if (food.getHealAmount(stack) <= 0)
                    continue;

                if (random.nextFloat() < rotChance) {
                    // Rot food
                    int newStackSize = random.nextInt((int)Math.floor(stack.getCount() / 2), stack.getCount() + 1);
                    inventory.setInventorySlotContents(i, new ItemStack(Items.ROTTEN_FLESH, newStackSize));
                    foodRotted = foodRotted + stack.getCount();
                }
            }
        }

        return foodRotted;
    }
}
