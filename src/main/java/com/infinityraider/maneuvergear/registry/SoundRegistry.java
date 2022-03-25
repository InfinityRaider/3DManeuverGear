package com.infinityraider.maneuvergear.registry;

import com.infinityraider.infinitylib.sound.SoundEventBase;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.reference.Names;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {
    public static final SoundEvent soundEventRecord = ManeuverGear.instance.getConfig().disableMusicDisc() ? null :
            new SoundEventBase(ManeuverGear.instance.getModId(), Names.Sounds.RECORD) {
                @Override
                public boolean isEnabled() {
                    return !ManeuverGear.instance.getConfig().disableMusicDisc();
                }
            };
}
