package net.mitchfizz05.randomevents.eventsystem.services;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mitchfizz05.randomevents.eventsystem.component.CAnnounceable;
import net.mitchfizz05.randomevents.eventsystem.component.CDifficulty;
import net.mitchfizz05.randomevents.eventsystem.event.RandomEventTriggerEvent;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;

import java.util.List;

/**
 * Announces events.
 *
 * Listens on the event bus for when a random event triggers and sends the announcement to the relevant players.
 */
public class AnnouncerService
{
    public AnnouncerService()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Get the standard translation key for event announcements.
     *
     * @param event Event to get translation key for
     * @return Translation key
     */
    public static String getStandardTranslationKey(RandomEvent event)
    {
        return "randomevent." + event.getName();
    }

    private ITextComponent generateAnnouncement(CAnnounceable announceable, CDifficulty difficulty)
    {
        ITextComponent announcement = new TextComponentString("");

        // Set an appropriate text colour for this event's difficulty.
        Style eventTextStyle = new Style();
        if (difficulty != null)
            eventTextStyle.setColor(difficulty.difficulty.getColor());

        // Construct message
        announcement.appendSibling(new TextComponentString("[Event] ").setStyle(new Style().setColor(TextFormatting.DARK_AQUA).setBold(true)));
        announcement.appendSibling(new TextComponentTranslation("randomevent.blight").setStyle(eventTextStyle));

        return announcement;
    }

    @SubscribeEvent
    public void onRandomEventTrigger(RandomEventTriggerEvent event)
    {
        // Announce the event
        if (event.player == null) {
            // No player available, must be a global event.
            RandomEventServices.announcerService.announce(event.randomEvent, event.world.getMinecraftServer());
        } else {
            // Player *is* available, must be a player specific event.
            RandomEventServices.announcerService.announce(event.randomEvent, (EntityPlayerMP) event.player);
        }
    }

    /**
     * Announce an event to the world!
     *
     * If the event given doesn't have a {@link CAnnounceable} component, it will silently not be announced.
     * It is recommended the event to have a {@link CDifficulty} component to color the message appropriately.
     *
     * @param event Random event to announce.
     * @param server Server to broadcast message to.
     */
    public void announce(RandomEvent event, MinecraftServer server)
    {
        announce(event, server.getPlayerList().getPlayers());
    }

    /**
     * Announce an event to a list of players.
     *
     * If the event given doesn't have a {@link CAnnounceable} component, it will silently not be announced.
     * It is recommended the event to have a {@link CDifficulty} component to color the message appropriately.
     *
     * @param event Random event to announce.
     * @param players List of players to announce the event to.
     */
    public void announce(RandomEvent event, List<EntityPlayerMP> players)
    {
        for (EntityPlayerMP player : players) {
            announce(event, player);
        }
    }

    /**
     * Announce an event to an individual player.
     *
     * If the event given doesn't have a {@link CAnnounceable} component, it will silently not be announced.
     * It is recommended the event to have a {@link CDifficulty} component to color the message appropriately.
     *
     * @param event Random event to announce.
     * @param player Player to announce event to.
     */
    public void announce(RandomEvent event, EntityPlayerMP player)
    {
        // Check if event is announceable.
        if (!event.hasComponent(CAnnounceable.class))
            return;

        CAnnounceable announceable = (CAnnounceable) event.getComponent(CAnnounceable.class);
        CDifficulty difficulty = (CDifficulty) event.getComponent(CDifficulty.class);

        player.sendMessage(generateAnnouncement(announceable, difficulty));
    }
}
