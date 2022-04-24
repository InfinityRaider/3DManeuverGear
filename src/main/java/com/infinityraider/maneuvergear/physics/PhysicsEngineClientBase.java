package com.infinityraider.maneuvergear.physics;

import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.entity.EntityDart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IExtensibleEnum;

import javax.annotation.Nullable;
import java.util.function.Function;

public abstract class PhysicsEngineClientBase extends PhysicsEngine {
    public enum Type implements IExtensibleEnum {
        GEOMETRIC(PhysicsEngineClientGeometric::new),
        NEWTONIAN(PhysicsEngineClientNewtonian::new);

        private final Function<Player, PhysicsEngine> factory;

        Type(Function<Player, PhysicsEngine> factory) {
            this.factory = factory;
        }

        public final PhysicsEngine newEngine(Player player) {
            return this.factory.apply(player);
        }

        @SuppressWarnings("unused")
        public static PhysicsEngineClientBase.Type create(String name, Function<Player, PhysicsEngine> factory) {
            throw new IllegalStateException("Enum not extended");
        }
    }

    /** Velocity at which the cable is retracted (blocks per second) */
    public static final double retractingVelocity = ManeuverGear.instance.getConfig().getRetractingSpeed()/20.0D;

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

    /** Locked state (no cables can be retracted anymore */
    private boolean locked;

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
        //decrement cables if necessary
        this.decrementCableLength(this.getLeftDart());
        this.decrementCableLength(this.getRightDart());
        this.locked = this.checkLocked();
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
        double cap = ManeuverGear.instance.getConfig().getVelocityCap();
        if(cap > 0) {
            double v_sq = vX*vX + vY*vY + vZ*vZ;
            double f = Mth.fastInvSqrt(v_sq);
            if(v_sq > cap*cap) {
                vX *= cap*f;
                vY *= cap*f;
                vZ *= cap*f;
            }
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

    private void decrementCableLength(EntityDart dart) {
        if(this.locked) {
            return;
        }
        if(dart == null) {
            return;
        }
        if((dart.isLeft() && isRetractingLeft()) || (!dart.isLeft() && isRetractingRight())) {
            dart.setCableLength(dart.getCableLength() - retractingVelocity);
        }
    }

    private boolean checkLocked() {
        if(this.L == null || this.R == null) {
            return false;
        }
        double LR = this.L.subtract(this.R).length();
        double l = this.getLeftDart().getCableLength();
        double r = this.getRightDart().getCableLength();
        return LR >= l + r;
    }
}
