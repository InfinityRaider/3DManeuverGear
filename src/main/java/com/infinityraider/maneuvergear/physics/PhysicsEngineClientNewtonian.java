package com.infinityraider.maneuvergear.physics;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class PhysicsEngineClientNewtonian extends PhysicsEngineClientBase {
    /** Tick time  */
    private static final double DT = 1.0/20;

    /** Gravitational acceleration, calculated from gravity change */
    private static final double G = 0.0025/ DT;

    /** Player mass constant */
    private static final double M = 70;  // TODO: tweak

    /** Cable stiffness constant */
    private static final double K = 5;  // TODO: tweak

    public PhysicsEngineClientNewtonian(Player player) {
        super(player);
    }

    @Override
    protected void doUpdateLogic() {
        // Get previous velocity
        Vec3 V0 = this.player().getDeltaMovement();
        // Get force vectors
        Vec3 l = this.getCableForce(true);
        Vec3 r = this.getCableForce(false);
        Vec3 g = new Vec3(0, -M*G, 0);
        // Sum force vectors
        Vec3 F = l.add(r).add(g);
        // Calculate velocity increment
        Vec3 dV = F.scale(DT/M);
        // Set new velocity
        this.setPlayerVelocity(V0.add(dV));
    }

    private Vec3 getCableForce(boolean left) {
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
        return PA.normalize().scale(K*(d-l));
    }
}
