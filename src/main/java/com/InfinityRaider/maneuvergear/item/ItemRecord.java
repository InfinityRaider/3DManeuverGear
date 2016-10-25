package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.ManeuverGear;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.reference.Reference;
import com.infinityraider.infinitylib.item.IItemWithModel;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MethodsReturnNonnullByDefault
public class ItemRecord extends net.minecraft.item.ItemRecord implements IItemWithModel {
    public ItemRecord(String name) {
        super(name, registerSoundAndCreateRecord(name));
    }

    @Override
    public String getInternalName() {
        return Names.Objects.RECORD;
    }

    @Override
    public List<String> getOreTags() {
        return Collections.emptyList();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase(), name);
    }

    @Override
    public List<Tuple<Integer, ModelResourceLocation>> getModelDefinitions() {
        List<Tuple<Integer, ModelResourceLocation>> list = new ArrayList<>();
        list.add(new Tuple<>(0, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":record", "inventory")));
        return list;
    }

    private static SoundEvent registerSoundAndCreateRecord(String name) {
        ResourceLocation loc = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "records." + name);
        SoundEvent sound = new SoundEvent(loc);
        for(Method method : SoundEvent.class.getDeclaredMethods()) {
            if(Modifier.isStatic(method.getModifiers()) && method.getParameterCount() > 0) {
                method.setAccessible(true);
                try {
                    method.invoke(null, loc.toString());
                } catch (Exception e) {
                    ManeuverGear.instance.getLogger().printStackTrace(e);
                }
                break;
            }
        }
        return sound;
    }
}
