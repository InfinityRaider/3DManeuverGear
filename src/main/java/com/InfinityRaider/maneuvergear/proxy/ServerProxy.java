package com.InfinityRaider.maneuvergear.proxy;

import com.InfinityRaider.maneuvergear.init.EntityRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

@SuppressWarnings("unused")
@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
    @Override
    public EntityPlayer getClientPlayer() {
        return null;
    }

    @Override
    public World getClientWorld() {
        return null;
    }

    @Override
    public World getWorldByDimensionId(int dimension) {
        return FMLServerHandler.instance().getServer().worldServerForDimension(dimension);
    }

    @Override
    public void spawnSteamParticles(EntityPlayer player) {

    }

    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();
    }

    @Override
    public void initEntities() {
        EntityRegistry.getInstance().serverInit();
    }

    @Override
    public void replacePlayerModel() {

    }
}
