package net.mitchfizz05.randomevents.content;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mitchfizz05.randomevents.RandomEvents;

import java.util.ArrayList;

/**
 * Random Events loot tables.
 */
public class RELootTables
{
    public static LootPool reGeneralLootPool;

    /**
     * Chests that reGeneralLootPool will be injected into
     */
    public static ArrayList<String> reGeneralInjectInto = new ArrayList<String>();

    static
    {
        reGeneralInjectInto.add("minecraft:chests/abandoned_mineshaft");
        reGeneralInjectInto.add("minecraft:chests/desert_pyramid");
        reGeneralInjectInto.add("minecraft:chests/jungle_temple");
        reGeneralInjectInto.add("minecraft:chests/simple_dungeon");
        reGeneralInjectInto.add("minecraft:chests/stronghold_corridor");
        reGeneralInjectInto.add("minecraft:chests/village_blacksmith");
    }

    public RELootTables()
    {
        MinecraftForge.EVENT_BUS.register(this);

        // Loot entry for general Random Events loot
        LootEntryTable reGeneralLoot = new LootEntryTable(new ResourceLocation("random_events:random_events_general"),
                1, 0, new LootCondition[0], "random_events_general");

        // Create Random Events loot pool
        reGeneralLootPool = new LootPool(new LootEntry[] { reGeneralLoot },
                new LootCondition[0], // No special conditions
                new RandomValueRange(0, 4), // Rolls
                new RandomValueRange(0, 1), // Bonus rolls
                "random_events_general_pool");
    }

    @SubscribeEvent
    public void lootLoad(LootTableLoadEvent event) {
        // Inject loot
        if (reGeneralInjectInto.contains(event.getName().toString())) {
            event.getTable().addPool(reGeneralLootPool);
            RandomEvents.logger.info("Injected loot pool into " + event.getName().toString() + "!");
        }
    }
}
