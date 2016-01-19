package com.InfinityRaider.maneuvergear.physics;

import com.InfinityRaider.maneuvergear.entity.EntityDart;

public class PhysicsEngineDummy extends PhysicsEngine {
    public PhysicsEngineDummy() {
        super();
    }

    @Override
    public void updateTick() {}

    @Override
    public void onDartAnchored(EntityDart dart) {}

    @Override
    public void onDartRetracted(boolean left) {}

    @Override
    public void toggleRetracting(boolean left, boolean status) {
         }

    @Override
    public void applyBoost() { }
}
