package net.mitchfizz05.randomevents;

import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mitchfizz05.randomevents.eventsystem.services.RandomEventServices;
import net.mitchfizz05.randomevents.proxy.CommonProxy;
import net.mitchfizz05.randomevents.util.WorldHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Random Events -- making Minecraft harder.
 */
@Mod(modid = RandomEvents.MOD_ID, version = RandomEvents.VERSION)
public class RandomEvents
{
    public static final String MOD_ID = "random_events";
    public static final String VERSION = "1.0";

    /**
     * Current mod instance
     */
    @Mod.Instance
    public static RandomEvents instance;
    /**
     * Random Events logger instance.
     */
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    /**
     * Random Events configuration.
     */
    public static Configuration config;
    /**
     * Event registry
     */
    public static final RandomEventRegistry randomEventRegistry = new RandomEventRegistry();

    /**
     * Keeps track of whether the world has loaded yet.
     */
    private boolean worldLoaded = false;

    @SidedProxy(clientSide = "net.mitchfizz05.randomevents.proxy.ClientProxy", serverSide = "net.mitchfizz05.randomevents.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // Initialise configuration file
        config = new Configuration(event.getSuggestedConfigurationFile());

        // Initialise WorldHelper
        WorldHelper.init();

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info(String.format("Random Events v%s by Mitchfizz05", VERSION));

        // Register ourselves on the event bus
        MinecraftForge.EVENT_BUS.register(this);

        // Init Random Event Services
        RandomEventServices.init();

        // Register events into registry
        randomEventRegistry.registerAllEvents();
        MinecraftForge.EVENT_BUS.post(new RandomEventsInitialised(randomEventRegistry));

        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandRandomEvents());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        //
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        //
    }

    /**
     * Triggered once all the Random Events have been initialised.
     */
    public class RandomEventsInitialised extends Event
    {
        private RandomEventRegistry randomEventRegistry;

        public RandomEventsInitialised(RandomEventRegistry randomEventRegistry)
        {
            this.randomEventRegistry = randomEventRegistry;
        }

        public RandomEventRegistry getRandomEventRegistry()
        {
            return randomEventRegistry;
        }
    }
}
