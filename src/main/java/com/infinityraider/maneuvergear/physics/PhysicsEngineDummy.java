package com.infinityraider.maneuvergear.physics;

import com.infinityraider.maneuvergear.entity.EntityDart;
import net.minecraft.world.phys.Vec3;

public class PhysicsEngineDummy extends PhysicsEngine {
    public PhysicsEngineDummy() {
        super();
    }

    @Override
    public void onUpdateTick() {}

    @Override
    public void onDartAnchored(EntityDart dart, Vec3 position) {}

    @Override
    public void onDartRetracted(boolean left) {}

    @Override
    public void toggleRetracting(boolean left, boolean status) {
         }

    @Override
    public void applyBoost() { }
}
