package com.infinityraider.maneuvergear.physics;

import com.infinityraider.maneuvergear.entity.EntityDart;
import net.minecraft.util.math.vector.Vector3d;

public abstract class PhysicsEngine {
    /** Left dart */
    private EntityDart leftDart;
    private EntityDart nextLeft;
    private boolean updateLeft;

    /**  Right Dart */
    private EntityDart rightDart;
    private EntityDart nextRight;
    private boolean updateRight;

    protected PhysicsEngine() {}

    public final void updateTick() {
        this.onUpdateTick();
        if(this.updateLeft) {
            this.leftDart = this.nextLeft;
            this.nextLeft = null;
            this.updateLeft = false;
        }
        if(this.updateRight) {
            this.rightDart = this.nextRight;
            this.nextRight = null;
            this.updateRight = false;
        }
    }

    protected abstract void onUpdateTick();

    public final EntityDart getLeftDart() {
        return this.leftDart;
    }

    public final EntityDart getRightDart() {
        return this.rightDart;
    }

    public final EntityDart getDart(boolean left) {
        return left ? this.leftDart : this.rightDart;
    }

    public final void setDart(EntityDart dart, boolean left) {
        if(left) {
            this.nextLeft = dart;
            this.updateLeft = true;
        } else {
            this.nextRight = dart;
            this.updateRight = true;
        }
    }

    public abstract void onDartAnchored(EntityDart dart, Vector3d position);

    public abstract void onDartRetracted(boolean left);

    public abstract void toggleRetracting(boolean left, boolean status);

    public abstract void applyBoost();
}
