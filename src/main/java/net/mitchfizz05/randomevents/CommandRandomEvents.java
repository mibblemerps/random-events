package net.mitchfizz05.randomevents;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.*;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;
import net.mitchfizz05.randomevents.eventsystem.services.RandomEventServices;
import net.mitchfizz05.randomevents.util.TimeHelper;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Random Events utility command.
 */
public class CommandRandomEvents implements ICommand
{
    private final ArrayList<String> aliases;

    public CommandRandomEvents()
    {
        aliases = new ArrayList<String>();
        aliases.add("randomevents");
        aliases.add("re");
    }

    public String getName()
    {
        return "randomevents";
    }

    public String getUsage(ICommandSender sender)
    {
        return "randomevents trigger <randomevent> [delay] [player]\n" +
                "randomevents info <randomevent>\n" +
                "randomevents forecast\n" +
                "randomevents list";
    }

    public List<String> getAliases()
    {
        return this.aliases;
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        // Check if sender is Mitchfizz05
        // todo: we don't want to keep this forever
        if (sender.getCommandSenderEntity().getUniqueID().toString().equalsIgnoreCase("b6284cef-69f4-40d2-8730-54053b1a925d")) {
            return true;
        }

        // TODO: add permissions.
        return true;
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        return null;
    }

    public boolean isUsernameIndex(String[] var1, int var2)
    {
        return false;
    }

    @Override
    public int compareTo(ICommand o)
    {
        return 0;
    }

    /**
     * Callback for when the command is executed
     *
     * @param server The server instance
     * @param sender The sender who executed the command
     * @param args The arguments that were passed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
            throw new WrongUsageException(getUsage(sender));

        // Dispatch command
        String subCommand = args[0];
        if (subCommand.equals("info"))
            commandInfo(server, sender, args);
        else if (subCommand.equals("trigger"))
            commandTrigger(server, sender, args);
        else if (subCommand.equals("forecast"))
            commandForecast(server, sender, args);
        else if (subCommand.equals("list"))
            commandList(server, sender, args);
        else
            throw new WrongUsageException(getUsage(sender));
    }

    protected RandomEvent getEventByName(String name) throws CommandException
    {
        RandomEvent randomEvent = RandomEvents.randomEventRegistry.get(name);
        if (randomEvent == null)
            throw new CommandException("Event \"" + name + "\" doesn't exist!");

        return randomEvent;
    }

    /**
     * Subcommand for getting information on an randomevent (time elapsed, time till trigger, etc..)
     */
    protected void commandInfo(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 2) throw new WrongUsageException(getUsage(sender));

        String eventName = args[1];

        // Get Random Event
        RandomEvent event = getEventByName(eventName);

        // Styles
        Style keyStyle = new Style().setColor(TextFormatting.YELLOW);
        Style valueStyle = new Style().setColor(TextFormatting.GRAY);

        // Construct info
        ITextComponent infoMsg = new TextComponentString("\n");
        infoMsg.appendSibling(new TextComponentString(event.getName() + "\n").setStyle(new Style().setColor(TextFormatting.DARK_AQUA).setBold(true)));

        // Is enabled?
        if (!event.isEnabled())
            infoMsg.appendSibling(new TextComponentString("Disabled!\n").setStyle(new Style().setColor(TextFormatting.RED)));

        // Is currently active?
        CLongEvent longEventComponent = (CLongEvent) event.getComponent(CLongEvent.class);
        if (longEventComponent != null && longEventComponent.isActive()) {
            infoMsg.appendSibling(new TextComponentString("Currently active!\n").setStyle(new Style().setColor(TextFormatting.GREEN)));

            // How long till the event ends?
            CLongTimedEvent longTimedEventComponent = (CLongTimedEvent) event.getComponent(CLongTimedEvent.class);
            if (longTimedEventComponent != null) {
                ITextComponent msg = new TextComponentString("Ends in: ").setStyle(keyStyle);
                msg.appendSibling(new TextComponentString(TimeHelper.formatSeconds(longTimedEventComponent.timeLeft) + "\n").setStyle(valueStyle));

                infoMsg.appendSibling(msg);
            }
        }

        // Announcement
        CAnnounceable announceableComponent = (CAnnounceable) event.getComponent(CAnnounceable.class);
        if (announceableComponent != null) {
            ITextComponent msg = new TextComponentString("Announcement: ").setStyle(keyStyle);
            msg.appendSibling(new TextComponentString(announceableComponent.getLocalisedAnnouncement() + "\n").setStyle(valueStyle));

            infoMsg.appendSibling(msg);
        }

        // Event Difficulty
        CDifficulty difficultyComponent = (CDifficulty) event.getComponent(CDifficulty.class);
        if (difficultyComponent != null) {
            ITextComponent msg = new TextComponentString("Difficulty: ").setStyle(keyStyle);
            msg.appendSibling(new TextComponentString(difficultyComponent.difficulty.toString() + "\n")
                    .setStyle(new Style().setColor(difficultyComponent.difficulty.getColor())));

            infoMsg.appendSibling(msg);
        }

