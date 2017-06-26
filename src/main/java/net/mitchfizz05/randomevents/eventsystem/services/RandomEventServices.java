package net.mitchfizz05.randomevents.eventsystem.services;

public class RandomEventServices
{
    public static WorldTimerService worldTimerService;
    public static ExecuteEventService executeEventService;
    public static PlayerTimerService playerTimerService;
    public static NbtService nbtService;
    public static AnnouncerService announcerService;


    // ---

    public static void init()
    {
        worldTimerService = new WorldTimerService();
        executeEventService = new ExecuteEventService();
        playerTimerService = new PlayerTimerService();
        nbtService = new NbtService();
        announcerService = new AnnouncerService();
    }
}
