package net.mitchfizz05.randomevents.statuseffect;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.mitchfizz05.randomevents.RandomEvents;

public class PotionBase extends Potion {
    private static ResourceLocation texture = new ResourceLocation(RandomEvents.MOD_ID, "textures/misc/potions.png");

    public PotionBase(String name, boolean badEffect, int potionColor, int iconIndexX, int iconIndexY) {
        super(badEffect, potionColor);
        this.setPotionName("effect.randomevents." + name);
        this.setRegistryName(RandomEvents.MOD_ID, name);
        this.setIconIndex(iconIndexX, iconIndexY);
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        // Bind our texture to the render engine, replacing the vanilla potions.png that is already bound.
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        return super.getStatusIconIndex();
    }
}
