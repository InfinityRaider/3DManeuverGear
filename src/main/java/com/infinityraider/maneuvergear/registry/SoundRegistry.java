package com.infinityraider.maneuvergear.registry;

import com.infinityraider.infinitylib.sound.SoundEventBase;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.reference.Names;

import javax.annotation.Nullable;

public final class SoundRegistry extends ModContentRegistry {
    private static final SoundRegistry INSTANCE = new SoundRegistry();

    public static SoundRegistry getInstance() {
        return INSTANCE;
    }

    @Nullable
    private final RegistryInitializer<SoundEventBase> musicDisk;

    private SoundRegistry() {
        this.musicDisk = ManeuverGear.instance.getConfig().disableMusicDisc() ? null : this.sound(() ->
                new SoundEventBase(ManeuverGear.instance.getModId(), Names.Sounds.RECORD) {
                    @Override
                    public boolean isEnabled() {
                        return !ManeuverGear.instance.getConfig().disableMusicDisc();
                    }
                }
        );
    }

    @Nullable
    public SoundEventBase getMusicDiskSound() {
        return this.musicDisk == null ? null : this.musicDisk.get();
    }
}
