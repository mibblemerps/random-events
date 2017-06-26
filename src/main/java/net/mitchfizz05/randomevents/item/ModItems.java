package net.mitchfizz05.randomevents.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.mitchfizz05.randomevents.RandomEvents;

public class ModItems
{
    // items..

    public static void preInit()
    {
        // initialise items..

        registerItems();
    }

    public static void registerItems()
    {
        // register items..
        // GameRegistry.register();
    }

    public static void registerRenders()
    {
        RandomEvents.logger.info("Registering RandomEvents item renders...");

        // register renderers..
        //registerRender();
    }

    public static void registerRender(Item item)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(item, 0, new ModelResourceLocation(RandomEvents.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }
}
