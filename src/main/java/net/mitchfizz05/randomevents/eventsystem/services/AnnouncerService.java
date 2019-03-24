package net.mitchfizz05.randomevents.eventsystem.services;

import net.minecraft.entity.player.EntityPlayer;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
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

    /**
     * Generate an announcement.
     *
     * @param announceable Announceable event
     * @param difficulty Event difficulty. This affects the colour of the announcement
     * @param targetPlayer Target player. If you do not want the target player included in the generated announcement,
     *                     this may be null.
     * @return Announcement ready to be sent to the relevant players
     */
    private ITextComponent generateAnnouncement(CAnnounceable announceable, CDifficulty difficulty, @Nullable EntityPlayer targetPlayer)
    {
        ITextComponent announcement = new TextComponentString("");

        // Set an appropriate text colour for this event's difficulty.
        Style eventTextStyle = new Style();
        if (difficulty != null)
            eventTextStyle.setColor(difficulty.difficulty.getColor());

        // Construct message
        announcement.appendSibling(new TextComponentString("[Event] ").setStyle(new Style().setColor(TextFormatting.DARK_AQUA).setBold(true)));
        if (targetPlayer != null)
            announcement.appendSibling(new TextComponentString("[" + targetPlayer.getName() + "] ").setStyle(new Style().setColor(TextFormatting.AQUA).setBold(true)));
        announcement.appendSibling(new TextComponentTranslation(announceable.translationKey).setStyle(eventTextStyle));

        return announcement;
    }

    @SubscribeEvent
    public void onRandomEventTrigger(RandomEventTriggerEvent event)
    {
        MinecraftServer server = event.world.getMinecraftServer();

        // Announce the event
        if (event.player == null) {
            // No player available, must be a global event.
            RandomEventServices.announcerService.announce(event.randomEvent, server);
        } else {
            // Player *is* available, must be a player specific event.
            List<EntityPlayerMP> cc = server.getPlayerList().getPlayers();
            RandomEventServices.announcerService.announce(event.randomEvent, event.player, cc);
        }
    }

    /**
     * Announce an event to the world!<br>
     * <br>
     * If the event given doesn't have a {@link CAnnounceable} component, it will silently not be announced.<br>
     * It is recommended the event to have a {@link CDifficulty} component to color the message appropriately.
     *
     * @param event Random event to announce.
     * @param server Server to broadcast message to.
     */
    public void announce(RandomEvent event, MinecraftServer server)
    {
        announce(event, server.getPlayerList().getPlayers(), new ArrayList<EntityPlayer>(0));
    }

    /**
     * Announce an event to a list of players.<br>
     * <br>
     * If the event given doesn't have a {@link CAnnounceable} component, it will silently not be announced.<br>
     * It is recommended the event to have a {@link CDifficulty} component to color the message appropriately.
     *
     * @param event Random event to announce.
     * @param players List of players to announce the event to.
     */
    public void announce(RandomEvent event, List<? extends EntityPlayer> players, List<? extends EntityPlayer> cc)
    {
        for (EntityPlayer player : players) {
            announce(event, player, cc);
        }
    }

    /**
     * Announce an event to an individual player.<br>
     * <br>
     * If the event given doesn't have a {@link CAnnounceable} component, it will silently not be announced.<br>
     * It is recommended the event to have a {@link CDifficulty} component to color the message appropriately.
     *
     * @param event Random event to announce.
     * @param player Player to announce event to.
     */
    public void announce(RandomEvent event, EntityPlayer player, List<? extends EntityPlayer> cc)
    {
        // Check if event is announceable.
        if (!event.hasComponent(CAnnounceable.class))
            return;

        CAnnounceable announceable = (CAnnounceable) event.getComponent(CAnnounceable.class);
        CDifficulty difficulty = (CDifficulty) event.getComponent(CDifficulty.class);

        player.sendMessage(generateAnnouncement(announceable, difficulty, null));

        for (EntityPlayer ccPlayer : cc) {
            // If the CC is the current player, skip, they've already got their announcement
            if (ccPlayer == player)
                continue;

            ccPlayer.sendMessage(generateAnnouncement(announceable, difficulty, player));
        }
    }
}
