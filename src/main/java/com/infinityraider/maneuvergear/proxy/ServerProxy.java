package com.infinityraider.maneuvergear.proxy;

import com.infinityraider.infinitylib.proxy.base.IServerProxyBase;
import com.infinityraider.maneuvergear.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy implements IServerProxyBase<Config>, IProxy {
    @Override
    public Function<ForgeConfigSpec.Builder, Config> getConfigConstructor() {
        return Config.Common::new;
    }
}
