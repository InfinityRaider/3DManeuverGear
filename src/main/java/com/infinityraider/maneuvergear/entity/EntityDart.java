package com.infinityraider.maneuvergear.entity;

import com.infinityraider.infinitylib.entity.EntityThrowableBase;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.physics.PhysicsEngine;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.registry.EntityRegistry;
import com.infinityraider.maneuvergear.render.RenderEntityDart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.ParametersAreNonnullByDefault;

public class EntityDart extends EntityThrowableBase {
    //number of blocks to fall per second due to gravity
    public static final float GRAVITY_VELOCITY = 0.5F;
    //number of blocks to fly per second
    public static final float INITIAL_VELOCITY = 100.0F;

    //length of the cable
    public static int getMaxCableLength()  {
        return ManeuverGear.instance.getConfig().getCableLength();
    }

    private boolean left;
    private boolean hooked = false;
    private double cableLength;
    private PlayerEntity player;

    //For client side spawning
    private EntityDart(EntityType<EntityDart> type, World world) {
        super(type, world);
        if(this.player!=null) {
            DartHandler.instance.getPhysicsEngine(this.getPlayer()).setDart(this, left);
        }
        //render the entity even if off screen
        this.ignoreFrustumCheck = true;
    }

    public EntityDart(PlayerEntity player, boolean left) {
        super(EntityRegistry.getInstance().entityDartEntry, player, player.getEntityWorld());
        this.player = player;
        this.left = left;
        //render the entity even if off screen
        this.ignoreFrustumCheck = true;
        Vector3d direction = player.getLookVec();
        double v_X = direction.getX()*INITIAL_VELOCITY/20.0F;
        double v_Y = direction.getY()*INITIAL_VELOCITY/20.0F;
        double v_Z = direction.getZ()*INITIAL_VELOCITY/20.0F;
        this.setMotion(v_X, v_Y, v_Z);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void onImpact(RayTraceResult impact) {
        ManeuverGear.instance.getLogger().debug("impact " + (this.getEntityWorld().isRemote ? "client side" : "server side"));
        Vector3d velocity = this.getMotion();
        double yaw = -Math.atan2(velocity.getZ(), velocity.getX());
        double pitch = Math.asin(velocity.getY() / Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ()));
        DartHandler.instance.onDartAnchored(this, impact.getHitVec(), this.getCableLength(), (float) yaw, (float) pitch);
    }

    @Override
    protected void registerData() {

    }

    @Override
    protected float getGravityVelocity() {
        return hooked ? 0 : GRAVITY_VELOCITY/20;
    }

    /** returns the player that fired this dart */
    public PlayerEntity getPlayer() {
        return player;
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
        PlayerEntity player = this.getPlayer();
        return this.getPositionVec().subtract(player.getPosX(), player.getPosY(), player.getPosZ()).length();
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
        if(!this.getEntityWorld().isRemote && this.getPlayer() == null) {
            this.setDead();
            return;
        }
        //if the dart is hooked somewhere, it shouldn't do anything anymore
        if(this.hooked) {
            this.setRawPosition(this.prevPosX, this.prevPosY, this.prevPosZ);
            this.rotationYaw = this.prevRotationYaw;
            this.rotationPitch = this.prevRotationPitch;
            return;
        } else {
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
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
        DartHandler.instance.retractDart(this.getPlayer(), this.left);
        this.setDead();
    }

    @Override
    public void setDead() {
        if(this.getEntityWorld().isRemote) {
            PhysicsEngine engine = DartHandler.instance.getPhysicsEngine(player);
            engine.setDart(null, left);
            engine.onDartRetracted(left);
        }
        super.setDead();
    }

    @Override
    public void writeCustomEntityData(CompoundNBT tag) {
        tag.putBoolean(Names.NBT.LEFT, left);
        tag.putBoolean(Names.NBT.HOOKED, hooked);
        tag.putDouble(Names.NBT.LENGTH, this.getCableLength());
        tag.putUniqueId(Names.NBT.PLAYER, player.getUniqueID());
    }

    @Override
    public void readCustomEntityData(CompoundNBT tag) {
        left = tag.getBoolean(Names.NBT.LEFT);
        hooked = tag.getBoolean(Names.NBT.HOOKED);
        this.setCableLength(tag.getDouble(Names.NBT.LENGTH));
        player = this.getEntityWorld().getPlayerByUuid(tag.getUniqueId(Names.NBT.PLAYER));
        if (this.player != null) {
            DartHandler.instance.getPhysicsEngine(this.getPlayer()).setDart(this, left);
        }

    }

    public static class SpawnFactory implements EntityType.IFactory<EntityDart> {
        private static final SpawnFactory INSTANCE = new SpawnFactory();

        public static SpawnFactory getInstance() {
            return INSTANCE;
        }

        private SpawnFactory() {}

        @Override
        public EntityDart create(EntityType<EntityDart> type, World world) {
            return new EntityDart(type, world);
        }
    }


    public static class RenderFactory implements IRenderFactory<EntityDart> {
        private static final RenderFactory INSTANCE = new RenderFactory();

        public static RenderFactory getInstance() {
            return INSTANCE;
        }

        private RenderFactory() {}

        @Override
        @OnlyIn(Dist.CLIENT)
        public EntityRenderer<? super EntityDart> createRenderFor(EntityRendererManager manager) {
            return new RenderEntityDart(manager);
        }
    }
}
