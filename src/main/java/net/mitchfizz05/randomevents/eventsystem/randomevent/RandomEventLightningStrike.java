package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CPlayerTimer;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nonnull;

/**
 * A bolt of lightning strikes the player. Only happens if outside and raining.
 */
public class RandomEventLightningStrike extends RandomEvent
{
    public RandomEventLightningStrike()
    {
        super("lightning_strike");

        addComponent(new CPlayerTimer(this, TimeHelper.hrsToTicks(1), TimeHelper.hrsToTicks(4)));
        addComponent(new CPlayerEvent());
    }

    @Override
    public void execute(@Nonnull World world, EntityPlayer player) throws ExecuteEventException
    {
        super.execute(world, player);

        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        // Check that can see sky
        if (!world.canSeeSky(player.getPosition()))
            throw new ExecuteEventException("Player cannot see the sky", this);

        // Check that weather is appropriate
        if (!world.getWorldInfo().isRaining())
            throw new ExecuteEventException("Not correct weather for a lightning strike", this);

        // Spawn lightning.
        EntityLightningBolt bolt = new EntityLightningBolt(world, 0d, 0d, 0d, false);
        bolt.setLocationAndAngles(x, y, z, 0f, 0f);
        world.addWeatherEffect(bolt);
    }
}
