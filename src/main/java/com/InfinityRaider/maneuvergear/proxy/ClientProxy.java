package com.InfinityRaider.maneuvergear.proxy;

import com.InfinityRaider.maneuvergear.entity.EntityDart;
import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.handler.KeyInputHandler;
import com.InfinityRaider.maneuvergear.handler.MouseClickHandler;
import com.InfinityRaider.maneuvergear.init.Items;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngine;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngineClientLocal;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngineDummy;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.reference.Reference;
import com.InfinityRaider.maneuvergear.render.*;
import com.InfinityRaider.maneuvergear.render.model.ModelBipedModified;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    public static KeyBinding retractLeft = new KeyBinding(Reference.MOD_ID+"."+Names.Objects.KEY+"."+Names.Objects.RETRACT+Names.Objects.LEFT, Keyboard.KEY_Z, "key.categories.movement");
    public static KeyBinding retractRight = new KeyBinding(Reference.MOD_ID+"."+Names.Objects.KEY+"."+Names.Objects.RETRACT+Names.Objects.RIGHT, Keyboard.KEY_X, "key.categories.movement");

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public PhysicsEngine createPhysicsEngine(EntityPlayer player) {
        if(player == null || !player.worldObj.isRemote) {
            return new PhysicsEngineDummy();
        }
        EntityPlayer local = getClientPlayer();
        if(local == null) {
            //This only happens during first startup of an SSP world
            return new PhysicsEngineClientLocal(player);
        }
        if(local.getUniqueID().equals(player.getUniqueID())) {
            //Happens during equipping of maneuver gear in an SSP or SMP world, a second SSP world startup or when a LAN player joins a host
            return new PhysicsEngineClientLocal(player);
        }
        else {
            //Happens when a LAN player joins an SSP world
            return new PhysicsEngineDummy();
        }
    }

    @Override
    public void spawnSteamParticles(EntityPlayer player) {
        World world = player.worldObj;
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        Vec3 lookVec = player.getLook(2);
        int nr = 10;
        for(int i=0;i<nr;i++) {
            EntityCloudFX particle = new EntityCloudFX(world, x, y, z, -(lookVec.xCoord*i)/(0.0F+nr), -(lookVec.yCoord*i)/(0.0F+nr), -(lookVec.zCoord*i)/(0.0F+nr));
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        super.initConfiguration(event);
        ConfigurationHandler.initClientConfigs(event);
    }

    @Override
    public World getWorldByDimensionId(int dimension) {
        Side effectiveSide = FMLCommonHandler.instance().getEffectiveSide();
        if(effectiveSide == Side.SERVER) {
            return FMLClientHandler.instance().getServer().worldServerForDimension(dimension);
        } else {
            return getClientWorld();
        }
    }

    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();

        MinecraftForge.EVENT_BUS.register(MouseClickHandler.getInstance());
        FMLCommonHandler.instance().bus().register(MouseClickHandler.getInstance());

        MinecraftForge.EVENT_BUS.register(KeyInputHandler.getInstance());
        FMLCommonHandler.instance().bus().register(KeyInputHandler.getInstance());

        RenderSecondaryWeapon renderSecondaryWeapon = new RenderSecondaryWeapon();
        MinecraftForge.EVENT_BUS.register(renderSecondaryWeapon);
        FMLCommonHandler.instance().bus().register(renderSecondaryWeapon);

        MinecraftForge.EVENT_BUS.register(RenderBauble.getInstance());
        FMLCommonHandler.instance().bus().register(RenderBauble.getInstance());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerRenderers() {
        //items
        MinecraftForgeClient.registerItemRenderer(Items.itemManeuverGearHandle, RenderItemHandle.instance);
        MinecraftForgeClient.registerItemRenderer(Items.itemManeuverGear, RenderManeuverGear.instance);

        //entities
        RenderingRegistry.registerEntityRenderingHandler(EntityDart.class, new RenderEntityDart());

        //player rendering
        ModelBipedModified.replaceOldModel();
    }

    @Override
    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(retractLeft);
        ClientRegistry.registerKeyBinding(retractRight);
    }
}
