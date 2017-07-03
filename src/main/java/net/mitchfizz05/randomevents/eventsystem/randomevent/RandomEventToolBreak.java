package net.mitchfizz05.randomevents.eventsystem.randomevent;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.services.RandomEventServices;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * The player's tool randomly breaks.
 */
public class RandomEventToolBreak extends RandomEvent
{
    public ArrayList<Class> breakableItems = new ArrayList<Class>();
    public ArrayList<Class> stickDropItems = new ArrayList<Class>();

    public RandomEventToolBreak()
    {
        super("tool_break");

        addComponent(new CPlayerEvent());

        // Items that are breakable
        breakableItems.add(ItemTool.class);
        breakableItems.add(ItemHoe.class);
        breakableItems.add(ItemShears.class);
        breakableItems.add(ItemSword.class);

        // Items that will drop sticks upon
        stickDropItems.add(ItemTool.class);
        stickDropItems.add(ItemHoe.class);
        stickDropItems.add(ItemSword.class);

        // Register on event bus to allow receiving BreakBlock event.
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void execute(@Nonnull World world, EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem.isEmpty()) throw new ExecuteEventException("Not holding anything", this);

        // Check if the item is whitelisted to be subject to this event.
        if (!isBreakableTool(currentItem.getItem())) throw new ExecuteEventException("Not a whitelisted breakable tool", this);

        // Check if item is a stick drop item
        ItemStack stickDrop = null;
        if (doesToolDropStick(currentItem.getItem())) {
            stickDrop = new ItemStack(Items.STICK, ThreadLocalRandom.current().nextInt(1, 2 + 1));
        }

        // Break tool
        player.renderBrokenItemStack(currentItem); // TODO: work out why this doesn't work
        currentItem.shrink(1);

        if (stickDrop != null) {
            // Drop stick.
            player.dropItem(stickDrop, true);
        }
    }

    /**
     * Is this a tool that can break via this event?
     *
     * @param item Item
     * @return Can break?
     */
    public boolean isBreakableTool(Item item)
    {
        boolean isBreakable = false;
        for (Class breakableItem : breakableItems) {
            if (breakableItem.isInstance(item)) {
                isBreakable = true;
            }
        }
        return isBreakable;
    }

    /**
     * Get the material of a tool.
     * This is surprisingly difficult with all different classes using different systems of storing tool material.
     *
     * @param item Item to check
     * @return Tool material
     */
    public Item.ToolMaterial getToolMaterial(Item item)
    {
        if (item instanceof ItemTool) {
            return ((ItemTool)item).getToolMaterial();
        } else if (item instanceof ItemSword) {
            String materialName = ((ItemSword)item).getToolMaterialName();
            return Item.ToolMaterial.valueOf(materialName);
        } else if (item instanceof ItemHoe) {
            String materialName = ((ItemHoe)item).getMaterialName();
            return Item.ToolMaterial.valueOf(materialName);
        } else if (item instanceof ItemShears) {
            return Item.ToolMaterial.IRON;
        } else {
            return null;
        }
    }

    /**
     * Get the random chance of a tool breaking.
     *
     * @param itemstack Tool itemstack.
     * @return Chance between 0 and 1.
     */
    public float getBreakableChance(ItemStack itemstack)
    {
        if (!isBreakableTool(itemstack.getItem()))
            return 0f; // Not even a breakable tool.

        Item.ToolMaterial material = getToolMaterial(itemstack.getItem());

        if (material == Item.ToolMaterial.GOLD)
            return 0f;
        else {
            return 0.9f / itemstack.getMaxDamage();
        }
    }

    /**
     * Does this tool drop a stick when it breaks?
     * Generally this means the tool has sticks in the recipe.
     *
     * @param item Item
     * @return Does it drop sticks?
     */
    public boolean doesToolDropStick(Item item)
    {
        boolean isStickDropItem = false;
        for (Class stickDropItem : stickDropItems) {
            if (stickDropItem.isInstance(item)) {
                isStickDropItem = true;
            }
        }
        return isStickDropItem;
    }

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event)
    {
        if (ThreadLocalRandom.current().nextFloat() < getBreakableChance(event.getPlayer().inventory.getCurrentItem())) {
            // Trigger event.
            try {
                RandomEventServices.executeEventService.executeEvent(this, event.getWorld(), event.getPlayer());
            } catch (ExecuteEventException ignored) {}
        }
    }
}
