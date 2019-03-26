package net.mitchfizz05.randomevents.mechanics;

import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.mitchfizz05.randomevents.RandomEvents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RandomFoodPoisoning
{
    public static ArrayList<String> rawFood = new ArrayList<>();

    public static float chance = 0.33f;
    public static int duration = 30; // in seconds

    private static boolean enabled = true;

    static {
        rawFood.add(Items.PORKCHOP.getRegistryName().toString());
        rawFood.add(Items.BEEF.getRegistryName().toString());
        rawFood.add(Items.FISH.getRegistryName().toString());
        rawFood.add(Items.CHICKEN.getRegistryName().toString());
        rawFood.add(Items.RABBIT.getRegistryName().toString());
    }

    public static void postInit(FMLPostInitializationEvent event)
    {
        // Load config
        enabled = RandomEvents.config.get("food_poisoning", "enabled", enabled).getBoolean();
        chance = (float) RandomEvents.config.get("food_poisoning", "chance", chance,
                "Chance that eating raw food will give food poisoning. 0-1").getDouble();
        duration = RandomEvents.config.get("food_poisoning", "duration", duration,
                "Duration of food poisoning, in seconds.").getInt();
        rawFood = new ArrayList<>(Arrays.asList(RandomEvents.config.get("food_poisoning", "poisonous_foods",
                rawFood.toArray(new String[0]),
                "List of foods that cause food poisoning.").getStringList()));
        RandomEvents.config.save();

        if (!enabled) {
            RandomEvents.logger.info("Food poisoning disabled.");
            return;
        }

        RandomEvents.logger.info("Applying food poisoning traits to poisonous foods...");
        for (Item item : Item.REGISTRY) {
            if (!(item instanceof ItemFood)) continue;

            if (rawFood.contains(item.getRegistryName().toString())) {
                ((ItemFood) item).setPotionEffect(new PotionEffect(MobEffects.HUNGER,duration * 20, 0), chance);
            }
        }
    }
}
