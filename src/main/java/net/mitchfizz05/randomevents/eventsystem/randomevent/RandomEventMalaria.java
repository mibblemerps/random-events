package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.statuseffect.REStatusEffects;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;

/**
 * Gives the player malaria.
 */
public class RandomEventMalaria extends RandomEvent
{
    public RandomEventMalaria()
    {
        super("malaria");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToTicks(2), TimeHelper.hrsToTicks(3)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(@Nonnull World world, EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        if (player.isPotionActive(REStatusEffects.malaria)) throw new ExecuteEventException("Already has malaria", this);

        player.addPotionEffect(new PotionEffect(REStatusEffects.malaria, TimeHelper.minsToTicks(8), 0, false, false));
    }
}
