package net.mitchfizz05.randomevents;

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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.mitchfizz05.randomevents.command.CommandRandomEvents;
import net.mitchfizz05.randomevents.command.CommandRoomHelper;
import net.mitchfizz05.randomevents.world.biomes.Biomes;
import net.mitchfizz05.randomevents.block.REBlocks;
import net.mitchfizz05.randomevents.content.RELootTables;
import net.mitchfizz05.randomevents.world.dimension.Dimensions;
import net.mitchfizz05.randomevents.eventsystem.services.RandomEventServices;
import net.mitchfizz05.randomevents.item.ModItems;
import net.mitchfizz05.randomevents.mechanics.RandomFoodPoisoning;
import net.mitchfizz05.randomevents.proxy.CommonProxy;
import net.mitchfizz05.randomevents.statuseffect.REStatusEffects;
import net.mitchfizz05.randomevents.util.WorldHelper;
import net.mitchfizz05.randomevents.world.structures.WorldGenDreamRealmStructures;
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

    public static RELootTables lootTables;

    /**
     * Keeps track of whether the world has loaded yet.
     */
    private boolean worldLoaded = false;

    public static WeatherHelper weatherHelper = new WeatherHelper();

    @SidedProxy(clientSide = "net.mitchfizz05.randomevents.proxy.ClientProxy", serverSide = "net.mitchfizz05.randomevents.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // Initialise configuration file
        config = new Configuration(event.getSuggestedConfigurationFile());

        // Initialise mod items
        ModItems.preInit();

        // Initialise WorldHelper
        WorldHelper.init();

        Biomes.register();
        Dimensions.regsiter();
        GameRegistry.registerWorldGenerator(new WorldGenDreamRealmStructures(), 0);

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

        // Register game content
        REBlocks.register();
        REStatusEffects.register();
        lootTables = new RELootTables();

        // Register events into registry
        randomEventRegistry.registerAllEvents();
        MinecraftForge.EVENT_BUS.post(new RandomEventsInitialised(randomEventRegistry));

        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);

        RandomFoodPoisoning.postInit(event);
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandRandomEvents());
        event.registerServerCommand(new CommandRoomHelper());
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
