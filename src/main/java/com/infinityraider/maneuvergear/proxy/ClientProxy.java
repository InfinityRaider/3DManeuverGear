package com.infinityraider.maneuvergear.proxy;

import com.infinityraider.maneuvergear.compat.CuriosCompatClient;
import com.infinityraider.maneuvergear.config.Config;
import com.infinityraider.maneuvergear.handler.KeyInputHandler;
import com.infinityraider.maneuvergear.handler.TooltipHandler;
import com.infinityraider.maneuvergear.physics.PhysicsEngine;
import com.infinityraider.maneuvergear.physics.PhysicsEngineClientLocal;
import com.infinityraider.maneuvergear.physics.PhysicsEngineDummy;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IClientProxyBase<Config>, IProxy {
    public static final KeyMapping KEY_RETRACT_LEFT = new KeyMapping(
            Reference.MOD_ID+"."+Names.Objects.KEY+"."+Names.Objects.RETRACT + "_" + Names.Objects.LEFT,
            GLFW.GLFW_KEY_Z,
            "key.categories.movement");
    public static final KeyMapping KEY_RETRACT_RIGHT = new KeyMapping(
            Reference.MOD_ID+"."+Names.Objects.KEY+"."+Names.Objects.RETRACT + "_" + Names.Objects.RIGHT,
            GLFW.GLFW_KEY_X,
            "key.categories.movement");

    @Override
    public void onClientSetupEvent(final FMLClientSetupEvent event) {
        // Register key bindings
        ClientRegistry.registerKeyBinding(KEY_RETRACT_LEFT);
        ClientRegistry.registerKeyBinding(KEY_RETRACT_RIGHT);
        // Register Curios renderer
        CuriosCompatClient.init();
    }

    @Override
    public PhysicsEngine createPhysicsEngine(PlayerEntity player) {
        if(player == null || !player.getEntityWorld().isRemote) {
            return new PhysicsEngineDummy();
        }
        PlayerEntity local = getClientPlayer();
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
    public Function<ForgeConfigSpec.Builder, Config> getConfigConstructor() {
        return Config.Client::new;
    }

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        this.registerEventHandler(KeyInputHandler.getInstance());
        this.registerEventHandler(TooltipHandler.getInstance());
    }

    @Override
    public boolean isShiftPressed() {
        return Minecraft.getInstance().options.keyShift.isDown();
    }
}
