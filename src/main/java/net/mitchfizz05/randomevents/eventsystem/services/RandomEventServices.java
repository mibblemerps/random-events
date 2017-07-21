package net.mitchfizz05.randomevents.eventsystem.services;

import net.mitchfizz05.randomevents.RandomEvents;

public class RandomEventServices
{
    public static WorldTimerService worldTimerService;
    public static ExecuteEventService executeEventService;
    public static PlayerTimerService playerTimerService;
    public static NbtService nbtService;
    public static AnnouncerService announcerService;
    public static ConfigService configService;
    public static LongEventService longEventService;
    public static EventTimerMultiplierService eventTimerMultiplierService;


    // ---

    public static void init()
    {
        worldTimerService = new WorldTimerService();
        executeEventService = new ExecuteEventService();
        playerTimerService = new PlayerTimerService();
        nbtService = new NbtService();
        announcerService = new AnnouncerService();
        configService = new ConfigService(RandomEvents.config);
        longEventService = new LongEventService();
        eventTimerMultiplierService = new EventTimerMultiplierService();
    }
}
