package net.mitchfizz05.randomevents.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.mitchfizz05.randomevents.RandomEvents;

public class ModItems
{
    public static ItemMedicalPack medicalPack;
    public static ItemMysteriousBerry mysteriousBerry;
    public static ItemDreamSword dreamSword;

    public static void preInit()
    {
        MinecraftForge.EVENT_BUS.register(new ModItems());

        medicalPack = new ItemMedicalPack();
        mysteriousBerry = new ItemMysteriousBerry();
        dreamSword = new ItemDreamSword();
    }

    public static void registerRenders()
    {
        RandomEvents.logger.info("Registering RandomEvents item renders...");

        // register renderers..
        registerRender(medicalPack);
        registerRender(mysteriousBerry);
        registerRender(dreamSword);
    }

    public static void registerRender(Item item)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(item, 0, new ModelResourceLocation(RandomEvents.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }

    private ModItems()
    {
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(medicalPack);
        event.getRegistry().register(mysteriousBerry);
        event.getRegistry().register(dreamSword);
    }
}
