package net.mitchfizz05.randomevents;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.mitchfizz05.randomevents.util.room.Room;
import net.mitchfizz05.randomevents.util.room.RoomAnalyser;

/**
 * Allows debugging of rooms.
 */
public class CommandRoomHelper extends CommandBase
{
    @Override
    public String getName()
    {
        return "roomhelper";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/roomhelper";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        // Analyse room
        Room room = RoomAnalyser.analyseRoom(server.getEntityWorld(), sender.getPosition());

        // Styles
        Style keyStyle = new Style().setColor(TextFormatting.YELLOW);
        Style valueStyle = new Style().setColor(TextFormatting.GRAY);

        // Print information
        ITextComponent msg = new TextComponentString("\n");
        msg.appendSibling(new TextComponentString("Room Analysis\n").setStyle(new Style().setBold(true).setColor(TextFormatting.DARK_AQUA)));

        // Room dimensions
        msg.appendSibling(new TextComponentString("Dimensions: ").setStyle(keyStyle));
        msg.appendSibling(new TextComponentString(
                Integer.toString(room.width) + "x"
                + Integer.toString(room.height) + "x"
                + Integer.toString(room.depth) + "\n"
        ).setStyle(valueStyle));

        // Room volume
        msg.appendSibling(new TextComponentString("Volume: ").setStyle(keyStyle));
        msg.appendSibling(new TextComponentString(Integer.toString(room.size) + "\n").setStyle(valueStyle));

        sender.sendMessage(msg);
    }
}
