package com.infinityraider.maneuvergear.physics;

import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.entity.EntityDart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class PhysicsEngineClientBase extends PhysicsEngine {
    /** Boost intensity */
    public static final int BOOST = 2;

    /** Player */
    private final Player player;

    /** Left dart */
    @Nullable
    private Vec3 L;
    private boolean retractingLeft;

    /**  Right Dart */
    @Nullable
    private Vec3 R;
    private boolean retractingRight;

    public PhysicsEngineClientBase(Player player) {
        super();
        this.player = player;
    }

    protected Player player() {
        return this.player;
    }

    @Nullable
    protected Vec3 L() {
        return this.L;
    }

    @Nullable
    protected Vec3 R() {
        return this.R;
    }

    protected boolean isRetractingLeft() {
        return this.retractingLeft;
    }

    protected boolean isRetractingRight() {
        return this.retractingRight;
    }

    @Override
    public final void onUpdateTick() {
        //load dart positions
        LoadAnchoredDartPosition(true);
        LoadAnchoredDartPosition(false);
        //check if neither of the darts is anchored, in which case nothing needs to be calculated
        if(this.L == null && this.R == null) {
            return;
        }
        this.doUpdateLogic();
    }

    protected abstract void doUpdateLogic();

    @Override
    public final void onDartAnchored(EntityDart dart, Vec3 position) {
        if(dart.isLeft()) {
            L = position.add(0, 0, 0);
        }
        else {
            R = position.add(0, 0, 0);
        }
    }

    @Override
    public final void onDartRetracted(boolean left) {

    }

    public final void toggleRetracting(boolean left, boolean status) {
        if(left) {
            retractingLeft = status;
        }
        else {
            retractingRight = status;
        }
    }

    @Override
    public final void applyBoost() {
        Vec3 look = player.getLookAngle();
        Vec3 boost = look.multiply(BOOST, BOOST, BOOST);
        Vec3 v = fetchCurrentVelocity();
        setPlayerVelocity(v.add(boost));
    }

    protected Vec3 playerPosition() {
        return new Vec3(this.player().getX(), this.player().getY(), this.player().getZ());
    }

    protected Vec3 fetchCurrentVelocity() {
        return player.getDeltaMovement();
    }

    /** Sets the player's velocity to correspond to a certain velocity given by vector V */
    protected void setPlayerVelocity(Vec3 V) {
        double vX = V.x();
        double vY = V.y();
        double vZ = V.z();
        if(Double.isNaN(vX)) {
            ManeuverGear.instance.getLogger().debug("vX is Nan");
            vX = 0;
        }
        if(Double.isNaN(vY)) {
            ManeuverGear.instance.getLogger().debug("vY is Nan");
            vY = 0;
        }
        if(Double.isNaN(vZ)) {
            ManeuverGear.instance.getLogger().debug("vZ is Nan");
            vZ = 0;
        }
        player.setDeltaMovement(vX, vY, vZ);
    }



    private void LoadAnchoredDartPosition(boolean left) {
        EntityDart dart = this.getDart(left);
        if(dart == null) {
            if(left) {
                this.L = null;
            } else {
                this.R = null;
            }
        } else {
            if(left) {
                this.L = dart.isHooked() ? dart.position() : null;
            } else {
                this.R = dart.isHooked() ? dart.position() : null;
            }
        }
    }
}
