package net.mitchfizz05.randomevents.mechanics;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.statuseffect.ITreatableWithMedicine;

public class UnlockMedicine
{
    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(new UnlockMedicine());
    }

    @SubscribeEvent
    public void OnPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == Side.CLIENT)
            return;

        if (event.player.ticksExisted % 20 != 0)
            return;

        for (Potion potion : event.player.getActivePotionMap().keySet()){
            if (potion instanceof ITreatableWithMedicine) {
                event.player.unlockRecipes(new ResourceLocation[] {new ResourceLocation(RandomEvents.MOD_ID, "medical_pack")});
            }
        }
    }
}
