package com.infinityraider.maneuvergear.registry;

import com.infinityraider.infinitylib.sound.SoundEventBase;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.reference.Names;
import net.minecraft.util.SoundEvent;

public class SoundRegistry {
    private static SoundRegistry INSTANCE = new SoundRegistry();

    public static SoundRegistry getInstance() {
        return INSTANCE;
    }

    public final SoundEvent soundEventRecord;

    private SoundRegistry() {
        this.soundEventRecord = ManeuverGear.instance.getConfig().disableMusicDisc() ? null :
                new SoundEventBase(ManeuverGear.instance.getModId(), Names.Sounds.RECORD) {
                    @Override
                    public boolean isEnabled() {
                        return !ManeuverGear.instance.getConfig().disableMusicDisc();
                    }
                };
    }
}
