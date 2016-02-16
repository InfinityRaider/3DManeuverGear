package com.InfinityRaider.maneuvergear.entity;

import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.handler.DartHandler;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngine;
import com.InfinityRaider.maneuvergear.physics.Vector;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.render.RenderEntityDart;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDart extends EntityThrowable implements IEntityAdditionalSpawnData {
    //number of blocks to fall per second due to gravity
    public static final float GRAVITY_VELOCITY = 0.5F;
    //number of blocks to fly per second
    public static final float INITIAL_VELOCITY = 100.0F;
    //length of the cable
    public static final int CABLE_LENGTH =  ConfigurationHandler.cableLength;

    private boolean left;
    private boolean hooked = false;
    private double cableLength;
    private EntityPlayer player;

    /** Default constructer, this has to be here because the entity is contructed client side with this */
    @SuppressWarnings("unused")
    public EntityDart(World world) {
        super(world);
        if(this.player!=null) {
            DartHandler.instance.getPhysicsEngine(this.getPlayer()).setDart(this, left);
        }
        //render the entity even if off screen
        this.ignoreFrustumCheck = true;
    }

    public EntityDart(EntityPlayer player, boolean left) {
        super(player.worldObj, player);
        this.player = player;
        this.left = left;
        //render the entity even if off screen
        this.ignoreFrustumCheck = true;
        Vec3 direction = player.getLookVec();
        double v_X = direction.xCoord*INITIAL_VELOCITY/20.0F;
        double v_Y = direction.yCoord*INITIAL_VELOCITY/20.0F;
        double v_Z = direction.zCoord*INITIAL_VELOCITY/20.0F;
        setVelocity(v_X, v_Y, v_Z);
    }

    @Override
    protected void onImpact(MovingObjectPosition position) {
        Vector impactPosition = new Vector(position.hitVec);
        double yaw = Math.atan2(impactPosition.getZ(), impactPosition.getX());
        double pitch = Math.asin(impactPosition.getY() / Math.sqrt(impactPosition.getX() * impactPosition.getX() + impactPosition.getZ() * impactPosition.getZ()));
        this.setLocationAndAngles(impactPosition.getX(), impactPosition.getY(), impactPosition.getZ(), (float) yaw, (float) pitch);
        this.hooked = true;
        setVelocity(0, 0, 0);
        DartHandler.instance.onDartAnchored(this);
    }

    public void setVelocity(double v_X, double v_Y, double v_Z) {
        this.motionX = v_X;
        this.motionY = v_Y;
        this.motionZ = v_Z;
    }

    @Override
    protected float getGravityVelocity() {
        return hooked?0:GRAVITY_VELOCITY/20;
    }

    /** returns the player that fired this dart */
    public EntityPlayer getPlayer() {
        return player;
    }

    /** check if this is the left or the right dart */
    public boolean isLeft() {
        return left;
    }

    /** check if this dart is grappled somewhere in a block */
    public boolean isHooked() {
        return this.hooked;
    }

    public double calculateDistanceToPlayer() {
        EntityPlayer player = this.getPlayer();
        return this.getPositionAsVector().substract(new Vector(player.posX, player.posY, player.posZ)).norm();
    }

    /** sets the length of the cable */
    public void setCableLength(double l) {
        cableLength = l<0?0:l>CABLE_LENGTH?CABLE_LENGTH:l;
    }

    /** gets the length of the cable */
    public double getCableLength() {
        return cableLength;
    }

    /** returns the coordinates of this entity in the form of a Vector */
    public Vector getPositionAsVector() {
        return new Vector(this.posX, this.posY, this.posZ);
    }

    @Override
    public void onUpdate() {
        //serverside, if this dart is not connected to a player, get rid of it
        if(!worldObj.isRemote && this.getPlayer()==null) {
            this.setDead();
            return;
        }
        //if the dart is hooked somewhere, it shouldn't do anything anymore
        if(this.hooked) {
            return;
        }
        //check for collision during movement this tick
        this.cableLength = this.calculateDistanceToPlayer();
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        this.onEntityUpdate();
        if (this.throwableShake > 0) {
            --this.throwableShake;
        }
        Vec3 vec3 = new Vec3(this.posX, this.posY, this.posZ);
        Vec3 vec31 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3, vec31);
        if (movingobjectposition != null) {
            if(movingobjectposition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
                this.retractDart();
            }
            this.onImpact(movingobjectposition);
            return;
        }
        //no collision detected, increase the components with the velocity
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        //check if the maximum cable length has been exceeded, and if so retract the dart
        if(this.cableLength > CABLE_LENGTH) {
            retractDart();
            return;
        }
        //cable length has not been exceeded: set previous values of position and rotation and decrement velocity due to friction
        float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
        for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f1) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F){}
        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }
        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }
        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float f2 = 0.99F;
        float f3 = this.getGravityVelocity();
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f4 = 0.25F;
                this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double)f4, this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX, this.motionY, this.motionZ);
            }
            f2 = 0.8F;
        }
        this.motionX *= (double)f2;
        this.motionY *= (double)f2;
        this.motionZ *= (double)f2;
        this.motionY -= (double)f3;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public void retractDart() {
        DartHandler.instance.retractDart(this.getPlayer(), this.left);
        this.setDead();
    }

    @Override
    public void setDead() {
        if(this.worldObj.isRemote) {
            PhysicsEngine engine = DartHandler.instance.getPhysicsEngine(player);
            engine.setDart(null, left);
            engine.onDartRetracted(left);
        }
        super.setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean(Names.NBT.LEFT, left);
        tag.setBoolean(Names.NBT.HOOKED, hooked);
        tag.setDouble(Names.NBT.LENGTH, cableLength);
        tag.setInteger(Names.NBT.PLAYER, player.getEntityId());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        left = tag.getBoolean(Names.NBT.LEFT);
        hooked = tag.getBoolean(Names.NBT.HOOKED);
        cableLength = tag.getDouble(Names.NBT.LENGTH);
        player = worldObj.getPlayerEntityByName(tag.getString(Names.NBT.PLAYER));
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeBoolean(this.left);
        data.writeBoolean(this.hooked);
        data.writeDouble(this.cableLength);
        data.writeInt(player.getEntityId());
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        this.left = data.readBoolean();
        this.hooked = data.readBoolean();
        this.cableLength = data.readDouble();
        Entity entity = worldObj.getEntityByID(data.readInt());
        if (entity instanceof EntityPlayer) {
            //Should always be the case
            this.player = (EntityPlayer) entity;
        }
        if (this.player != null) {
            DartHandler.instance.getPhysicsEngine(this.getPlayer()).setDart(this, left);
        }
    }


    public static class RenderFactory implements IRenderFactory<EntityDart> {
        private static final RenderFactory INSTANCE = new RenderFactory();

        public static RenderFactory getInstance() {
            return INSTANCE;
        }

        private RenderFactory() {}

        @Override
        @SideOnly(Side.CLIENT)
        public Render<? super EntityDart> createRenderFor(RenderManager manager) {
            return new RenderEntityDart(manager);
        }
    }
}
