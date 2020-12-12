package com.infinityraider.maneuvergear.physics;

import com.infinityraider.maneuvergear.entity.EntityDart;
import net.minecraft.util.math.vector.Vector3d;

public class PhysicsEngineDummy extends PhysicsEngine {
    public PhysicsEngineDummy() {
        super();
    }

    @Override
    public void onUpdateTick() {}

    @Override
    public void onDartAnchored(EntityDart dart, Vector3d position) {}

    @Override
    public void onDartRetracted(boolean left) {}

    @Override
    public void toggleRetracting(boolean left, boolean status) {
         }

    @Override
    public void applyBoost() { }
}
