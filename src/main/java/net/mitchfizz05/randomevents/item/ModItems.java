package net.mitchfizz05.randomevents.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.mitchfizz05.randomevents.RandomEvents;

public class ModItems
{
    public static ItemMedicalPack medicalPack;
    public static ItemMysteriousBerry mysteriousBerry;

    public static void preInit()
    {
        medicalPack = new ItemMedicalPack();
        mysteriousBerry = new ItemMysteriousBerry();

        registerItems();
    }

    public static void registerItems()
    {
        GameRegistry.register(medicalPack);
        GameRegistry.register(mysteriousBerry);
    }

    public static void registerRenders()
    {
        RandomEvents.logger.info("Registering RandomEvents item renders...");

        // register renderers..
        registerRender(medicalPack);
        registerRender(mysteriousBerry);
    }

    public static void registerRender(Item item)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(item, 0, new ModelResourceLocation(RandomEvents.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }
}
