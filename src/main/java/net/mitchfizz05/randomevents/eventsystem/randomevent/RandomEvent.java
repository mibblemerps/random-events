package net.mitchfizz05.randomevents.eventsystem.randomevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.mitchfizz05.randomevents.eventsystem.EventDifficulty;
import net.mitchfizz05.randomevents.eventsystem.ExecuteEventException;
import net.mitchfizz05.randomevents.eventsystem.component.CAnnounceable;
import net.mitchfizz05.randomevents.eventsystem.component.CDifficulty;
import net.mitchfizz05.randomevents.eventsystem.component.IComponent;
import net.mitchfizz05.randomevents.eventsystem.event.RandomEventTriggerEvent;
import net.mitchfizz05.randomevents.eventsystem.services.AnnouncerService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Random Event base class.
 */
public class RandomEvent
{
    private String name;

    /**
     * Array of components on this randomevent.
     */
    private ArrayList<IComponent> components = new ArrayList<IComponent>();

    public RandomEvent(String name)
    {
        this.name = name;

        addComponent(new CDifficulty(EventDifficulty.BAD));
        addComponent(new CAnnounceable(AnnouncerService.getStandardTranslationKey(this)));
    }

    /**
     * Event code name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get component on this randomevent.
     * If the component doesn't exist, returns null.
     *
     * @param componentClass Component class
     * @return Component, or null if it isn't on this randomevent.
     */
    public IComponent getComponent(Class componentClass)
    {
        for (IComponent component : components) {
            if (component.getClass() == componentClass) {
                return component;
            }
        }

        return null;
    }

    /**
     * Does this randomevent have a particular component?
     *
     * @param componentClass Component class
     * @return Has component?
     */
    public boolean hasComponent(Class componentClass)
    {
        return getComponent(componentClass) != null;
    }

    /**
     * Get an list of all components on this randomevent.
     * Note this list is immutable, so components can not be added/removed through this, though they can be modified.
     */
    public List<IComponent> getComponents()
    {
        return Collections.unmodifiableList(components);
    }

    /**
     * Remove a component from this randomevent.
     *
     * @param componentClass Component class
     */
    public void removeComponent(Class componentClass)
    {
        for (int i = 0; i < components.size(); i++) {
            IComponent component = components.get(i);

            if (component.getClass() == componentClass) {
                components.remove(i);
                return;
            }
        }
    }

    /**
     * Add new component to this randomevent.
     */
    public void addComponent(IComponent component)
    {
        if (hasComponent(component.getClass())) {
            // Already has component
        }

        components.add(component);
    }

    // ---

    /**
     * Where the event should perform it's effects.
     *
     * WARNING: Do not execute this directly! Use the {@link net.mitchfizz05.randomevents.eventsystem.services.ExecuteEventService}!
     *
     * @param world World
     * @param player Player the event is targeting.
     * @throws ExecuteEventException If the event fails to execute, this will be thrown.
     */
    public void execute(@Nonnull World world, @Nullable EntityPlayer player) throws ExecuteEventException {}
}
