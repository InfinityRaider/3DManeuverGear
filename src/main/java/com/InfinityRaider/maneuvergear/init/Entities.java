package com.InfinityRaider.maneuvergear.init;

import com.InfinityRaider.maneuvergear.ManeuverGear;
import com.InfinityRaider.maneuvergear.entity.EntityDart;
import com.InfinityRaider.maneuvergear.reference.Names;
import cpw.mods.fml.common.registry.EntityRegistry;

public class Entities {
    public static void init() {
        EntityRegistry.registerModEntity(EntityDart.class, Names.Objects.DART, 0, ManeuverGear.instance, EntityDart.CABLE_LENGTH*2, 1, true);
    }
}