        // World min/max wait time
        CWorldTimer worldTimerComponent = (CWorldTimer) event.getComponent(CWorldTimer.class);
        if (worldTimerComponent != null) {
            ITextComponent msg = new TextComponentString("Global Timer. Trigger time: ").setStyle(keyStyle);
            msg.appendSibling(new TextComponentString(worldTimerComponent.minWaitTime + "-" + worldTimerComponent.maxWaitTime + " seconds\n")
                    .setStyle(valueStyle));

            infoMsg.appendSibling(msg);
        }

        // Player min/max wait time
        CPlayerTimer playerTimerComponent = (CPlayerTimer) event.getComponent(CPlayerTimer.class);
        if (playerTimerComponent != null) {
            ITextComponent msg = new TextComponentString("(Player Timer) Trigger time: ").setStyle(keyStyle);
            msg.appendSibling(new TextComponentString(playerTimerComponent.minWaitTime + "-" + playerTimerComponent.maxWaitTime + " seconds\n")
                    .setStyle(valueStyle));

            infoMsg.appendSibling(msg);
        }

        sender.sendMessage(infoMsg);
    }

    /**
     * Subcommand for triggering an randomevent.
     */
    protected void commandTrigger(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String eventName = null;
        int delay = 0;
        String targetPlayerName = null;

        if (args.length < 2 || args.length > 4)
            throw new WrongUsageException(getUsage(sender));

        switch (args.length) {
            case 4: // trigger <randomevent name> [delay] [player]
                targetPlayerName = args[3];
            case 3: // trigger <randomevent name> [delay]
                try { delay = Integer.parseInt(args[2]); } catch (NumberFormatException e) { throw new WrongUsageException("[delay] parameter expected a number"); }
            case 2: // trigger <randomevent name>
                eventName = args[1];
        }

        if (delay != 0) {
            sender.sendMessage(new TextComponentString("Warning: delay parameter doesn't current work.").setStyle(new Style().setColor(TextFormatting.YELLOW)));
        }

        // Target player
        EntityPlayer target = null;

        // Set target to executing player
        Entity senderEntity = sender.getCommandSenderEntity();
        if (senderEntity instanceof EntityPlayer)
            target = (EntityPlayer) senderEntity;

        // Get custom player target is specified.
        if (targetPlayerName != null) {
            senderEntity = server.getPlayerList().getPlayerByUsername(targetPlayerName);
            if (senderEntity == null)
                throw new CommandException("Player " + targetPlayerName + " not found!");
        }

        // Get randomevent
        RandomEvent event = getEventByName(eventName);

        try {
            //event.execute(server.getEntityWorld(), target);
            RandomEventServices.executeEventService.executeEvent(event, server.getEntityWorld(), target);
        } catch (ExecuteEventException e) {
            throw new CommandException("Couldn't execute randomevent: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommandException("An unexpected exception occurred executing " + event.getName() + ". See log for details.");
        }
    }

    /**
     * Subcommand for getting the randomevent forecast (next couple of events that will happen).
     */
    protected void commandForecast(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        // todo: reimplement

        /*ArrayList<IRandomEvent> events = RandomEvents.randomEventRegistry.randomEvents;

        final EntityPlayer player = null;

        List<RandomEvent> forecast = new ArrayList<RandomEvent>();

        for (IRandomEvent ievent : events) {
            if (getSecondsTillEventTrigger(ievent, player) != -1) {
                forecast.add((RandomEvent) ievent);
            }
        }

        // Sort the events by time till trigger, nearest to triggering first.
        Collections.sort(forecast, new Comparator<RandomEvent>()
        {
            @Override
            public int compare(RandomEvent o1, RandomEvent o2)
            {
                int secs1 = getSecondsTillEventTrigger(o1, player);
                int secs2 = getSecondsTillEventTrigger(o2, player);

                return (secs1 == secs2) ? 0 : (secs1 > secs2) ? 1 : -1;
            }
        });

        sender.sendMessage(new TextComponentString("\nEvent forecast").setStyle(new Style().setBold(true).setColor(TextFormatting.AQUA)));

        // Get the top 8 events and show the seconds till trigger.
        for (int i = 0; i < Math.min(forecast.size(), 8); i++) {
            RandomEvent randomevent = forecast.get(i);
            WorldTimerComponent worldTimer = (WorldTimerComponent) randomevent.getComponent(WorldTimerComponent.class);
            int secs = worldTimer.getSecondsTillNextTrigger();

            TextComponentString msg = new TextComponentString("");
            msg.appendSibling(new TextComponentString(randomevent.getName() + ": "));
            msg.appendSibling(new TextComponentString(DurationFormatHelper.formatSeconds(secs)).setStyle(new Style().setColor(TextFormatting.GRAY)));
            sender.sendMessage(msg);
        }*/
    }

    /**
     * Subcommand for getting list of all events.
     */
    protected void commandList(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        ArrayList<RandomEvent> events = RandomEvents.randomEventRegistry.randomEvents;

        sender.sendMessage(new TextComponentString("\nRegistered events (" + events.size() + "):").setStyle(new Style().setBold(true).setColor(TextFormatting.AQUA)));

        for (RandomEvent event : events) {
            sender.sendMessage(new TextComponentString(" - " + event.getName()));
        }
    }
}
