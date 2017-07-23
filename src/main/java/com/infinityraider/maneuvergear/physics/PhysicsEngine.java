package com.infinityraider.maneuvergear.physics;

import com.infinityraider.maneuvergear.entity.EntityDart;

public abstract class PhysicsEngine {
    /** Left dart */
    protected EntityDart leftDart;

    /**  Right Dart */
    protected EntityDart rightDart;

    protected PhysicsEngine() {}

    public abstract void updateTick();

    public final EntityDart getDart(boolean left) {
        return left?leftDart:rightDart;
    }

    public final void setDart(EntityDart dart, boolean left) {
        if(left) {
            leftDart = dart;
        } else {
            rightDart = dart;
        }
    }

    public abstract void onDartAnchored(EntityDart dart);

    public abstract void onDartRetracted(boolean left);

    public abstract void toggleRetracting(boolean left, boolean status);

    public abstract void applyBoost();
}
