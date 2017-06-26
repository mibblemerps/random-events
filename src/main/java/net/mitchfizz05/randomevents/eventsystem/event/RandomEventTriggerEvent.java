package net.mitchfizz05.randomevents.eventsystem.event;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;

public class RandomEventTriggerEvent extends Event
{
    public RandomEvent randomEvent;
    public World world;
    public EntityPlayer player;

    public RandomEventTriggerEvent(RandomEvent randomEvent, World world, EntityPlayer player)
    {
        this.randomEvent = randomEvent;
        this.world = world;
        this.player = player;
    }
}
