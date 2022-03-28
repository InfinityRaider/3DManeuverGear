package com.infinityraider.maneuvergear.utility;

import net.minecraft.world.entity.LivingEntity;

public interface IManeuverGear {
    void onWornTick(LivingEntity entity);

    void onEquip(LivingEntity entity);

    void onUnequip(LivingEntity entity);
}
