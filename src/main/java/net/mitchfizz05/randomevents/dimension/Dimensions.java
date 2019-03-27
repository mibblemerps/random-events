package net.mitchfizz05.randomevents.dimension;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.mitchfizz05.randomevents.dimension.dreamrealm.DimensionDreamRealm;

public class Dimensions
{
    public static final DimensionType DREAMREALM = DimensionType.register("DreamRealm", "_dream", 65, DimensionDreamRealm.class, false);

    public static void regsiter()
    {
        DimensionManager.registerDimension(DREAMREALM.getId(), DREAMREALM);
    }
}
