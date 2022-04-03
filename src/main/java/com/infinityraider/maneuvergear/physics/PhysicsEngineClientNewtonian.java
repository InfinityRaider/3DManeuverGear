package com.infinityraider.maneuvergear.physics;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class PhysicsEngineClientNewtonian extends PhysicsEngineClientBase {
    /** Tick time  */
    private static final double DT = 1.0/20;

    /** Gravitational acceleration */
    private static final double G = 0.0025;

    /** Cable stiffness constant divided by player mass */
    private static final double K_M = 4;

    public PhysicsEngineClientNewtonian(Player player) {
        super(player);
    }

    @Override
    protected void doUpdateLogic() {
        // Get previous velocity
        Vec3 V0 = this.player().getDeltaMovement();
        // Get acceleration vectors
        Vec3 l = this.getCableDeltaVector(true).scale(K_M); // Left cable
        Vec3 r = this.getCableDeltaVector(false).scale(K_M); // Right cable
        Vec3 g = new Vec3(0, -G, 0); // Gravity
        // Sum acceleration vectors
        Vec3 a = l.add(r).add(g);
        // Calculate velocity increment
        Vec3 dV = a.scale(DT);
        // Set new velocity
        this.setPlayerVelocity(V0.add(dV));
    }

    private Vec3 getCableDeltaVector(boolean left) {
        Vec3 A = left ? this.L() : this.R();
        // If there is no left anchor point, there is no force
        if (A == null) {
            return Vec3.ZERO;
        }
        // Determine vector from player to dart
        Vec3 PA = A.subtract(this.playerPosition());
        // Get cable length
        double l = (left ? this.getLeftDart() : this.getRightDart()).getCableLength();
        // Get distance to dart
        double d = PA.length();
        // If the dart is closer to the player than its cable length, there is no force
        if(d <= l) {
            return Vec3.ZERO;
        }
        // Normalize the direction vector and scale it
        return PA.normalize().scale(d-l);
    }
}
