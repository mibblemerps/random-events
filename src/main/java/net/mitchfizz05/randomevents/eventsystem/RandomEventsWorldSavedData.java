package net.mitchfizz05.randomevents.eventsystem;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;
import net.mitchfizz05.randomevents.RandomEvents;
import net.mitchfizz05.randomevents.eventsystem.component.IComponent;
import net.mitchfizz05.randomevents.eventsystem.randomevent.RandomEvent;

import java.util.List;

/**
 * The {@link WorldSavedData} provider that saves and loads Random Events data.
 */
public class RandomEventsWorldSavedData extends WorldSavedData
{
    public RandomEventsWorldSavedData(String tagname)
    {
        super(tagname);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        // Get all events
        List<RandomEvent> events = RandomEvents.randomEventRegistry.randomEvents;

        // Loop through all events and their components
        for (RandomEvent event : events) {
            for (IComponent component : event.getComponents()) {
                // Check if this component needs to save data to NBT.
                if (component instanceof IUsesNBT) {
                    // Read NBT data
                    ((IUsesNBT) component).readFromNBT(getNbtTagForEvent(event, nbt));
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        // Get all events
        List<RandomEvent> events = RandomEvents.randomEventRegistry.randomEvents;

        // Loop through all events and their components
        for (RandomEvent event : events) {
            for (IComponent component : event.getComponents()) {
                // Check if this component needs to write data to NBT.
                if (component instanceof IUsesNBT) {
                    // Write NBT data
                    ((IUsesNBT) component).writeToNBT(getNbtTagForEvent(event, nbt));
                }
            }
        }

        return nbt;
    }

    /**
     * Get the NBT tag for a Random Event.
     * If the event doesn't have an NBT tag yet, one will be created.
     *
     * @param event Random Event
     * @param nbt Root NBT compound tag
     * @return Event tag
     */
    public NBTTagCompound getNbtTagForEvent(RandomEvent event, NBTTagCompound nbt)
    {
        if (!nbt.hasKey(event.getName()))
            nbt.setTag(event.getName(), new NBTTagCompound());
        return nbt.getCompoundTag(event.getName());
    }
}
