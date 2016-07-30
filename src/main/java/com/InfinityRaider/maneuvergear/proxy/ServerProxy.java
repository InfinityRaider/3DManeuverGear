package com.InfinityRaider.maneuvergear.proxy;

import com.InfinityRaider.maneuvergear.init.EntityRegistry;
import com.infinityraider.infinitylib.proxy.IServerProxyBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy implements IServerProxyBase {
    @Override
    public void spawnSteamParticles(EntityPlayer player) {}

    @Override
    public void initEntities() {
        EntityRegistry.getInstance().serverInit();
    }

    @Override
    public void replacePlayerModel() {}
}
