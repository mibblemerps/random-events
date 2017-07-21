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
 * Gives the player the plague.
 */
public class RandomEventPlague extends RandomEvent
{
    public RandomEventPlague()
    {
        super("plague");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToSecs(2), TimeHelper.hrsToSecs(3)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(@Nonnull World world, EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        if (player.isPotionActive(REStatusEffects.plague)) throw new ExecuteEventException("Already has plague", this);

        player.addPotionEffect(new PotionEffect(REStatusEffects.plague, REStatusEffects.plague.defaultDuration, 0, false, false));
    }
}
