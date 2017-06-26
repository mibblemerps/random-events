package net.mitchfizz05.randomevents.util;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldHelper
{
    private static boolean initialised = false;
    private static boolean worldLoaded = false;

    /**
     * Only to be instantiated by itself. This should only be instantiated to register itself on the event bus via init().
     */
    private WorldHelper()
    {
    }

    /**
     * Is the provided world the overworld?
     *
     * @param world World
     * @return Is overworld?
     */
    public static boolean isOverworld(World world)
    {
        return (DimensionManager.getWorld(0) == world);
    }

    /**
     * Has the world been loaded yet?
     * This becomes true once {@link WorldEvent.Load} has been triggered, and becomes false once
     * {@link WorldEvent.Unload} has been triggered.
     */
    public static boolean isWorldLoaded()
    {
        return worldLoaded;
    }

    // ---

    /**
     * Initialise the {@link WorldHelper}.
     * Some methods may not work if the helper hasn't been initialised yet.
     */
    public static void init()
    {
        if (initialised)
            return;

        MinecraftForge.EVENT_BUS.register(new WorldHelper());

        initialised = true;
    }

    // ---

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (isOverworld(event.getWorld()))
            worldLoaded = true;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        if (isOverworld(event.getWorld()))
            worldLoaded = false;
    }
}
