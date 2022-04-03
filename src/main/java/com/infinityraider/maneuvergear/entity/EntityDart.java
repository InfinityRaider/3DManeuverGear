package com.infinityraider.maneuvergear.entity;

import com.infinityraider.infinitylib.entity.EntityThrowableBase;
import com.infinityraider.infinitylib.entity.IEntityRenderSupplier;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.physics.PhysicsEngine;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.registry.EntityRegistry;
import com.infinityraider.maneuvergear.render.RenderEntityDart;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityDart extends EntityThrowableBase {
    //number of blocks to fall per second due to gravity
    public static final float GRAVITY_VELOCITY = 0.5F;
    //number of blocks to fly per second
    public static final float INITIAL_VELOCITY = 100.0F;

    //render distance
    @OnlyIn(Dist.CLIENT)
    private static final int RENDER_DISTANCE_SQ = 64*64;

    //length of the cable
    public static int getMaxCableLength()  {
        return ManeuverGear.instance.getConfig().getCableLength();
    }

    private boolean left;
    private boolean hooked = false;
    private double cableLength;

    // This is needed to track the owner client side, since Vanilla doesn't do it
    private Player player;

    //For client side spawning
    private EntityDart(EntityType<EntityDart> type, Level world) {
        super(type, world);
        if(this.player!=null) {
            DartHandler.instance.getPhysicsEngine(this.getOwner()).setDart(this, left);
        }
        //render the entity even if off screen
        this.noCulling = true;
    }

    public EntityDart(Player player, boolean left) {
        super(EntityRegistry.entityDartEntry, player);
        this.left = left;
        //render the entity even if off screen
        this.noCulling = true;
        Vec3 direction = player.getLookAngle();
        double v_X = direction.x()*INITIAL_VELOCITY/20.0F;
        double v_Y = direction.y()*INITIAL_VELOCITY/20.0F;
        double v_Z = direction.z()*INITIAL_VELOCITY/20.0F;
        this.setDeltaMovement(v_X, v_Y, v_Z);
        this.player = player;
    }

    @Nullable
    @Override
    public Player getOwner() {
        Player player = (Player) super.getOwner();
        return player == null ? this.player : player;
    }

    @Override
    protected void onHit(HitResult impact) {
        ManeuverGear.instance.getLogger().debug("impact " + (this.getLevel().isClientSide() ? "client side" : "server side"));
        Vec3 velocity = this.getDeltaMovement();
        double yaw = -Math.atan2(velocity.z(), velocity.x());
        double pitch = Math.asin(velocity.y() / Math.sqrt(velocity.x() * velocity.x() + velocity.z() * velocity.z()));
        DartHandler.instance.onDartAnchored(this, impact.getLocation(), this.getCableLength(), (float) yaw, (float) pitch);
    }

    @Override
    protected float getGravity() {
        return hooked ? 0 : GRAVITY_VELOCITY/20;
    }

    /** check if this is the left or the right dart */
    public boolean isLeft() {
        return left;
    }

    /** sets the dart to hooked status */
    public void setHooked() {
        this.hooked = true;
    }

    /** check if this dart is grappled somewhere in a block */
    public boolean isHooked() {
        return this.hooked;
    }

    public double calculateDistanceToPlayer() {
        Player player = this.getOwner();
        return player == null ? 0 : this.position().subtract(player.getX(), player.getY(), player.getZ()).length();
    }

    /** sets the length of the cable */
    public void setCableLength(double l) {
        cableLength = l < 0 ? 0: Math.min(l, getMaxCableLength());
    }

    /** gets the length of the cable */
    public double getCableLength() {
        return this.cableLength;
    }

    @Override
    public void tick() {
        //serverside, if this dart is not connected to a player, get rid of it
        if(!this.getLevel().isClientSide() && this.getOwner() == null) {
            this.kill();
            return;
        }
        //if the dart is hooked somewhere, it shouldn't do anything anymore
        if(this.hooked) {
            this.setPosRaw(this.xOld, this.yOld, this.zOld);
            this.setXRot(this.xRotO);
            this.setYRot(this.yRotO);
            return;
        } else {
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
        }
        //check for collision during movement this tick
        this.setCableLength(this.calculateDistanceToPlayer());
        //check if the maximum cable length has been exceeded, and if so retract the dart
        if(this.getCableLength() > getMaxCableLength()) {
            this.retractDart();
            return;
        }
        super.tick();
    }

    public void retractDart() {
        DartHandler.instance.retractDart(this.getOwner(), this.left);
        this.kill();
    }

    @Override
    public void kill() {
        if(this.getLevel().isClientSide()) {
            PhysicsEngine engine = DartHandler.instance.getPhysicsEngine(this.getOwner());
            engine.setDart(null, left);
            engine.onDartRetracted(left);
        }
        super.kill();
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance <= RENDER_DISTANCE_SQ;
    }

    @Override
    public void writeCustomEntityData(CompoundTag tag) {
        tag.putBoolean(Names.NBT.LEFT, left);
        tag.putBoolean(Names.NBT.HOOKED, hooked);
        tag.putDouble(Names.NBT.LENGTH, this.getCableLength());
        // This is necessary since client side, the owner is not known in the vanilla superclass
        Player player = this.getOwner();
        if(player == null) {
            tag.putBoolean(Names.NBT.FLAG, false);
        } else {
            tag.putBoolean(Names.NBT.FLAG, true);
            tag.putUUID(Names.NBT.PLAYER, player.getUUID());
        }
    }

    @Override
    public void readCustomEntityData(CompoundTag tag) {
        left = tag.getBoolean(Names.NBT.LEFT);
        hooked = tag.getBoolean(Names.NBT.HOOKED);
        this.setCableLength(tag.getDouble(Names.NBT.LENGTH));
        if(tag.contains(Names.NBT.FLAG) && tag.getBoolean(Names.NBT.FLAG)) {
            this.player = this.getLevel().getPlayerByUUID(tag.getUUID(Names.NBT.PLAYER));
            if (this.player != null) {
                DartHandler.instance.getPhysicsEngine(this.getOwner()).setDart(this, left);
            }
        }
    }

    public static class SpawnFactory implements EntityType.EntityFactory<EntityDart> {
        private static final SpawnFactory INSTANCE = new SpawnFactory();

        public static SpawnFactory getInstance() {
            return INSTANCE;
        }

        private SpawnFactory() {}

        @Override
        public EntityDart create(EntityType<EntityDart> type, Level world) {
            return new EntityDart(type, world);
        }
    }


    public static class RenderFactory implements IEntityRenderSupplier<EntityDart> {
        private static final RenderFactory INSTANCE = new RenderFactory();

        public static RenderFactory getInstance() {
            return INSTANCE;
        }

        private RenderFactory() {}

        @Override
        @OnlyIn(Dist.CLIENT)
        public Supplier<EntityRendererProvider<EntityDart>> supplyRenderer() {
            return () -> RenderEntityDart::new;
        }
    }
}
